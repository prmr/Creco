package ca.mcgill.cs.creco.data;

import java.util.ArrayList;
import java.util.Arrays;

public class Category {
	public int materialsCount;
	public String pluralName;
	public String productGroupId; 
	public String url;
	public String name;
	public String id;
	public String imageCanonical;
	public String singularName;
	public String franchise;
	public String type = null;
	
	public String classType;
	
	public Category parent; 
	public ArrayList<Category> children = new ArrayList<Category>();
	private ArrayList<RatingStat> ratings;
	private ArrayList<SpecStat> specs;
	private ArrayList<Product> productsList = new ArrayList<Product>();
	
	private ArrayList<String> ratingIntersection;
	private ArrayList<String> specIntersection;
	
	public Integer depth;
	public Integer productsCount;
	public Integer testedProductsCount;
	public Integer servicesCount;
	public Integer ratedProductsCount;
	
	public Double jaccard;
	
	private int count;
	private int ratedCount;
	private int testedCount;
	
	private DownLevel downLevel;
	private CategoryList catList;

	public static final String[] getExpectedStringFields() {
		return new String[] 
		{
			"pluralName", "parent", "productGroupId", 
			"url", "name", "id", "imageCanonical", "singularName", 
			"franchise", "type", "children"
		};
	}
	
	public static final String[] getExpectedIntFields() {
		return new String[] 
		{
			"materialsCount", "depth", "productsCount", 
			"testedProductsCount", "servicesCount", "ratedProductsCount",
			"count", "ratedCount", "testedCount"
		};
	}
	
	public void calculateJaccard()
	{
		double numerator  = ((double) (this.ratingIntersection.size() + this.specIntersection.size()));
		double denominator = ((double) (this.ratings.size() + this.specs.size()));
		if(denominator > 0)
		{
			this.jaccard = numerator / denominator;
		}
		else
		{
			this.jaccard = null;
		}
	}
	
	public Double getJaccard()
	{
		return this.jaccard;
	}
	
	public void setClassType(String classType)
	{
		this.classType = classType;
	}
	
	public String getClassType()
	{
		return this.classType;
	}
	
	public boolean isEquivalence()
	{
		if(this.classType.equals("equivalence"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isSubEquivalence()
	{
		if(this.classType.equals("equivalence") || this.classType.equals("subEquivalence"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void putProducts(Product[] products)
	{
		for(Product prod : products)
		{
			this.putProduct(prod);
		}
	}
	
	public String describe()
	{
		String desc = "";
		desc += this.singularName + " (\"" + this.id + "\")\n";
		desc += " - count: " + this.count + "\n";
		desc += " - ratedCount: " + this.ratedCount + "\n";
		desc += " - testedCount: " + this.testedCount + "\n";
		desc += " - number of ratings: " + this.ratings.size() + "\n";
		desc += " - number of specs: " + this.specs.size() + "\n";
		desc += " - Jaccard: " + this.jaccard + "\n";
		desc += "\n - Ratings:\n";
		for(RatingStat rating : this.ratings)
		{
			desc += "\t- " + rating.getAttributeName() + ": " + rating.getCount() + " (" + (float)(rating.getCount())/this.getCount()*100 + "%)\n";
		}
		desc += "\n - Specs:\n";
		for(SpecStat spec : this.specs)
		{
			desc += "\t- " + spec.getAttributeName() + " : " + spec.getCount() + " (" + (float)(spec.getCount())/this.getCount()*100 + "%)\n";
		}
		
		return desc;
	}
	
	public void intersectRatings(RatingStat[] ratings)
	{
		// if the ratingsIntersection is empty (null), then just copy in the current set of ratings
		if(this.ratingIntersection == null)
		{
			this.ratingIntersection = new ArrayList<String>();
			for(RatingStat ratingStat : ratings)
			{
				this.ratingIntersection.add(ratingStat.getId());
			}
		}
		
		// Otherwise, scan the current set and remove any ratingId's not found in the passed set
		else
		{
			for(String existingRatingId : this.ratingIntersection)
			{
				for(RatingStat newRating : ratings)
				{
					if(newRating.getId().equals(existingRatingId))
					{
						continue;
					}
				}
				this.removeRatingFromIntersection(existingRatingId);
			}
		}
	}
	
	
	public void removeRatingFromIntersection(String ratingId)
	{
		ArrayList<String> newRatingIntersection = new ArrayList<String>();
		for(String existingRatingId : this.ratingIntersection)
		{
			if(existingRatingId.equals(ratingId))
			{
				continue;
			}
			newRatingIntersection.add(ratingId);
		}
		this.setRatingIntersection(newRatingIntersection);
	}
	
	public void intersectSpecs(SpecStat[] specs)
	{
		// if the specsIntersection is empty (null), then just copy in the current set of specs
		if(this.specIntersection == null)
		{
			this.specIntersection = new ArrayList<String>();
			for(SpecStat specStat : specs)
			{
				this.specIntersection.add(specStat.getId());
			}
		}
		
		// Otherwise, scan the current set and remove any specId's not found in the passed set
		else
		{
			for(String existingSpecId : this.specIntersection)
			{
				for(SpecStat newSpec : specs)
				{
					if(newSpec.getId().equals(existingSpecId))
					{
						continue;
					}
				}
				this.removeSpecFromIntersection(existingSpecId);
			}
		}
	}
	
	
	public void removeSpecFromIntersection(String specId)
	{
		ArrayList<String> newSpecIntersection = new ArrayList<String>();
		for(String existingSpecId : this.specIntersection)
		{
			if(existingSpecId.equals(specId))
			{
				continue;
			}
			newSpecIntersection.add(specId);
		}
		this.setSpecIntersection(newSpecIntersection);
	}
	
	
	public void setRatingIntersection(ArrayList<String> ratingIntersection)
	{
		this.ratingIntersection = ratingIntersection;
	}
	
	public void setSpecIntersection(ArrayList<String> specIntersection)
	{
		this.specIntersection = specIntersection;
	}
	
	public void putProduct(Product prod)
	{
		this.productsList.add(prod);
	}
	
	public void setCatList(CategoryList catList)
	{
		this.catList = catList;
	}
	
	public void putRating(Rating rating)
	{
		String ratingId = rating.getId();
		for(RatingStat existingRatingStat : this.ratings)
		{
			if(existingRatingStat.getId().equals(ratingId))
			{
				existingRatingStat.increment(1);
				return;
			}
		}
		RatingStat ratingStat = new RatingStat(rating);
		ratingStat.increment(1);
		this.ratings.add(ratingStat);
	}
	
	public void mergeRatings(RatingStat[] ratings)
	{
		for(RatingStat rating : ratings)
		{
			this.mergeRating(rating);
		}
	}
	
	public void mergeRating(RatingStat rating)
	{
		for(RatingStat existingRating : this.ratings)
		{
			if(existingRating.getId().equals(rating.getId()))
			{
				existingRating.increment(rating.getCount());
				return;
			}
		}
		RatingStat newRating = new RatingStat(rating);
		this.ratings.add(newRating);
	}
	
	public RatingStat[] getRatings()
	{
		return this.ratings.toArray(new RatingStat[0]);
	}
	
	public void mergeSpecs(SpecStat[] specs)
	{
		for(SpecStat spec : specs)
		{
			this.mergeSpec(spec);
		}
	}
	
	public void mergeSpec(SpecStat spec)
	{
		for(SpecStat existingSpec: this.specs)
		{
			if(existingSpec.getId().equals(spec.getId()))
			{
				existingSpec.increment(spec.getCount());
				return;
			}
		}
		SpecStat newSpec = new SpecStat(spec);
		this.specs.add(newSpec);
	}
	
	public SpecStat[] getSpecs()
	{
		return this.specs.toArray(new SpecStat[0]);
	}
	
	public void setRatings(ArrayList<RatingStat> ratings)
	{
		this.ratings = ratings;
	}
	
	public void putRatings(Rating[] ratings)
	{
		for(Rating rating : ratings)
		{
			this.putRating(rating);
		}
	}
	
	public void setSpecs(ArrayList<SpecStat> specs)
	{
		this.specs = specs;
	}
	
	
	public void putSpec(Spec spec)
	{
		String specId = spec.getId();
		for(SpecStat existingSpecStat: this.specs)
		{
			if(existingSpecStat.getId().equals(specId))
			{
				existingSpecStat.increment(1);
				return;
			}
		}
		SpecStat specStat = new SpecStat(spec);
		specStat.increment(1);
		this.specs.add(specStat);
	}
	
	public int getRatedCount()
	{
		return this.ratedCount;
	}
	
	public int getTestedCount()
	{
		return this.testedCount;
	}
	
	public Product[] getProducts()
	{
		return this.productsList.toArray(new Product[0]);
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public void incrementCount(int add)
	{
		this.count += add;
	}
	
	public void incrementRatedCount(int add)
	{
		this.ratedCount += 1;
	}
	
	public void incrementTestedCount(int add)
	{
		this.testedCount += 1;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public void setRatedCount(int count)
	{
		this.ratedCount = count;
	}
	
	public void setTestedCount(int count)
	{
		this.testedCount = count;
	}
	
	public void putSpecs(Spec[] specs)
	{
		for(Spec spec : specs)
		{
			this.putSpec(spec);
		}
	}
	
	public void setParent(Category parent)
	{
		this.parent = parent;
	}
	
	public void setString(String key, String val) {
		if(key.equals("singularName")) {
			this.singularName = val;
		} else if (key.equals("pluralName")) {
			this.pluralName = val;
		} else if(key.equals("productGroupId")) {
			this.productGroupId = val;
		} else if(key.equals("url")) {
			this.url = val;
		} else if(key.equals("name")) {
			this.name = val;
		} else if(key.equals("id")) {
			this.id = val;
		} else if(key.equals("imageCanonical")) {
			this.imageCanonical = val;
		} else if (key.equals("franchise")) {
			this.franchise = val;
		} else if (key.equals("type")) {
			this.type = val;
		}
	}
	
	public String getString(String key) {
		if(key.equals("singularName")) {
			return this.singularName;
		} else if (key.equals("pluralName")) {
			return this.pluralName;
		} else if(key.equals("productGroupId")) {
			return this.productGroupId;
		} else if(key.equals("url")) {
			return this.url;
		} else if(key.equals("name")) {
			return this.name;
		} else if(key.equals("id")) {
			return this.id;
		} else if(key.equals("imageCanonical")) {
			return this.imageCanonical;
		} else if (key.equals("franchise")) {
			return this.franchise;
		} else if (key.equals("type")) {
			return this.type;
		} 
		else 
		{
			return null;
		}
	}
	
	public void setInt(String key, Integer val) {
		if(key.equals("materialsCount")) {
			this.materialsCount = val;
		} else if(key.equals("depth")) {
			this.depth = val;
		} else if(key.equals("productsCount")) {
			this.productsCount = val;
		} else if(key.equals("testedProductsCount")) {
			this.testedProductsCount = val;
		} else if(key.equals("servicesCount")) {
			this.servicesCount = val;
		} else if(key.equals("ratedProductsCount")) {
			this.ratedProductsCount = val;
		}
	}
	
	public Integer getInt(String key) {
		if(key.equals("materialsCount")) {
			return this.materialsCount;
		} else if(key.equals("depth")) {
			return this.depth;
		} else if(key.equals("productsCount")) {
			return this.productsCount;
		} else if(key.equals("testedProductsCount")) {
			return this.testedProductsCount;
		} else if(key.equals("servicesCount")) {
			return this.servicesCount;
		} else if(key.equals("ratedProductsCount")) {
			return this.ratedProductsCount;
		} else
		{
			return null;
		}
	}	
	
	public Category[] getDownLevel() 
	{
		if(this.downLevel != null)
		{
			return this.downLevel.getChildren();
		}
		else
		{
			return null;
		}
	}

	public Category getParent() {
		return this.parent;
	}
	
	public String getSingularName() {
		return this.singularName;
	}
	
	public Category[] getChildren() 
	{
		return this.children.toArray(new Category[0]);
	}
	
	public int size()
	{
		return this.productsList.size();
	}
	
	public void removeChild(Category child)
	{
		ArrayList<Category> newChildren = new ArrayList<Category>();
		for(Category existingChild : this.children)
		{
			if(existingChild.getId().equals(child.getId()))
			{
				continue;
			}
			newChildren.add(existingChild);
		}
		this.children = newChildren;
	}
	
	public void addChild(Category child)
	{
		this.children.add(child);
	}

	
	public String getType() {
		return this.type;
	}
	
	public String getId() 
	{
		return this.id;
	}
	
	public int getDepth() 
	{
		return this.depth;
	}
}

