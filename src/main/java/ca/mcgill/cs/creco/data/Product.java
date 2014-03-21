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
import java.util.Collections;
import java.util.HashMap;

/**
 * Represents a product in the Consumer Reports database.
 */
public class Product 
{
	// Fields directly copied from CR Data fields
	private String aId;
	private String aDisplayName;
	private Boolean aIsTested;
	private String aBrandName;
	private String aModelOverviewPageUrl;
	
	// Derived fields
	private HashMap<String, Attribute> aAttributes = new HashMap<String, Attribute>();
	private String aCategoryId;
	private CategoryBuilder aCategory;
	
		
	/**
	 * Constructs a new product record.
	 * @param pId The product id.
	 * @param pDisplayName The display name of the product.
	 * @param pIsTested Whether the product has been tested by Consumer Reports.
	 * @param pCategoryId The ID of the category for this product.
	 * @param pBrandName The brand name
	 */
	public Product(String pId, String pDisplayName, Boolean pIsTested, String pCategoryId, String pBrandName, String pModelOverviewPageUrl, Collection<Attribute> Attributes)
	{
		aId = pId;
		aDisplayName = pDisplayName;
		aIsTested = pIsTested;
		aCategoryId = pCategoryId;
		aBrandName = pBrandName;
		aModelOverviewPageUrl = pModelOverviewPageUrl;
	}
	
	public void addAttribute(Attribute pAttribute)
	{
		aAttributes.put(pAttribute.getId(), pAttribute);
	}
	
	void setCategory(CategoryBuilder pCategory)
	{
		aCategory = pCategory;
	}
	
	/**
	 * @return The category ID.
	 */
	public String getCategoryId() 
	{ return aCategoryId; }
	
	/**
	 * @return The number of ratings.
	 */
	//TODO: this needs to detect the number of ratings proper (not all attributes)
	public int getNumRatings() 
	{ 
		return aAttributes.size(); 
	}

	/**
	 * @return The product ID.
	 */
	public String getId() 
	{ return aId; }
	
	

	/**
	 * @return The modified display name of the product.
	 */
	public String getName() 
	{ 
		
		if(aBrandName != null)
		{
			return aBrandName +" " +aDisplayName;
		} 
		else
		{
			return aDisplayName;
		}
		}
	
	/**
	 * @return True if the product has been tested by Consumer Reports.
	 */
	public Boolean getIsTested() 
	{ return aIsTested; }

	/**
	 * @return The product's category.
	 */
	public Category getCategory() 
	{ return aCategory.getCategory(); }

	/**
	 * @return A iterator on the ratings for this product.
	 */
	@Deprecated
	public Iterable<Attribute> getRatings()
	{
		return getAttributes();
	}

	/**
	 * @return A iterator on the specs for this product.
	 */
	@Deprecated
	public Iterable<Attribute> getSpecs() 
	{	
		return getAttributes();
	}
	
	/**
	 * @return A iterator on the specs for this product.
	 */
	public Iterable<Attribute> getAttributes() 
	{	
		return Collections.unmodifiableCollection(aAttributes.values());
	}
	
	/**
	 * Return the rating for this product with pId.
	 * @param pId The id to look for.
	 * @return The corresponding rating.
	 */
	@Deprecated
	public Attribute getRating(String pId)
	{
		return getAttribute(pId);
	}
	
	/**
	 * Return the spec for this product with pId.
	 * @param pId The id to look for.
	 * @return The corresponding rating.
	 */
	@Deprecated
	public Attribute getSpec(String pId)
	{
		return getAttribute(pId);
	}
	
	/**
	 * Return the spec for this product with pId.
	 * @param pId The id to look for.
	 * @return The corresponding rating.
	 */
	public Attribute getAttribute(String pId)
	{
		return aAttributes.get(pId);
	}
	
	
	/**
	 * Return a url to the CR website page for the product
	 * @return The url for the product overview page
	 */
	public String getUrl() {
		return aModelOverviewPageUrl;
	}
}