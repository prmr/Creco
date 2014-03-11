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
	Category getCategory(String pIndex);
	
	/**
	 * Gets a category from the database.
	 * @param pIndex The category's ID.
	 * @return The category object.
	 */
	Category2 getCategory2(String pIndex);
	
	/**
	 * Gets the set of all equivalence classes in the database.
	 * @return An iterable set of all the equivalence classes.
	 */
	Iterable<Category> getEquivalenceClasses();
	
	/**
	 * Gets all categories from the database.
	 * @return An iterable set of all the categories.
	 */
	Collection<Category2> getCategories();
	
	/**
	 * Gets all products from the database.
	 * @return An iterable set of all the products.
	 */
	Iterator<Product> getProducts();
}
