import java.lang.*;
/**
 * Class to hold an object Integer and Character to save data when reading from a file
 * 
 * @author Sanjana Goli and Rohith Mandavilli
 *
 */
public class CharIntNode implements Comparable<CharIntNode> {
	private Integer integer;		//frequency at which character repeats
	private Character character;	//the actual character that repeats in a file
	
	
	/**
	 * have different constructors for different nodes of the trees
	 */
	
	
	/**
	 * when character and integer values are both available
	 * @param character
	 * @param integer
	 */
	public CharIntNode(Character character, Integer integer) {
		this.integer = integer;
		this.character = character;
	}
	
	/**
	 * regular tree -  only have the frequency of the two nodes
	 * @param integer
	 */
	public CharIntNode(Integer integer) {
		this.integer = integer;
		this.character = null;
	}
	/**
	 * Extra credit tree - only need character for each node
	 * @param character 
	 */
	public CharIntNode(Character character) {
		this.integer = null;
		this.character = character;
	}
	
	/**
	 * Implemented compareTo method - compares integer values of different charIntNodes
	 */
	public int compareTo(CharIntNode node2) {
		if(integer < node2.integer) return -1;
		else if(integer == node2.integer) return 0;
		else return 1;
	}
	
	public Integer getInt() {
		return integer;
	}
	
	public Character getChar() {
		return character;
	}
	
	@Override
	public String toString() {
		return character + " " + integer;
	}
}
