package ca.mcgill.cs.creco.logic.search;

import java.util.List;

/**
 * Searches the database for products.
 */
public interface IProductSearch
{
	
	/**
	 * Searches the database for matching products and returns only the products within the
	 * equivalence class which match the query string.
	 * @param pQueryString The string to match against product names and other fields.
	 * @param pCategoryID The ID of the equivalence class within which the search will focus.
	 * @return A list of ScoredProducts, sorted from highest score (matches the query string
	 * the most) to lowest. Products which do not match at all (score of 0) are not returned.
	 */
	List<ScoredProduct> queryProducts(String pQueryString, String pCategoryID);
	
	/**
	 * Searches the database for matching products but returns all products within the searched
	 * equivalence class, even if they do not match the query string.
	 * @param pQueryString The string to match against product names and other fields.
	 * @param pCategoryID The ID of the equivalence class within which the search will focus.
	 * @return A list of ScoredProducts, sorted from highest score (matches the query string
	 * the most) to lowest. Products which do not match at all are returned with a score of 0.
	 */
	List<ScoredProduct> queryProductsReturnAll(String pQueryString, String pCategoryID);
}
