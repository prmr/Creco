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
	private String aImageThumbnail;
	private Double aOverallScore;
	
	// Derived fields
	private boolean aIsRated = false;
	private HashMap<String, Attribute> aAttributes = new HashMap<String, Attribute>();
	private String aCategoryId;
	private Category aCategory;
	private Attribute aPrice = null;
	
		
	/**
	 * Constructs a new product record.
	 * @param pId The product id.
	 * @param pDisplayName The display name of the product.
	 * @param pIsTested Whether the product has been tested by Consumer Reports.
	 * @param pCategoryId The ID of the category for this product.
	 * @param pBrandName The brand name
	 * @param pModelOverviewPageUrl The url of the product on the CR website.
	 * @param pAttributes The attributes of the product. Copied internally.
	 * @param pImageThumbnail Thumbnail of the image
	 * @param pOverallScore the overall score
	 */
	public Product(String pId, String pDisplayName, Boolean pIsTested, String pCategoryId, 
			String pBrandName, String pModelOverviewPageUrl, Collection<Attribute> pAttributes,
			String pImageThumbnail, Double pOverallScore)
	{
		aId = pId;
		aDisplayName = pDisplayName;
		aIsTested = pIsTested;
		aCategoryId = pCategoryId;
		aBrandName = pBrandName;
		aModelOverviewPageUrl = pModelOverviewPageUrl;
		aImageThumbnail = pImageThumbnail;
		aOverallScore = pOverallScore;
		for(Attribute att : pAttributes) 
		{
			// While copying over the attributes, note whether any were ratings, and capture a reference to the price if any
			if(att.isRating())
			{
				aIsRated = true;
			}
			else if(att.isPrice())
			{
				aPrice = att;
			}
			aAttributes.put(att.getId(), att);
		}
	}
	
	void setCategory(Category pCategory)
	{
		aCategory = pCategory;
	}
	
	/**
	 * @return The category ID.
	 */
	public String getCategoryId() 
	{ return aCategoryId; }

	/**
	 * @return True if the product has been tested by Consumer Reports.
	 */
	public Boolean isTested() 
	{ return aIsTested; }

	/**
	 * @return True if the product has a rating.
	 */
	public boolean isRated() 
	{ 
		return aIsRated; 
	}

	/**
	 * @return True if the product has a price.
	 */
	public boolean isPriced() 
	{ 
		return aPrice != null; 
	}

	/**
	 * @return The product ID.
	 */
	public String getId() 
	{ return aId; }
	
	
	
	
	/**
	 * @return The Brand name of the product.
	 */
	public String getBrandName() 
	{
		if(aBrandName != null)
		{
			return aBrandName;
		}
		else 
		{
			return "";
		}
	}

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
	 * @return The product's category.
	 */
	public Category getCategory() 
	{ return aCategory; }
	
	/**
	 * @return A iterator on the specs for this product.
	 */
	public Iterable<Attribute> getAttributes() 
	{	
		return Collections.unmodifiableCollection(aAttributes.values());
	}
		
	/**
	 * Return the Attribute for this product with pId.
	 * @param pId The id to look for.
	 * @return The corresponding rating.
	 */
	public Attribute getAttribute(String pId)
	{
		return aAttributes.get(pId);
	}
	
	/**
	 * Return the price for this product.
	 * @return The corresponding rating.  Null if there is no price.
	 */
	public Attribute getPrice()
	{
		return aPrice;
	}
	
	/**
	 * Return a url to the CR website page for the product.
	 * @return The url for the product overview page.
	 */
	public String getUrl() 
	{
		return aModelOverviewPageUrl;
	}
	/**
	 * Return a Thumbnail url for the product.
	 * @return The Thumbnail url for the product.
	 */
	public String getImage() 
	{
		if((!aImageThumbnail.contains("static2"))&& (!aImageThumbnail.contains("static4")) &&
				(!aImageThumbnail.contains("static3")) && (!aImageThumbnail.contains("static1")) )
		{
		return aImageThumbnail;
		}
		return "";
	}
	/**
	 * @return The overall score to the product.
	 */
	public Double getOverallScore() 
	{
		return aOverallScore;
	}
	
}