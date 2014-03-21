package ca.mcgill.cs.creco.logic.search;

import java.util.List;
import ca.mcgill.cs.creco.data.Product;

/**
 * Searches the database for products.
 */
public interface IProductSearch
{
	
	/**
	 * Returns only the products within the
	 * equivalence class which match the query string (alphabetically).
	 * @param pQueryString The string to match against product names and other fields.
	 * @param pCategoryID The ID of the equivalence class within which the search will focus.
	 * @return A list of ScoredProducts arranged in alphabetical order
	 */
	
	
	List<Product> returnProductsAlphabetically(String pCategoryID);
	
	
}