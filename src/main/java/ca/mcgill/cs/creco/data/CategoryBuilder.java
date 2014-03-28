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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.StringUtils;

/**
 * Represents a category in the product database.
 */
public class CategoryBuilder 
{
	private static final int PERCENT = 100;
	
	// Fields copied directly from CR database
	private String aId;
	private String aSingularName;
	
	// Derived fields set in constructor
	private int aCount;
	private int aRatedCount;
	private int aTestedCount;
	private CategoryBuilder aParent; 
	
	// Derived fields set by associateProducts()
	private Double aJaccardIndex;
	private ArrayList<CategoryBuilder> aChildren = new ArrayList<CategoryBuilder>();
	private ArrayList<Product> aProducts = new ArrayList<Product>();	
	private HashSet<String> aAttributeUnion = new HashSet<String>();
	private HashSet<String> aAttributeIntersection = new HashSet<String>();
	
	/**
	 * Creates a new Category initialized only with the parameter fields.
	 * @param pId The Category ID
	 * @param pSingularName The singular name of the category.
	 * @param pParent The category's parent, or null if this is a root category.
	 */
	public CategoryBuilder(String pId, String pSingularName, CategoryBuilder pParent)
	{
		aId = pId;
		aSingularName = pSingularName;
		aParent = pParent;
	}
	
	/**
	 * @return The immutable Category object representing this category.
	 */
	public Category getCategory()
	{
		return new Category(aId, aSingularName, getRootCategoryName(), aProducts);
	}
	
	//--- Public Method ---
	
	/**
	 * @return The Jaccard index of the Attributes contained in this category
	 */
	public Double getJaccardIndex()
	{
		return aJaccardIndex;
	}
			
	/**
	 * @return The products associated with this category.
	 */
	public Iterable<Product> getProducts()
	{
		return Collections.unmodifiableCollection(aProducts);
	}
	
	/**
	 * @return The count for this category.
	 */
	public int getCount()
	{
		return aCount;
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
	 * Adds a subcategory to this category.
	 * @param pSubcategory the subcategory to add.
	 */
	public void addSubcategory(CategoryBuilder pSubcategory)
	{
		aChildren.add(pSubcategory);
	}
	
	@Override
	public String toString()
	{
		String description = "";
		description += aSingularName + " (\"" + aId + "\")\n";
		description += " - count: " + aCount + "\n";
		description += " - ratedCount: " + aRatedCount + "\n";
		description += " - testedCount: " + aTestedCount + "\n";
		description += " - number of attributes: " + aAttributeUnion.size() + "\n";
		description += " - Jaccard: " + aJaccardIndex + "\n";
		description += "\n - Attributes:\n";

		for(String attributeId : aAttributeUnion)
		{
			description += "\t- " + attributeId;
		}

		return description;
	}
	
	//--- Package-private stuff ---
	
	String getRootCategoryName()
	{
		if( aParent == null )
		{
			return aSingularName;
		}
		else
		{
			return aParent.getRootCategoryName();
		}
	}
	
	int getNumberOfChildren()
	{
		return aChildren.size();
	}
		
	
	void calculateJaccard()
	{		
		if(aAttributeUnion.size() > 0)
		{
			aJaccardIndex = ((double) aAttributeIntersection.size()) / ((double) aAttributeUnion.size());
		}
		else
		{
			aJaccardIndex = null;
		}
	}
	
	void putProducts(Iterable<Product> pProducts)
	{
		for(Product prod : pProducts)
		{
			addProduct(prod);
		}
	}
						
	void addProduct(Product pProduct)
	{
		aProducts.add(pProduct);
	}
		
	void addAttribute(String pAttributeId)
	{
		aAttributeUnion.add(pAttributeId);
		aAttributeIntersection.add(pAttributeId);
	}
	
	void mergeAttributes(Set<String> pAttributeIds) {
		for(String attributeId : pAttributeIds)
		{
			aAttributeUnion.add(attributeId);
		}
		HashSet<String> newAttributeIntersection = new HashSet<String>();
		for(String attributeId : aAttributeIntersection)
		{
			if(pAttributeIds.contains(attributeId))
			{
				newAttributeIntersection.add(attributeId);
			}
		}
		aAttributeIntersection = newAttributeIntersection;
	}
	
	Set<String> getAttributeIds()
	{
		return Collections.unmodifiableSet(aAttributeIntersection);
	}
	
	int getRatedCount()
	{
		return aRatedCount;
	}
	
	int getTestedCount()
	{
		return aTestedCount;
	}
	
	void incrementCount(int pAmount)
	{
		aCount += pAmount;
	}
	
	void incrementRatedCount(int pAmount)
	{
		aRatedCount += pAmount;
	}
	
	void incrementTestedCount(int pAmount)
	{
		aTestedCount += pAmount;
	}
	
	void setCount(int pCount)
	{
		aCount = pCount;
	}
	
	void setRatedCount(int pCount)
	{
		aRatedCount = pCount;
	}
	
	void setTestedCount(int pCount)
	{
		aTestedCount = pCount;
	}
	
	void setParent(CategoryBuilder pParent)
	{
		aParent = pParent;
	}	

	CategoryBuilder getParent() 
	{
		return aParent;
	}
	
	Iterable<CategoryBuilder> getChildren() 
	{
		return Collections.unmodifiableCollection(aChildren);
	}
	
	void removeChild(CategoryBuilder pChild)
	{
		ArrayList<CategoryBuilder> newChildren = new ArrayList<CategoryBuilder>();
		for(CategoryBuilder existingChild : aChildren)
		{
			if(existingChild.getId().equals(pChild.getId()))
			{
				continue;
			}
			newChildren.add(existingChild);
		}
		aChildren = newChildren;
	}
}

