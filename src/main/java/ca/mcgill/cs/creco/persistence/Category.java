package ca.mcgill.cs.creco.persistence;

import java.util.ArrayList;

public class Category {
	public int materialsCount;
	public String pluralName;
	public String parent; 
	public String productGroupId; 
	public String url;
	public String name;
	public String id;
	public String imageCanonical;
	public String singularName;
	public String franchise;
	public String type = null;
	
	public ArrayList<Category> children = new ArrayList<Category>();
	private ArrayList<Rating> ratings;
	private ArrayList<Spec> specs;
	private ArrayList<Product> productsList = new ArrayList<Product>();
	
	public Integer depth;
	public Integer productsCount;
	public Integer testedProductsCount;
	public Integer servicesCount;
	public Integer ratedProductsCount;
	
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
			"testedProductsCount", "servicesCount", "ratedProductsCount"
		};
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
		for(Rating existingRating : this.ratings)
		{
			if(existingRating.getId().equals(ratingId))
			{
				return;
			}
		}
		this.ratings.add(rating);
	}
	
	public Rating[] getRatings()
	{
		return this.ratings.toArray(new Rating[0]);
	}
	
	public Spec[] getSpecs()
	{
		return this.specs.toArray(new Spec[0]);
	}
	
	public void setRatings(ArrayList<Rating> ratings)
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
	
	public void setSpecs(ArrayList<Spec> specs)
	{
		this.specs = specs;
	}
	
	public void putSpec(Spec spec)
	{
		String specId = spec.getId();
		for(Spec existingSpec: this.specs)
		{
			if(existingSpec.getId().equals(specId))
			{
				return;
			}
		}
		this.specs.add(spec);
	}
	
	
	public void putSpecs(Spec[] specs)
	{
		for(Spec spec : specs)
		{
			this.putSpec(spec);
		}
	}
	
	public void setParent(String parentId)
	{
		this.parent = parentId;
	}
	
	public void setString(String key, String val) {
		if(key.equals("singularName")) {
			this.singularName = val;
		} else if (key.equals("pluralName")) {
			this.pluralName = val;
		} else if (key.equals("parent")) {
			this.parent = val;
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
		} else if (key.equals("parent")) {
			return this.parent;
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

	public String getParent() {
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

