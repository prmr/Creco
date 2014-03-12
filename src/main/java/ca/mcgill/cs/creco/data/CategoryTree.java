package ca.mcgill.cs.creco.data;

/**
 * An object of this class is used to collect category nodes 
 * organized in a tree structure and process them to flatten the 
 * category tree into a set of meaning full product categories.
 * 
 * Using instances of this class requires following this protocol:
 * <ol>
 * <li>Calling <tt>addNode</tt></li>
 * <li>Calling the indexing and processing methods</li>
 * <li>Obtaining the list of categories</li>
 * </ol>
 */
public class CategoryTree
{
	/**
	 * Adds a category node to this tree.
	 * @param pNode The node to add.
	 */
	public void addNode(CategoryNode pNode)
	{}
}
