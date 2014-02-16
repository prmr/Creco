package ca.mcgill.cs.creco.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.thymeleaf.util.StringUtils;

import ca.mcgill.cs.creco.data.stubs.CategoryStub;

public class Category {
	
	// Fields copied directly from CR database
	private int materialsCount;
	private String pluralName;
	private String productGroupId; 
	private String url;
	private String name;
	private String id;
	private String imageCanonical;
	private String singularName;
	private String franchise;
	private String type;
	private Integer productsCount;
	private Integer testedProductsCount;
	private Integer servicesCount;
	private Integer ratedProductsCount;
	
	// Derived fields set in constructor
	private int count;
	private int ratedCount;
	private int testedCount;
	private int depth;	
	private String classType;
	private Category parent; 
	private Double jaccard;
	private ArrayList<Category> children;
	private CategoryList catList;
	
	// Derived fields set by associateProducts()
	private ArrayList<Product> productsList;	
	private HashSet<String> ratingIntersection;
	private HashSet<String> specIntersection;
	private HashMap<String, SpecStat> specs;
	private HashMap<String, RatingStat> ratings;
	
	// State fields for correcting implementing finding the intersection of children's attributes (later used for jaccard)
	private boolean startNewRatingIntersection;
	private boolean startNewSpecIntersection;
	
	Category(CategoryList catList, CategoryStub catStub, Category parent, int depth) 
	{
		// copy over the CR data fields
		this.materialsCount = catStub.materialsCount;
		this.pluralName = catStub.pluralName;
		this.productGroupId = catStub.productGroupId;
		this.url = catStub.url;
		this.name = catStub.name;
		this.id = catStub.id;
		this.imageCanonical = catStub.imageCanonical;
		this.singularName = catStub.singularName;
		this.franchise = catStub.franchise;
		this.type = catStub.type;
		this.productsCount = catStub.productsCount;
		this.testedProductsCount = catStub.testedProductsCount;
		this.servicesCount = catStub.servicesCount;
		this.ratedProductsCount = catStub.ratedProductsCount;
		
		// Now write the derived fields
		this.catList = catList;
		this.depth = depth;
		this.ratings = new HashMap<String, RatingStat>();
		this.specs = new HashMap<String, SpecStat>();
		this.parent = parent;
		this.productsList = new ArrayList<Product>();
		this.ratingIntersection = new HashSet<String>();
		this.specIntersection = new HashSet<String>();
		this.specs = new HashMap<String, SpecStat>();
		this.ratings = new HashMap<String, RatingStat>();
		
		// set the children, if any
		this.children = new ArrayList<Category>();
		if(catStub.downLevel != null)
		{
			// figure out where the children are, if any
			CategoryStub[] childCatStubs;
			if(catStub.downLevel.supercategory != null)
			{
				childCatStubs = catStub.downLevel.supercategory;
			}
			else if(catStub.downLevel.category != null)
			{
				childCatStubs = catStub.downLevel.category;
			}
			else if(catStub.downLevel.subfranchise != null)
			{
				childCatStubs = catStub.downLevel.subfranchise;
			}
			else if(catStub.downLevel.subcategory != null)
			{
				childCatStubs = catStub.downLevel.subcategory;
			}
			else
			{
				childCatStubs = new CategoryStub[0];
			}
			
			// add them to the child list, building each child into a full Category itself
			childCatStubs = (childCatStubs != null)? childCatStubs : new CategoryStub[0];
			for(CategoryStub childCatStub : childCatStubs)
			{
				this.children.add(new Category(this.catList, childCatStub, this, this.depth + 1));
			}
		}
		
		// Initialize the state ready for taking intersections of child category's attributes
		this.startNewRatingIntersection = true;
		this.startNewSpecIntersection = true;
	}

	void clearRatings()
	{
		this.ratings = new HashMap<String, RatingStat>();
	}
	
	void clearSpecs()
	{
		this.specs = new HashMap<String, SpecStat>();
	}
	
	void calculateJaccard()
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
	
	void setDepth(int depth)
	{
		this.depth = depth;
	}
	
	public int getNumProducts()
	{
		return this.productsList.size();
	}
	
	public int getNumChildren()
	{
		return this.children.size();
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
		if(this.classType.equals("equivalence") || this.classType.equals("subequivalence"))
		{
			return true;
		}
		else
		{
			return false;
		}
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
		desc += this.singularName + " (\"" + this.id + "\")\n";
		desc += " - count: " + this.count + "\n";
		desc += " - ratedCount: " + this.ratedCount + "\n";
		desc += " - testedCount: " + this.testedCount + "\n";
		desc += " - number of ratings: " + this.ratings.size() + "\n";
		desc += " - number of specs: " + this.specs.size() + "\n";
		desc += " - Jaccard: " + this.jaccard + "\n";
		desc += "\n - Ratings:\n";
		for(RatingStat rating : this.ratings.values())
		{
			desc += "\t- " + rating.getName() + " (" + rating.getId() +"): " + rating.getCount() + " (" + (float)(rating.getCount())/this.getCount()*100 + "%)";
			if(rating.getValueMax() != null)
			{
				desc += " min/max: [" + rating.getValueMin() + ", " + rating.getValueMax() + "]";
			}
			desc += " values: [" + StringUtils.join(rating.getValueEnum(), ", ") + "]\n";
		}
		desc += "\n - Specs:\n";
		for(SpecStat spec : this.specs.values())
		{
			desc += "\t- " + spec.getName() + " (" + spec.getId() + "): " + spec.getCount() + " (" + (float)(spec.getCount())/this.getCount()*100 + "%)";
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
		this.startNewRatingIntersection = true;
	}
	
	public void restartSpecIntersection()
	{
		this.startNewSpecIntersection = true;
	}
	
	public void intersectRatings(Category child)
	{
		// For the first child over which we take the intersection of ratings
		// we simply note all the ratings it has
		if(this.startNewRatingIntersection)
		{
			for(RatingStat rating : child.getRatings())
			{
				this.ratingIntersection.add(rating.getId());
			}
			this.startNewRatingIntersection = false;
			return;
		}
		
		// Subsequently, we remove any ratings that are not found in a given child
		if(this.id.equals("28726"))
		{
			int a = 1;
		}
		HashSet<String> newRatingIntersection = new HashSet<String>(); 
		for(String existingRatingId : this.ratingIntersection)
		{
			if(child.getRating(existingRatingId) != null)
			{
				newRatingIntersection.add(existingRatingId);
			}
		}
		this.ratingIntersection = newRatingIntersection;
	}
		
	public void intersectSpecs(Category child)
	{
		// For the first child over which we take the intersection of specs
		// we simply note all the specs it has
		if(this.startNewSpecIntersection)
		{
			for(SpecStat spec : child.getSpecs())
			{
				this.specIntersection.add(spec.getId());
			}
			this.startNewSpecIntersection = false;
			return;
		}
		
		// Subsequently, we remove any specs that are not found in a given child
		HashSet<String> newSpecIntersection = new HashSet<String>();
		for(String existingSpecId : this.specIntersection)
		{
			if(child.getSpec(existingSpecId) != null)
			{
				newSpecIntersection.add(existingSpecId);
			}
		}
		this.specIntersection = newSpecIntersection;
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
		for(RatingStat existingRatingStat : this.ratings.values())
		{
			if(existingRatingStat.getId().equals(ratingId))
			{
				existingRatingStat.increment(1);
				existingRatingStat.updateRange(rating.getValue());
				return;
			}
		}
		RatingStat ratingStat = new RatingStat(rating);
		ratingStat.increment(1);
		ratingStat.updateRange(rating.getValue());
		this.ratings.put(rating.getId(), ratingStat);
	}
	
	public void mergeRatings(Iterable<RatingStat> ratings)
	{
		for(RatingStat rating : ratings)
		{
			this.mergeRating(rating);
		}
	}
	
	public void mergeRating(RatingStat rating)
	{
		for(RatingStat existingRating : this.ratings.values())
		{
			if(existingRating.getId().equals(rating.getId()))
			{
				existingRating.update(rating);
				return;
			}
		}
		RatingStat newRating = new RatingStat(rating);
		this.ratings.put(rating.getId(), newRating);
	}
	
	public Iterable<RatingStat> getRatings()
	{
		return Collections.unmodifiableCollection(this.ratings.values());
	}
	
	public RatingStat getRating(String id)
	{
		return this.ratings.get(id);
	}
	
	public void mergeSpecs(Iterable<SpecStat> specs)
	{
		for(SpecStat spec : specs)
		{
			this.mergeSpec(spec);
		}
	}
	
	public void mergeSpec(SpecStat spec)
	{
		for(SpecStat existingSpec: this.specs.values())
		{
			if(existingSpec.getId().equals(spec.getId()))
			{
				existingSpec.update(spec);
				return;
			}
		}
		SpecStat newSpec = new SpecStat(spec);
		this.specs.put(spec.getId(), newSpec);
	}
	
	public Iterable<SpecStat> getSpecs()
	{
		return Collections.unmodifiableCollection(this.specs.values());
	}

	public SpecStat getSpec(String id)
	{
		return this.specs.get(id);
	}

	public void putRatings(Iterable<Rating> ratings)
	{
		for(Rating rating : ratings)
		{
			this.putRating(rating);
		}
	}

	public void putSpec(Spec spec)
	{
		String specId = spec.getId();
		for(SpecStat existingSpecStat: this.specs.values())
		{
			if(existingSpecStat.getId().equals(specId))
			{
				existingSpecStat.increment(1);
				existingSpecStat.updateRange(spec.getValue());
				return;
			}
		}
		SpecStat specStat = new SpecStat(spec);
		specStat.increment(1);
		specStat.updateRange(spec.getValue());
		this.specs.put(spec.getId(), specStat);
	}
	
	public int getRatedCount()
	{
		return this.ratedCount;
	}
	
	public int getTestedCount()
	{
		return this.testedCount;
	}
	
	public Iterable<Product> getProducts()
	{
		return Collections.unmodifiableCollection(Category.this.productsList);
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
		this.ratedCount += add;
	}
	
	public void incrementTestedCount(int add)
	{
		this.testedCount += add;
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
	
	public void putSpecs(Iterable<Spec> specs)
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

	public Category getParent() {
		return this.parent;
	}
	
	public String getName() {
		return this.singularName;
	}
	
	public String getPluralName()
	{
		return this.pluralName;
	}
	
	public Iterable<Category> getChildren() 
	{
		return Collections.unmodifiableCollection(Category.this.children);
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

