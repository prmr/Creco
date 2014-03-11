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

/**
 * Represents a category in the product database.
 */
public class Category2 
{
	// Fields copied directly from CR database
	private String aId;
	private String aSingularName;
	private Category2 aRootCategory; 
	private ArrayList<Product> aProducts = new ArrayList<Product>();	
	private ArrayList<AttributeStat> aSpecifications = new ArrayList<AttributeStat>();
	private ArrayList<AttributeStat> aRatings = new  ArrayList<AttributeStat>();
	
	/**
	 * Creates a new Category initialized only with the parameter fields.
	 * @param pId The Category ID
	 * @param pSingularName The singular name of the category.
	 * @param pRootCategory The category's parent, or null if this is a root category.
	 */
	Category2(String pId, String pSingularName, Category2 pRootCategory, 
			  ArrayList<Product> pProducts, ArrayList<AttributeStat> pSpecifications, ArrayList<AttributeStat> pRatings)
	{
		aId = pId;
		aSingularName = pSingularName;
		aRootCategory = pRootCategory;
		aProducts.addAll(pProducts);
		aSpecifications.addAll(pSpecifications);
		aRatings.addAll(pRatings);
	}
	
	/**
	 * @return A collection of ratings associated with this category.
	 */
	public Collection<AttributeStat> getRatings()
	{
		return Collections.unmodifiableCollection(aRatings);
	}
	
	/**
	 * @return A collection of specifications associated with this category.
	 */
	public Collection<AttributeStat> getSpecifications()
	{
		return Collections.unmodifiableCollection(aSpecifications);
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
	
	/**
	 * @return The root category for this category.
	 */
	public Category2 getRoot()
	{
		return aRootCategory;
	}
}

