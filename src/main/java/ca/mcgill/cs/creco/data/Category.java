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
	
	//--- Public Method ---
	
	/**
	 * @return The Jaccard index of the ratings and specifications covered
	 * by this category.
	 */
	public Double getJaccardIndex()
	{
		return aJaccardIndex;
	}
	
	/**
	 * @return A collection of ratings associated with this category.
	 */
	public Iterable<AttributeStat> getRatings()
	{
		return Collections.unmodifiableCollection(aRatings.values());
	}
	
	/**
	 * @return A collection of specifications associated with this category.
	 */
	public Iterable<AttributeStat> getSpecifications()
	{
		return Collections.unmodifiableCollection(aSpecifications.values());
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
	public void addSubcategory(Category pSubcategory)
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
		description += " - number of ratings: " + aRatings.size() + "\n";
		description += " - number of specs: " + aSpecifications.size() + "\n";
		description += " - Jaccard: " + aJaccardIndex + "\n";
		description += "\n - Ratings:\n";
		for(AttributeStat rating : aRatings.values())
		{
			description += "\t- " + rating.getAttribute().getName() + " (" + rating.getAttribute().getId() +
					"): " + rating.getCount() + " (" + (float)(rating.getCount())/this.getCount()*PERCENT + "%)";
			
			if(rating.getValueMax() != null)
			{
				description += " min/max: [" + rating.getValueMin() + ", " + rating.getValueMax() + "]";
			}
			description += " values: [" + StringUtils.join(rating.getValueEnum(), ", ") + "]\n";
		}
		description += "\n - Specs:\n";
		for(AttributeStat spec : this.aSpecifications.values())
		{
			description += "\t- " + spec.getAttribute().getName() + " (" + spec.getAttribute().getId() + 
					"): " + spec.getCount() + " (" + (float)(spec.getCount())/this.getCount()*PERCENT + "%)";
			if(spec.getValueMax() != null)
			{
				description += " min/max: [" + spec.getValueMin() + ", " + spec.getValueMax() + "]";
			}
			description += " values: [" + StringUtils.join(spec.getValueEnum(), ", ") + "]\n";
		}
		
		return description;
	}
	
	//--- Package-private stuff ---
	
	int getNumberOfChildren()
	{
		return aChildren.size();
	}
	
	void clearRatings()
	{
		aRatings.clear();
	}
	
	void clearSpecs()
	{
		aSpecifications.clear();
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
	
	void putProducts(Iterable<Product> pProducts)
	{
		for(Product prod : pProducts)
		{
			addProduct(prod);
		}
	}
	
	void restartRatingIntersection()
	{
		aStartNewRatingIntersection = true;
	}
	
	void restartSpecIntersection()
	{
		
		aStartNewSpecIntersection = true;
	}
	
	void intersectRatings(Category pChild)
	{
		// For the first child over which we take the intersection of ratings
		// we simply note all the ratings it has
		if(aStartNewRatingIntersection)
		{
			for(AttributeStat rating : pChild.getRatings())
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
			if(pChild.getRating(existingRatingId) != null)
			{
				newRatingIntersection.add(existingRatingId);
			}
		}
		aRatingIntersection = newRatingIntersection;
	}
		
	void intersectSpecs(Category pChild)
	{
		// For the first child over which we take the intersection of specs
		// we simply note all the specs it has
		if(aStartNewSpecIntersection)
		{
			for(AttributeStat spec : pChild.getSpecifications())
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
			if(pChild.getSpecification(existingSpecId) != null)
			{
				newSpecIntersection.add(existingSpecId);
			}
		}
		aSpecificationIntersection = newSpecIntersection;
	}
	
	void addProduct(Product pProduct)
	{
		aProducts.add(pProduct);
	}
	
	void addRating(Attribute pRating)
	{
		String ratingId = pRating.getId();
		for(AttributeStat existingRatingStat : aRatings.values())
		{
			if(existingRatingStat.getAttribute().getId().equals(ratingId))
			{
				existingRatingStat.increment(1);
				existingRatingStat.updateRange(pRating.getValue());
				return;
			}
		}
		AttributeStat ratingStat = new AttributeStat(pRating);
		ratingStat.increment(1);
		ratingStat.updateRange(pRating.getValue());
		aRatings.put(pRating.getId(), ratingStat);
	}
	
	void mergeRatings(Iterable<AttributeStat> pRatings)
	{
		for(AttributeStat rating : pRatings)
		{
			mergeRating(rating);
		}
	}
	
	private void mergeRating(AttributeStat pRatingStat)
	{
		for(AttributeStat existingRating : aRatings.values())
		{
			if(existingRating.getAttribute().getId().equals(pRatingStat.getAttribute().getId()))
			{
				existingRating.update(pRatingStat);
				return;
			}
		}
		AttributeStat newRating = new AttributeStat(pRatingStat);
		aRatings.put(pRatingStat.getAttribute().getId(), newRating);
	}
	
	private AttributeStat getRating(String pId)
	{
		return aRatings.get(pId);
	}
	
	void mergeSpecs(Iterable<AttributeStat> pSpecifications)
	{
		for(AttributeStat spec : pSpecifications)
		{
			mergeSpec(spec);
		}
	}
	
	private void mergeSpec(AttributeStat pSpecifications)
	{
		for(AttributeStat existingSpec: this.aSpecifications.values())
		{
			if(existingSpec.getAttribute().getId().equals(pSpecifications.getAttribute().getId()))
			{
				existingSpec.update(pSpecifications);
				return;
			}
		}
		AttributeStat newSpec = new AttributeStat(pSpecifications);
		aSpecifications.put(pSpecifications.getAttribute().getId(), newSpec);
	}
	
	private AttributeStat getSpecification(String pId)
	{
		return aSpecifications.get(pId);
	}

	void addRatings(Iterable<Attribute> pRatings)
	{
		for(Attribute rating : pRatings)
		{
			addRating(rating);
		}
	}

	private void addSpecification(Attribute pSpecification)
	{
		String specId = pSpecification.getId();
		for(AttributeStat existingSpecStat: aSpecifications.values())
		{
			if(existingSpecStat.getAttribute().getId().equals(specId))
			{
				existingSpecStat.increment(1);
				existingSpecStat.updateRange(pSpecification.getValue());
				return;
			}
		}
		AttributeStat specStat = new AttributeStat(pSpecification);
		specStat.increment(1);
		specStat.updateRange(pSpecification.getValue());
		aSpecifications.put(pSpecification.getId(), specStat);
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
	
	void putSpecifications(Iterable<Attribute> pSpecifications)
	{
		for(Attribute spec : pSpecifications)
		{
			addSpecification(spec);
		}
	}
	
	void setParent(Category pParent)
	{
		aParent = pParent;
	}	

	Category getParent() 
	{
		return aParent;
	}
	
	Iterable<Category> getChildren() 
	{
		return Collections.unmodifiableCollection(aChildren);
	}
	
	void removeChild(Category pChild)
	{
		ArrayList<Category> newChildren = new ArrayList<Category>();
		for(Category existingChild : aChildren)
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

