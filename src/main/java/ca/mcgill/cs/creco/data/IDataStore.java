/**
 * Copyright 2014 McGill University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.mcgill.cs.creco.data;

import java.util.Collection;

/**
 * Provides methods to access the data from the database.
 */
public interface IDataStore 
{
	
	/**
	 * Gets a category from the database.
	 * @param pId The category's ID.
	 * @return The category object.
	 */
	Category getCategory(String pId);
	
	/**
	 * Returns a product from the database.
	 * @param pId The id of the product.
	 * @return The product with id pId
	 */
	Product getProduct(String pId);	
	
	/**
	 * Gets all categories from the database.
	 * @return An iterable set of all the categories.
	 */
	Collection<Category> getCategories();
	
	/**
	 * Gets all products from the database.
	 * @return An iterable set of all the products.
	 */
	Collection<Product> getProducts();
}
