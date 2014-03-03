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
	private static final int PERCENT = 100;
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
	private HashSet<String> aSpecificationIntersection = new HashSet<String>();
	private HashMap<String, AttributeStat> aSpecifications = new HashMap<String, AttributeStat>();
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
		aRatings = new HashMap<String, AttributeStat>();
	}
	
	void clearSpecs()
	{
		aSpecifications = new HashMap<String, AttributeStat>();
	}
	
	void calculateJaccard()
	{		
		double numerator  = (double) (aRatingIntersection.size() + aSpecificationIntersection.size());
		double denominator = (double) (aRatings.size() + aSpecifications.size());
		if(denominator > 0)
		{
			aJaccardIndex = numerator / denominator;
		}
		else
		{
			aJaccardIndex = null;
		}
	}
	
	public int getNumChildren()
	{
		return aChildren.size();
	}
	
	public Double getJaccard()
	{
		return aJaccardIndex;
	}
	
	public void putProducts(Iterable<Product> products)
	{
		for(Product prod : products)
		{
			putProduct(prod);
		}
	}
	
	public String describe()
	{
		String desc = "";
		desc += aSingularName + " (\"" + aId + "\")\n";
		desc += " - count: " + aCount + "\n";
		desc += " - ratedCount: " + aRatedCount + "\n";
		desc += " - testedCount: " + aTestedCount + "\n";
		desc += " - number of ratings: " + aRatings.size() + "\n";
		desc += " - number of specs: " + aSpecifications.size() + "\n";
		desc += " - Jaccard: " + aJaccardIndex + "\n";
		desc += "\n - Ratings:\n";
		for(AttributeStat rating : aRatings.values())
		{
			desc += "\t- " + rating.getAttribute().getName() + " (" + rating.getAttribute().getId() +
					"): " + rating.getCount() + " (" + (float)(rating.getCount())/this.getCount()*PERCENT + "%)";
			
			if(rating.getValueMax() != null)
			{
				desc += " min/max: [" + rating.getValueMin() + ", " + rating.getValueMax() + "]";
			}
			desc += " values: [" + StringUtils.join(rating.getValueEnum(), ", ") + "]\n";
		}
		desc += "\n - Specs:\n";
		for(AttributeStat spec : this.aSpecifications.values())
		{
			desc += "\t- " + spec.getAttribute().getName() + " (" + spec.getAttribute().getId() + 
					"): " + spec.getCount() + " (" + (float)(spec.getCount())/this.getCount()*PERCENT + "%)";
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
		aStartNewRatingIntersection = true;
	}
	
	public void restartSpecIntersection()
	{
		
		aStartNewSpecIntersection = true;
	}
	
	public void intersectRatings(Category child)
	{
		// For the first child over which we take the intersection of ratings
		// we simply note all the ratings it has
		if(aStartNewRatingIntersection)
		{
			for(AttributeStat rating : child.getRatings())
			{
				aRatingIntersection.add(rating.getAttribute().getId());
			}
			aStartNewRatingIntersection = false;
			return;
		}
		
		// Subsequently, we remove any ratings that are not found in a given child
		if(aId.equals("28726"))
		{
			int a = 1;
		}
		HashSet<String> newRatingIntersection = new HashSet<String>(); 
		for(String existingRatingId : aRatingIntersection)
		{
			if(child.getRating(existingRatingId) != null)
			{
				newRatingIntersection.add(existingRatingId);
			}
		}
		aRatingIntersection = newRatingIntersection;
	}
		
	public void intersectSpecs(Category child)
	{
		// For the first child over which we take the intersection of specs
		// we simply note all the specs it has
		if(aStartNewSpecIntersection)
		{
			for(AttributeStat spec : child.getSpecs())
			{
				aSpecificationIntersection.add(spec.getAttribute().getId());
			}
			aStartNewSpecIntersection = false;
			return;
		}
		
		// Subsequently, we remove any specs that are not found in a given child
		HashSet<String> newSpecIntersection = new HashSet<String>();
		for(String existingSpecId : aSpecificationIntersection)
		{
			if(child.getSpec(existingSpecId) != null)
			{
				newSpecIntersection.add(existingSpecId);
			}
		}
		aSpecificationIntersection = newSpecIntersection;
	}
	
	public void putProduct(Product prod)
	{
		aProducts.add(prod);
	}
	
	public void putRating(Attribute rating)
	{
		String ratingId = rating.getId();
		for(AttributeStat existingRatingStat : aRatings.values())
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
		aRatings.put(rating.getId(), ratingStat);
	}
	
	public void mergeRatings(Iterable<AttributeStat> ratings)
	{
		for(AttributeStat rating : ratings)
		{
			mergeRating(rating);
		}
	}
	
	public void mergeRating(AttributeStat rating)
	{
		for(AttributeStat existingRating : aRatings.values())
		{
			if(existingRating.getAttribute().getId().equals(rating.getAttribute().getId()))
			{
				existingRating.update(rating);
				return;
			}
		}
		AttributeStat newRating = new AttributeStat(rating);
		aRatings.put(rating.getAttribute().getId(), newRating);
	}
	
	public Iterable<AttributeStat> getRatings()
	{
		return Collections.unmodifiableCollection(aRatings.values());
	}
	
	public AttributeStat getRating(String id)
	{
		return aRatings.get(id);
	}
	
	public void mergeSpecs(Iterable<AttributeStat> specs)
	{
		for(AttributeStat spec : specs)
		{
			mergeSpec(spec);
		}
	}
	
	public void mergeSpec(AttributeStat spec)
	{
		for(AttributeStat existingSpec: this.aSpecifications.values())
		{
			if(existingSpec.getAttribute().getId().equals(spec.getAttribute().getId()))
			{
				existingSpec.update(spec);
				return;
			}
		}
		AttributeStat newSpec = new AttributeStat(spec);
		aSpecifications.put(spec.getAttribute().getId(), newSpec);
	}
	
	public Iterable<AttributeStat> getSpecs()
	{
		return Collections.unmodifiableCollection(aSpecifications.values());
	}

	public AttributeStat getSpec(String id)
	{
		return aSpecifications.get(id);
	}

	public void putRatings(Iterable<Attribute> ratings)
	{
		for(Attribute rating : ratings)
		{
			putRating(rating);
		}
	}

	public void putSpec(Attribute spec)
	{
		String specId = spec.getId();
		for(AttributeStat existingSpecStat: aSpecifications.values())
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
		aSpecifications.put(spec.getId(), specStat);
	}
	
	public int getRatedCount()
	{
		return aRatedCount;
	}
	
	public int getTestedCount()
	{
		return aTestedCount;
	}
	
	public Iterable<Product> getProducts()
	{
		return Collections.unmodifiableCollection(aProducts);
	}
	
	public int getCount()
	{
		return aCount;
	}
	
	public void incrementCount(int add)
	{
		aCount += add;
	}
	
	public void incrementRatedCount(int add)
	{
		aRatedCount += add;
	}
	
	public void incrementTestedCount(int add)
	{
		aTestedCount += add;
	}
	
	public void setCount(int count)
	{
		aCount = count;
	}
	
	public void setRatedCount(int count)
	{
		aRatedCount = count;
	}
	
	public void setTestedCount(int count)
	{
		aTestedCount = count;
	}
	
	public void putSpecs(Iterable<Attribute> specs)
	{
		for(Attribute spec : specs)
		{
			putSpec(spec);
		}
	}
	
	public void setParent(Category parent)
	{
		aParent = parent;
	}	

	public Category getParent() 
	{
		return aParent;
	}
	
	public String getName() 
	{
		return aSingularName;
	}
	
	public Iterable<Category> getChildren() 
	{
		return Collections.unmodifiableCollection(aChildren);
	}
	
	public int size()
	{
		return aProducts.size();
	}
	
	public void removeChild(Category child)
	{
		ArrayList<Category> newChildren = new ArrayList<Category>();
		for(Category existingChild : aChildren)
		{
			if(existingChild.getId().equals(child.getId()))
			{
				continue;
			}
			newChildren.add(existingChild);
		}
		aChildren = newChildren;
	}
	
	public void addChild(Category child)
	{
		aChildren.add(child);
	}

	
	public String getId() 
	{
		return aId;
	}
	
}

