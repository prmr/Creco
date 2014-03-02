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

import java.util.Collections;
import java.util.HashMap;

import ca.mcgill.cs.creco.data.json.RatingStub;
import ca.mcgill.cs.creco.data.json.SpecStub;

/**
 * Represents a product in the Consumer Reports database.
 */
public class Product 
{
	// Fields directly copied from CR Data fields
	private String aId;
	private String aDisplayName;
	private Boolean aIsTested;
		
	// Derived fields
	private HashMap<String, Attribute> aRatings = new HashMap<String, Attribute>();
	private HashMap<String, Attribute> aSpecs = new HashMap<String, Attribute>();
	private String aCategoryId;
	private Category aCategory;
	
	/**
	 * Constructs a new product record.
	 * @param pId The product id.
	 * @param pDisplayName The display name of the product.
	 * @param pIsTested Whether the product has been tested by Consumer Reports.
	 * @param pCategoryId The ID of the category for this product.
	 */
	public Product(String pId, String pDisplayName, Boolean pIsTested, String pCategoryId)
	{
		aId = pId;
		aDisplayName = pDisplayName;
		aIsTested = pIsTested;
		aCategoryId = pCategoryId;
	}
	
	/**
	 * Adds a spec to this product. 
	 * @param pSpecStub The stub. TODO replace with non-dependency.
	 */
	public void addSpec(SpecStub pSpecStub)
	{
		aSpecs.put(pSpecStub.attributeId, new Attribute(pSpecStub));
	}
	
	/**
	 * Adds a rating to this product. 
	 * @param pRatingStub The stub. TODO replace with non-dependency.
	 */
	public void addRating(RatingStub pRatingStub)
	{
		aRatings.put(pRatingStub.attributeId, new Attribute(pRatingStub));
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
	 * @return The number of ratings.
	 */
	public int getNumRatings() 
	{ return aRatings.size(); }

	/**
	 * @return The product ID.
	 */
	public String getId() 
	{ return aId; }

	/**
	 * @return The display name of the product.
	 */
	public String getName() 
	{ return aDisplayName; }
	
	/**
	 * @return True if the product has been tested by Consumer Reports.
	 */
	public Boolean getIsTested() 
	{ return aIsTested; }

	/**
	 * @return The product's category.
	 */
	public Category getCategory() 
	{ return aCategory; }

	/**
	 * @return A iterator on the ratings for this product.
	 */
	public Iterable<Attribute> getRatings()
	{
		return Collections.unmodifiableCollection(Product.this.aRatings.values());
	}
	
	/**
	 * Return the rating for this product with pId.
	 * @param pId The id to look for.
	 * @return The corresponding rating.
	 */
	public Attribute getRating(String pId)
	{
		return this.aRatings.get(pId);
	}
	
	/**
	 * @return A iterator on the specs for this product.
	 */
	public Iterable<Attribute> getSpecs() 
	{	
		return Collections.unmodifiableCollection(Product.this.aSpecs.values());
	}
	
	/**
	 * Return the spec for this product with pId.
	 * @param pId The id to look for.
	 * @return The corresponding rating.
	 */
	public Attribute getSpec(String pId)
	{
		return this.aSpecs.get(pId);
	}
}
