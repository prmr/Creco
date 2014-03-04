package ca.mcgill.cs.creco.logic.search;

import java.util.List;

import ca.mcgill.cs.creco.data.Category;

/**
 * Searches the database for equivalence classes.
 */
public interface ICategorySearch
{

	/**
	 * Searches the database for matching equivalence classes (or categories).
	 * @param pQueryString The query string.
	 * @return A list of categories whose name or products match the query string.
	 */
	List<Category> queryCategories(String pQueryString);
}
