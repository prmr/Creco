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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.thymeleaf.util.StringUtils;

/**
 * Represents a category in the product database.
 */
public class Category 
{
	// Fields copied directly from CR database
	private String aId;
	private String aSingularName;
	
	// Derived fields set in constructor
	private int aCount;
	private int aRatedCount;
	private int aTestedCount;
	private Category aParent; 
	private Double aJaccardIndex;
	private ArrayList<Category> aChildren = new ArrayList<Category>();
	
	// Derived fields set by associateProducts()
	private ArrayList<Product> aProducts = new ArrayList<Product>();	
	private HashSet<String> aRatingIntersection = new HashSet<String>();
	private HashSet<String> aSpecIntersection = new HashSet<String>();
	private HashMap<String, AttributeStat> aSpecs = new HashMap<String, AttributeStat>();
	private HashMap<String, AttributeStat> aRatings = new HashMap<String, AttributeStat>();
	
	// State fields for correcting implementing finding the intersection of children's attributes (later used for jaccard)
	private boolean aStartNewRatingIntersection;
	private boolean aStartNewSpecIntersection;
	
	/**
	 * Creates a new Category initialized only with the parameter fields.
	 * @param pId The Category ID
	 * @param pSingularName The singular name of the category.
	 * @param pParent The category's parent, or null if this is a root category.
	 */
	public Category(String pId, String pSingularName, Category pParent)
	{
		aId = pId;
		aSingularName = pSingularName;
		aParent = pParent;
	}
	
	void clearRatings()
	{
		this.aRatings = new HashMap<String, AttributeStat>();
	}
	
	void clearSpecs()
	{
		this.aSpecs = new HashMap<String, AttributeStat>();
	}
	
	void calculateJaccard()
	{		
		double numerator  = ((double) (this.aRatingIntersection.size() + this.aSpecIntersection.size()));
		double denominator = ((double) (this.aRatings.size() + this.aSpecs.size()));
		if(denominator > 0)
		{
			this.aJaccardIndex = numerator / denominator;
		}
		else
		{
			this.aJaccardIndex = null;
		}
	}
	
	public int getNumChildren()
	{
		return this.aChildren.size();
	}
	
	public Double getJaccard()
	{
		return this.aJaccardIndex;
	}
	
	public void putProducts(Iterable<Product> products)
	{
		for(Product prod : products)
		{
			this.putProduct(prod);
		}
	}
	
	public String describe()
	{
		String desc = "";
		desc += this.aSingularName + " (\"" + this.aId + "\")\n";
		desc += " - count: " + this.aCount + "\n";
		desc += " - ratedCount: " + this.aRatedCount + "\n";
		desc += " - testedCount: " + this.aTestedCount + "\n";
		desc += " - number of ratings: " + this.aRatings.size() + "\n";
		desc += " - number of specs: " + this.aSpecs.size() + "\n";
		desc += " - Jaccard: " + this.aJaccardIndex + "\n";
		desc += "\n - Ratings:\n";
		for(AttributeStat rating : this.aRatings.values())
		{
			desc += "\t- " + rating.getAttribute().getName() + " (" + rating.getAttribute().getId() +"): " + rating.getCount() + " (" + (float)(rating.getCount())/this.getCount()*100 + "%)";
			if(rating.getValueMax() != null)
			{
				desc += " min/max: [" + rating.getValueMin() + ", " + rating.getValueMax() + "]";
			}
			desc += " values: [" + StringUtils.join(rating.getValueEnum(), ", ") + "]\n";
		}
		desc += "\n - Specs:\n";
		for(AttributeStat spec : this.aSpecs.values())
		{
			desc += "\t- " + spec.getAttribute().getName() + " (" + spec.getAttribute().getId() + "): " + spec.getCount() + " (" + (float)(spec.getCount())/this.getCount()*100 + "%)";
			if(spec.getValueMax() != null)
			{
				desc += " min/max: [" + spec.getValueMin() + ", " + spec.getValueMax() + "]";
			}
			desc += " values: [" + StringUtils.join(spec.getValueEnum(), ", ") + "]\n";
		}
		
		return desc;
	}
	
	public void restartRatingIntersection()
	{
		this.aStartNewRatingIntersection = true;
	}
	
	public void restartSpecIntersection()
	{
		this.aStartNewSpecIntersection = true;
	}
	
	public void intersectRatings(Category child)
	{
		// For the first child over which we take the intersection of ratings
		// we simply note all the ratings it has
		if(this.aStartNewRatingIntersection)
		{
			for(AttributeStat rating : child.getRatings())
			{
				this.aRatingIntersection.add(rating.getAttribute().getId());
			}
			this.aStartNewRatingIntersection = false;
			return;
		}
		
		// Subsequently, we remove any ratings that are not found in a given child
		if(this.aId.equals("28726"))
		{
			int a = 1;
		}
		HashSet<String> newRatingIntersection = new HashSet<String>(); 
		for(String existingRatingId : this.aRatingIntersection)
		{
			if(child.getRating(existingRatingId) != null)
			{
				newRatingIntersection.add(existingRatingId);
			}
		}
		this.aRatingIntersection = newRatingIntersection;
	}
		
	public void intersectSpecs(Category child)
	{
		// For the first child over which we take the intersection of specs
		// we simply note all the specs it has
		if(this.aStartNewSpecIntersection)
		{
			for(AttributeStat spec : child.getSpecs())
			{
				this.aSpecIntersection.add(spec.getAttribute().getId());
			}
			this.aStartNewSpecIntersection = false;
			return;
		}
		
		// Subsequently, we remove any specs that are not found in a given child
		HashSet<String> newSpecIntersection = new HashSet<String>();
		for(String existingSpecId : this.aSpecIntersection)
		{
			if(child.getSpec(existingSpecId) != null)
			{
				newSpecIntersection.add(existingSpecId);
			}
		}
		this.aSpecIntersection = newSpecIntersection;
	}
	
	public void putProduct(Product prod)
	{
		this.aProducts.add(prod);
	}
	
	public void putRating(Attribute rating)
	{
		String ratingId = rating.getId();
		for(AttributeStat existingRatingStat : this.aRatings.values())
		{
			if(existingRatingStat.getAttribute().getId().equals(ratingId))
			{
				existingRatingStat.increment(1);
				existingRatingStat.updateRange(rating.getValue());
				return;
			}
		}
		AttributeStat ratingStat = new AttributeStat(rating);
		ratingStat.increment(1);
		ratingStat.updateRange(rating.getValue());
		this.aRatings.put(rating.getId(), ratingStat);
	}
	
	public void mergeRatings(Iterable<AttributeStat> ratings)
	{
		for(AttributeStat rating : ratings)
		{
			this.mergeRating(rating);
		}
	}
	
	public void mergeRating(AttributeStat rating)
	{
		for(AttributeStat existingRating : this.aRatings.values())
		{
			if(existingRating.getAttribute().getId().equals(rating.getAttribute().getId()))
			{
				existingRating.update(rating);
				return;
			}
		}
		AttributeStat newRating = new AttributeStat(rating);
		this.aRatings.put(rating.getAttribute().getId(), newRating);
	}
	
	public Iterable<AttributeStat> getRatings()
	{
		return Collections.unmodifiableCollection(this.aRatings.values());
	}
	
	public AttributeStat getRating(String id)
	{
		return this.aRatings.get(id);
	}
	
	public void mergeSpecs(Iterable<AttributeStat> specs)
	{
		for(AttributeStat spec : specs)
		{
			this.mergeSpec(spec);
		}
	}
	
	public void mergeSpec(AttributeStat spec)
	{
		for(AttributeStat existingSpec: this.aSpecs.values())
		{
			if(existingSpec.getAttribute().getId().equals(spec.getAttribute().getId()))
			{
				existingSpec.update(spec);
				return;
			}
		}
		AttributeStat newSpec = new AttributeStat(spec);
		this.aSpecs.put(spec.getAttribute().getId(), newSpec);
	}
	
	public Iterable<AttributeStat> getSpecs()
	{
		return Collections.unmodifiableCollection(this.aSpecs.values());
	}

	public AttributeStat getSpec(String id)
	{
		return this.aSpecs.get(id);
	}

	public void putRatings(Iterable<Attribute> ratings)
	{
		for(Attribute rating : ratings)
		{
			this.putRating(rating);
		}
	}

	public void putSpec(Attribute spec)
	{
		String specId = spec.getId();
		for(AttributeStat existingSpecStat: this.aSpecs.values())
		{
			if(existingSpecStat.getAttribute().getId().equals(specId))
			{
				existingSpecStat.increment(1);
				existingSpecStat.updateRange(spec.getValue());
				return;
			}
		}
		AttributeStat specStat = new AttributeStat(spec);
		specStat.increment(1);
		specStat.updateRange(spec.getValue());
		this.aSpecs.put(spec.getId(), specStat);
	}
	
	public int getRatedCount()
	{
		return this.aRatedCount;
	}
	
	public int getTestedCount()
	{
		return this.aTestedCount;
	}
	
	public Iterable<Product> getProducts()
	{
		return Collections.unmodifiableCollection(Category.this.aProducts);
	}
	
	public int getCount()
	{
		return this.aCount;
	}
	
	public void incrementCount(int add)
	{
		this.aCount += add;
	}
	
	public void incrementRatedCount(int add)
	{
		this.aRatedCount += add;
	}
	
	public void incrementTestedCount(int add)
	{
		this.aTestedCount += add;
	}
	
	public void setCount(int count)
	{
		this.aCount = count;
	}
	
	public void setRatedCount(int count)
	{
		this.aRatedCount = count;
	}
	
	public void setTestedCount(int count)
	{
		this.aTestedCount = count;
	}
	
	public void putSpecs(Iterable<Attribute> specs)
	{
		for(Attribute spec : specs)
		{
			this.putSpec(spec);
		}
	}
	
	public void setParent(Category parent)
	{
		this.aParent = parent;
	}	

	public Category getParent() {
		return this.aParent;
	}
	
	public String getName() {
		return this.aSingularName;
	}
	
	public Iterable<Category> getChildren() 
	{
		return Collections.unmodifiableCollection(Category.this.aChildren);
	}
	
	public int size()
	{
		return this.aProducts.size();
	}
	
	public void removeChild(Category child)
	{
		ArrayList<Category> newChildren = new ArrayList<Category>();
		for(Category existingChild : this.aChildren)
		{
			if(existingChild.getId().equals(child.getId()))
			{
				continue;
			}
			newChildren.add(existingChild);
		}
		this.aChildren = newChildren;
	}
	
	public void addChild(Category child)
	{
		this.aChildren.add(child);
	}

	
	public String getId() 
	{
		return this.aId;
	}
	
}

