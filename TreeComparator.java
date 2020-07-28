import java.util.Comparator;
/**
 * 
 * Class created solely to compare trees
 * 
 * @author Rohith Mandavilli and Sanjana Goli
 *
 */

public class TreeComparator implements Comparator<BinaryTree<CharIntNode>>{
	/**
	 * 
	 * @param takes in tree c1 c2 of type charintnode
	 * @return returns -1 if c1 is less than c2, 0 if they are equal, and 1 if c1 is greater
	 */
	public int compare(BinaryTree<CharIntNode> c1, BinaryTree<CharIntNode> c2) {
		if(c1.getData().getInt() < c2.getData().getInt()) return -1;
		else if(c1.getData().getInt() == c2.getData().getInt()) return 0;
		else return 1;
	}
}