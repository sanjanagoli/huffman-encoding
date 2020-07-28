import java.io.*;
import java.util.*;
import java.lang.*;
/**
 * 
 * @author Sanjana Goli and Rohith Mandavilli
 *
 */
public class FrequencyMeasure {
	private Map<Character, Integer> freq;						//keeps track of the characters and the times they repeat
	protected PriorityQueue<BinaryTree<CharIntNode>> pQueue;		//hold all character nodes to eventually combine into a tree
	protected Map<Character, String> codeMap;						//holds tree path for each character (0 = left, 1 = right)
	private ArrayList<String> pathList;							//used in decompression to hold and save paths
	private Character c;										//used in charfreq/addtopq to iter and add characters
	protected static final String inputFile = "inputs/USConstitution.txt";	//input file used to compress/decompress
	protected static String compressedFile = inputFile.substring(0, inputFile.indexOf(".txt")) + "_compressed" + ".txt";		//new compressed file name - dependent on input
	protected static String decompressedFile = inputFile.substring(0, inputFile.indexOf(".txt")) + "_decompressed" + ".txt";	//new decompressed file name - dependent on input
	
	/**
	 * constructs each instance variable 
	 */
	public FrequencyMeasure() {
		freq = new HashMap<Character, Integer>();
		Comparator<BinaryTree<CharIntNode>> treeCompare = new TreeComparator();
		pQueue = new PriorityQueue<BinaryTree<CharIntNode>>(treeCompare);
		codeMap = new HashMap<Character, String>();
		pathList = new ArrayList<String>();
		c = null;
	}
	
	public static void main(String[] args) throws Exception 
	{
			FrequencyMeasure fq = new FrequencyMeasure();
			//try-catch block to prevent java crashes from file not found and null pointer exception
			
			try 
			{
				fq.addToPQ();			//calls filenotfound because uses input file variable
				fq.codeRetrieval();		//calls null pointer when codemap is empty (usually because there is no file)
				BufferedBitWriter bitOutput = new BufferedBitWriter(compressedFile);
				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter output = new BufferedWriter(new FileWriter(decompressedFile)); 
				fq.compression(bitOutput, reader);				//turns input file into compressed file
				BufferedBitReader bitInput = new BufferedBitReader(compressedFile);
				fq.decompression(bitInput, output);		//turns compressed file into decompressed file
				//note: decompressed file should have (and does) the same text as the input file
			}
			catch(FileNotFoundException e)
			{
				System.out.println(e);
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
	}
			
	public void charFrequency(String inputFile) throws Exception{
		//creates a reader to get text in input file
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));	
		int charac;
		//reads the text file in by character
		while ((charac = reader.read()) != -1) {
			Character c = new Character((char)charac);
			//checks if the character is already in the map
			//if not, add new key; if it is, update value
			if(!freq.containsKey(c)) {
				freq.put(c, 1);
			} else {
				freq.replace(c, new Integer(freq.get(c)+1));
			}
		}
		
		reader.close();
 
	}
	/**
	 * adds each CharInt in the frequency map to a new CharInt node in a priority queue to be later construced into 1 tree
	 */
	public void addToPQ() throws Exception {
		//generate frequency map with characters and repetitions
		charFrequency(inputFile);
		//get all keys to evenutally iterate through them
		Set<Character> keySet = freq.keySet();
		if(keySet.size() == 0) {
			throw new IOException();
		} else {
		Iterator<Character> iter = keySet.iterator();
		//iterates through set of keys in frequency map
		while(iter.hasNext()) {

			Character c = iter.next();
			//creates new BinaryTree node for priority queue of type CharIntNode
			CharIntNode cIN = new CharIntNode(c, freq.get(c));
			BinaryTree<CharIntNode> bCIN = new BinaryTree<CharIntNode>(cIN);
			pQueue.add(bCIN);
		}
		}
	}
	/**
	 * takes pq nodes and turns into a singular tree
	 */
	public void treeCreator() {
		//while pq has more than one element
		
		while(pQueue.size() > 1) {
			//take the first two elements of pq 
			BinaryTree<CharIntNode> b1 = pQueue.peek();
			pQueue.remove(b1);
			BinaryTree<CharIntNode> b2 = pQueue.peek();
			pQueue.remove(b2);
			//create a new tree with parent as total freq and add children as old nodes
			BinaryTree<CharIntNode> newTree = new BinaryTree<CharIntNode>(new CharIntNode(b1.getData().getInt()+b2.getData().getInt()));
			newTree.setLeft(b1);
			newTree.setRight(b2);
			//add that tree back into the priority queue with order
			pQueue.add(newTree);
			
		}
	}
	
	
	public void codeRetrieval() throws Exception {
		//while the count is less than the number of leaves
		//if has left is true add 0; if has right is true add 1
		//if isleaf of left or right is true and character is not null in the node, setLeft or setRight to null
		treeCreator();
		
		//if file is empty, throw exception
		
		accumulator(codeMap, pQueue.peek(), "");
		//handles when file contains only one letter or one letter repeated several times
		if(codeMap.size() == 1) {
			codeMap.replace(pQueue.peek().getData().getChar(), "1");
		}
		
		
		
		
	}
	/**
	 * gets codepath for each character in the tree, added to a map
	 * @param map Map that will contain the code paths
	 * @param tree Tree with the keys that will be recursively gone through
	 * @param path	Collection of "0"s and "1"s to denote the path of the character in the tree. 0 = left, 1  = right.
	 */
	private void accumulator(Map<Character, String> map, BinaryTree<CharIntNode> tree, String path) {
		if (tree.isLeaf()) {
			//if the node is a leaf add it to the map of the character and code map
			map.put(tree.getData().getChar(), path);
			return;
		}
		//keep recursing until a leaf is reached
		if(tree.hasLeft()) {
			accumulator(map, tree.getLeft(), path + '0');
		}
		
		if(tree.hasRight()) {
			accumulator(map, tree.getRight(), path + '1');
		}
	}
	
	public void compression(BufferedBitWriter bitOutput, BufferedReader reader) throws IOException {
		//creates reader for reading from input file, and bitOutput to write out bits to a compressed file

		//reads in each character
		int charac;
		while ((charac = reader.read()) != -1) {		//while a valid character
			Character c = new Character((char)charac);
			String path = codeMap.get(c);
			//goes through each 0 and 1 in the code path by using substring
			//creates a bit and determines whether the bit is true or false
			//outputs to the compression file
			for(int i = 0; i<path.length();i++)
			{
				String cut = path.substring(i, i+1);
				boolean bit;
				if(cut.equals("0")) bit = false;
				else bit = true;
				bitOutput.writeBit(bit);
			}
		}
		reader.close();
		bitOutput.close();
	}
	
	
	public void decompression(BufferedBitReader bitInput, BufferedWriter output) throws IOException
	{	
 
		String path = "";
		//reads through bit in file
		while (bitInput.hasNext()) {
			//add 0s and 1s to path string depending on bit value
			boolean bit = bitInput.readBit();
			if(bit == false) {
				path += "0";
			} else if (bit == true) {
				path += "1";
			}
			//if the code map contains the value, then add it to a list and reset path
			if(codeMap.containsValue(path)) {
				pathList.add(path);
				path = "";

			}
		}
		//iterate through the list of path codes and find + write out the corresponding character key
		for(String p: pathList)
		{
			findKey(p, pQueue.peek());
			output.write(c);
		}
		output.close();

	}
	
	//traverse through the tree of characters to find the character that corresponds to the code path
	private void findKey(String str, BinaryTree<CharIntNode> tree) {
		//base case 
		if(tree.isLeaf() == true) c = tree.getData().getChar();
		String cut = "";
		if(str.length() != 0)
		{
			cut = str.substring(0, 1);
		}
		//if next codepath element is 0, traverse through left tree
		if(cut.equals("0"))
		{
			if(tree.hasLeft())
			{
				findKey(str.substring(1), tree.getLeft());
			}
		}
		//exact opposite - if element is 1, go to the right side
		else if(cut.equals("1"))
		{
			if(tree.hasRight())
			{
				findKey(str.substring(1), tree.getRight());
			}
		}
				
	}
	
	

}
