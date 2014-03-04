package ca.mcgill.cs.creco.logic.search;

import java.util.List;

import ca.mcgill.cs.creco.data.Category;

public interface ICategorySearch {

	/**
	 * Searches the database for matching equivalence classes (or categories).
	 * @param queryString The query string.
	 * @return A list of categories whose name or products match the query string.
	 */
	List<Category> queryCategories(String queryString);
}
