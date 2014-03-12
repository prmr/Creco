package ca.mcgill.cs.creco.data;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides methods to access the data from the database.
 */
public interface IDataStore 
{
	
	/**
	 * Gets a category from the database.
	 * @param pIndex The category's ID.
	 * @return The category object.
	 */
	CategoryBuilder getCategory(String pIndex);
	
	/**
	 * Gets a category from the database.
	 * @param pIndex The category's ID.
	 * @return The category object.
	 */
	Category getCategory2(String pIndex);
	
	/**
	 * Gets the set of all equivalence classes in the database.
	 * @return An iterable set of all the equivalence classes.
	 */
	Iterable<CategoryBuilder> getEquivalenceClasses();
	
	/**
	 * Gets all categories from the database.
	 * @return An iterable set of all the categories.
	 */
	Collection<Category> getCategories();
	
	/**
	 * Gets all products from the database.
	 * @return An iterable set of all the products.
	 */
	Iterator<Product> getProducts();
}
