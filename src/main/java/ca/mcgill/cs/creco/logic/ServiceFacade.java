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
package ca.mcgill.cs.creco.logic;

import java.util.Collection;
import java.util.List;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;

/**
 * Single point of access for all services of domain 
 * objects. Implements Fowler's Service Layer pattern.
 */
public interface ServiceFacade 
{
	/**
	 * Returns a number of possible completions for the input string.
	 * Completions are based on product categories, product names, or 
	 * product brands. 
	 * @param pInput The input text to complete.
	 * @return A comma-separated string of potential completions.
	 */
	String getCompletions(String pInput);
	
	/**
	 * Return all categories that somehow match pQuery. 
	 * Only categories with at least one product are returned.
	 * @param pQuery The input query
	 * @return A collection of Category objects representing categories that match pQuery.
	 */
	Collection<Category> searchCategories(String pQuery);
	
	/**
	 * Returns a category given its id.
	 * @param pId The id of the category to obtain.
	 * @return The corresponding category.
	 */
	Category getCategory(String pId);
	
	/**
	 * Ranks a collection of products according to a given set of attributes.
	 * @param pScoredAttributes The set of attributes used to rank the products.
	 * @param pProducts The collection of products to rank.
	 * @return The ranked list of products, ordered from highest to lowest score.
	 */
	List<Product> rankProducts(List <ScoredAttribute> pScoredAttributes, Collection<Product> pProducts);
	
}
