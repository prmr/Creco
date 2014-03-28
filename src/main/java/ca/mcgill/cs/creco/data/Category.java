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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Represents a category in the product database.
 */
public class Category 
{
	// Fields copied directly from CR database
	private String aId;
	private String aSingularName;
	private String aRootCategoryName; 
	private ArrayList<Product> aProducts = new ArrayList<Product>();	
	
	/**
	 * TODO Make package-private.
	 * @param pId z
	 * @param pSingularName z
	 * @param pRootCategory z
	 * @param pProducts z
	 * @param pSpecifications z
	 * @param pRatings z
	 */
	public Category(String pId, String pSingularName, String pRootCategory, Collection<Product> pProducts)
	{
		aId = pId;
		aSingularName = pSingularName;
		aRootCategoryName = pRootCategory;
		aProducts.addAll(pProducts);
	}
	
	/**
	 * @return The name of the franchise for this category.
	 */
	public String getFranchise()
	{
		return aRootCategoryName;
	}
	
	/**
	 * @return The number of products in the category.
	 */
	public int getNumberOfProducts()
	{
		return aProducts.size();
	}
	
	
	/**
	 * @return The products associated with this category.
	 */
	public Collection<Product> getProducts()
	{
		return Collections.unmodifiableCollection(aProducts);
	}
	
	/**
	 * @return The id of this category.
	 */
	public String getId() 
	{
		return aId;
	}
	
	/**
	 * @return The singular name of this category.
	 */
	public String getName() 
	{
		return aSingularName;
	}
}

