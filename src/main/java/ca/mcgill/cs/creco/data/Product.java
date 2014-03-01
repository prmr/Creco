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

import ca.mcgill.cs.creco.data.json.ProductStub;
import ca.mcgill.cs.creco.data.json.RatingStub;
import ca.mcgill.cs.creco.data.json.SpecStub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	private HashMap<String, Rating> aRatings;
	private HashMap<String, Spec> aSpecs;
	private String aCategoryId;
	private Category aCategory;
	
	Product(ProductStub prodStub) 
	{
		// Copy fields from the stub
		this.aId = prodStub.id;                     
		this.aDisplayName = prodStub.displayName;                 
		this.aIsTested = prodStub.isTested;      
		
		// Calculate derived fields
		this.aCategoryId = prodStub.category.id;
		
		this.aSpecs = new HashMap<String, Spec>();
		if(prodStub.specs != null)
		{
			for(SpecStub spec : prodStub.specs)
			{
				this.aSpecs.put(spec.attributeId, new Spec(spec));
			}
		}
		
		this.aRatings = new HashMap<String, Rating>();
		if(prodStub.ratings != null)
		{
			for(RatingStub rating : prodStub.ratings)
			{
				this.aRatings.put(rating.attributeId, new Rating(rating));
			}
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
	public Iterable<Rating> getRatings()
	{
		return Collections.unmodifiableCollection(Product.this.aRatings.values());
	}
	
	/**
	 * Return the rating for this product with pId.
	 * @param pId The id to look for.
	 * @return The corresponding rating.
	 */
	public Rating getRating(String pId)
	{
		return this.aRatings.get(pId);
	}
	
	/**
	 * @return A iterator on the specs for this product.
	 */
	public Iterable<Spec> getSpecs() 
	{	
		return Collections.unmodifiableCollection(Product.this.aSpecs.values());
	}
	
	/**
	 * Return the spec for this product with pId.
	 * @param pId The id to look for.
	 * @return The corresponding rating.
	 */
	public Spec getSpec(String pId)
	{
		return this.aSpecs.get(pId);
	}
}
