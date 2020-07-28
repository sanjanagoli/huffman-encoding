import java.io.*;
import java.util.*;
import java.lang.*;
/**
 * 
 * @author Sanjana Goli and Rohith Mandavilli 
 *
 */
public class FrequencyMeasureExtra extends FrequencyMeasure{
	/**
	 * constructs each instance variable 
	 */
	public FrequencyMeasureExtra() {
		super();
	}
	
	public static void main(String[] args) throws Exception 
	{
		FrequencyMeasureExtra fq = new FrequencyMeasureExtra();
		//try-catch block to prevent java crashes from file not found and null pointer exception
		
		try 
		{
			BufferedBitWriter bitOutput = new BufferedBitWriter(compressedFile);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			fq.addToPQ();			//calls filenotfound because uses input file variable
			fq.codeRetrieval();		//calls null pointer when codemap is empty (usually because there is no file)
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

	public void compression(String compressedFile, String inputFile) throws IOException {
		//creates reader for reading from input file, and bitOutput to write out bits to a compressed file
		BufferedBitWriter bitOutput = new BufferedBitWriter(compressedFile);
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
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
	/**
	 * recursively compressed the tree into binary
	 * sets each ascii number to a 10 digit number, where the first digit represents the value of hasLeft and the second digit
	 * represents the value of hasRight
	 * first two digits in each 10 digit clump tells us whether the next charintnode should be a leaf or an inner node and where it should be
	 * adding zeros to the beginning of a binary number doesnt change the binary value itself
	 * @param tree
	 * @return
	 */
	private String preOrderTraversal(BinaryTree<CharIntNode> tree)
	{
		String s = "";
		
		if(tree.getData().getChar() == null)			//create a String of just binary, and data is from the test file	
		{
			if(tree.hasLeft() == true)s += "1";
			else s+= "0";
			
			if(tree.hasRight() == true)s += "1";
			else s += "0";
			s += "00000000";
		}
		else if(tree.getData().getChar() != null)
		{
			int ascii;
			ascii = (char) tree.getData().getChar(); //char cast returns the ascii value for the character
			String temp = Integer.toBinaryString(ascii); //turns the ascii value into a binary string
			if(temp.length()<8) //'0' is added at front if the character is less than 8 digits to make a 10 digit binary
			{
				int needToAdd = 8-temp.length();
				for(int i = 0; i<needToAdd; i++)
					s += "0";
			}
			s += "00" + Integer.toBinaryString(ascii);

		}
		//recursion!
		if(tree.hasLeft()) s = s + preOrderTraversal(tree.getLeft());
		if(tree.hasRight()) s = s + preOrderTraversal(tree.getRight());
		
		return s;
	}
	/**
	 * compresses the tree into bits - same structure as frequency measure, just for a different string
	 */
	public void compression(BufferedBitWriter bitOutput, BufferedReader reader) throws IOException
	{
		String s = preOrderTraversal(pQueue.peek()); //calls traversal to make string we can compress
		for(int i = 0; i<s.length(); i++) 		//compressed actual string, by writing bits
		{
			String bit = s.substring(i, i+1);
			if(bit.equals("0"))
			{
				bitOutput.writeBit(false);
			}
			else if(bit.equals("1"))
			{
				bitOutput.writeBit(true);
			}
		}
		//at this point the tree should be written to the file
		//now we can compress the actual text in the testfile by calling the super of this class which handles that functionality
		super.compression(bitOutput, reader);
	}

	/**
	 * helper method to recreate tree
	 * need to create a new method because it uses recursion
	 * bufferedbitreader/writer are passed in to keep the same reader/writer - avoids rereading the file from the
	 * very beginning when making a new reader/writer
	 * 
	 * @param bitInput 
	 * @param output
	 * @return
	 * @throws IOException
	 */
	private BinaryTree<CharIntNode> decompressRecursion(BufferedBitReader bitInput, BufferedWriter output) throws IOException
	{	
		boolean bit1 = bitInput.readBit(); //first digit
		boolean bit2 = bitInput.readBit(); //second digit
		
		String s = "";
		for (int i = 0; i < 8; i++) {		//gets the character through digits 2-10
			if (bitInput.readBit()) {
				s+= "1";
			}
			else {
				s+= "0";
			}
		}
		
		char c = (char) Integer.parseInt(s, 2); //turn the substring into the decimal version
	
		BinaryTree<CharIntNode> tree = new BinaryTree<CharIntNode>(new CharIntNode(c));
		if(bit1 == true) //hasleft
		{
			tree.setLeft(decompressRecursion(bitInput, output));
		}
		if(bit2 == true) //hasright
		{
			tree.setRight(decompressRecursion(bitInput, output));
		}
		
		
		return tree;
		
	}
	/**
	 * actual decompression - calls super to deal with actual characters in the test file
	 */
	public void decompression(BufferedBitReader bitInput, BufferedWriter output) throws IOException
	{
		BinaryTree<CharIntNode> tree = decompressRecursion(bitInput, output);
		System.out.println(tree);
		super.decompression(bitInput, output);

	}
	

}
