package ca.mcgill.cs.creco.logic.search;

import java.util.List;

import ca.mcgill.cs.creco.data.Category2;

/**
 * Searches the database for categories.
 */
public interface ICategorySearch
{

	/**
	 * Searches the database for matching categories (or equivalence classes).
	 * @param pQueryString The string to match against category names and other fields.
	 * @return A list of categories whose name or products match the query string.
	 */
	List<Category2> queryCategories(String pQueryString);
}
