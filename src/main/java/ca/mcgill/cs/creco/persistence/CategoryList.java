package ca.mcgill.cs.creco.persistence;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

public class CategoryList {
	private Hashtable<String, Category> hash = new Hashtable<String, Category>();
	private ArrayList<Category> franchises = new ArrayList<Category>();
	private ArrayList<Category> equivalenceClasses = new ArrayList<Category>();
	private ArrayList<Category> subEquivalenceClasses = new ArrayList<Category>();
	private ArrayList<Category> leaves = new ArrayList<Category>();
	private double jaccardThreshhold;
	
	public CategoryList(double jaccardThreshhold) 
	{
		this.jaccardThreshhold = jaccardThreshhold;
	}
	
	public Category get(String key) 
	{
		return this.hash.get(key);
	}
	
	public void findEquivalenceClasses() {
		for(Category franchise : this.franchises)
		{
			this.recurseFindEquivalenceClasses(franchise, 0);
		}
	}
	
	public void recurseFindEquivalenceClasses(Category cat, int mode)
	{
		Category[] children = cat.getChildren();
		
		if(mode == 1)
		{
			cat.setClassType("subEquivalence");
			for(Category child : children)
			{
				this.recurseFindEquivalenceClasses(child, 1);
			}
			return;
		}
		else
		{
			if(children.length == 0)
			{
				cat.setClassType("equivalence");
				return;
			}
			else
			{
				Double jaccard = cat.getJaccard();
				if(jaccard > this.jaccardThreshhold)
				{
					cat.setClassType("equivalence");
					for(Category child : children)
					{
						this.recurseFindEquivalenceClasses(child, 1);
					}
					return;
				}
				else
				{
					cat.setClassType("category");
					for(Category child : children)
					{
						this.recurseFindEquivalenceClasses(child, 0);
					}
					return;
				}	
			}
		}
	}
	
	public void refresh() 
	{
		// TODO 
		//  - calculate depths
		//  - tally product counts
		//  - accumulate equivalence classes
		//  - accumulate leaves
		//  - tally attributes
		//		- calculate the penetration of attributes
		// 		- calculate jaccard
		
		// First, make a list of all the franchises, and all the leaves.
		// These are good starting points for recursive functions
		this.franchises = new ArrayList<Category>();
		this.leaves = new ArrayList<Category>();
		
		Iterator<Entry<String, Category>> it = this.getIterator();
		while(it.hasNext()) 
		{
			Category cat = it.next().getValue();
			if(cat.getInt("depth") == 0) 
			{
				this.franchises.add(cat);
			}
			if(cat.getChildren().length == 0)
			{
				this.leaves.add(cat);
			}
		}
		
		// Now recursively refresh the category list
		for(Category franchise : this.franchises)
		{
			this.recursiveRefresh(franchise, 0);
		}
	}
	
	public void recursiveRefresh(Category cat, int depth)
	{
		cat.setInt("depth", depth);
		
		Category[] children = cat.getChildren();
		
		for(Category child : children) {
			recursiveRefresh(child, depth + 1);
		}
		
		if(children.length > 0)
		{
			cat.setRatedCount(0);
			cat.setTestedCount(0);
			cat.setCount(0);
			cat.setRatings(new ArrayList<RatingStat>());
			cat.setSpecs(new ArrayList<SpecStat>());
			cat.setRatingIntersection(null);
			cat.setSpecIntersection(null);
		}
	
		for(Category child : cat.getChildren())
		{
			cat.mergeRatings(child.getRatings());
			cat.mergeSpecs(child.getSpecs());
			cat.intersectRatings(child.getRatings());
			cat.intersectSpecs(child.getSpecs());
			
			cat.calculateJaccard();
			
			cat.putProducts(child.getProducts());
			cat.incrementRatedCount(child.getRatedCount());
			cat.incrementTestedCount(child.getTestedCount());
			cat.incrementCount(child.getCount());
		}
	}
	
	public Iterator<Entry<String, Category>> getIterator() {
		Iterator<Entry<String, Category>> it = hash.entrySet().iterator();
		return it;
	}
	
	public void put(String key, Category val) {
		this.hash.put(key, val);
	}

	public void associateProducts(ProductList prodList) 
	{
		Iterator<Entry<String, Product>> it = prodList.getIterator();
		while(it.hasNext())
		{
			Product prod = it.next().getValue();
			String catId = prod.getCategoryId();
			Category cat = this.get(catId);
			
			// Create two-way link between product and category
			prod.setCategory(cat);
			cat.putProduct(prod);
			
			// Aggregate some product info in the category
			cat.putRatings(prod.getRatings());
			cat.putSpecs(prod.getSpecs());
			
			// Increment the counts in this category
			cat.incrementCount(1);
			if(prod.getBool("isTested"))
			{
				cat.incrementTestedCount(1);
			}
			if(prod.getRatings().length > 0)
			{
				cat.incrementRatedCount(1);
			}
			
		}
	}
	
	public void eliminateSingletons()
	{
		for(Category franchise : this.franchises)
		{
			this.recurseEliminateSingletons(franchise);
		}
	}
	
	public void recurseEliminateSingletons(Category cat)
	{
		String catId = cat.getId();
		Category[] children = cat.getChildren();
		
		// detect if this is a singleton, if so, splice it out of the hierarchy
		if(children.length == 1)
		{
			Category child = children[0];
			String childId = child.getId();
			
			Category parent = cat.getParent();
			String parentId = parent.getId();
			
			child.setParent(parent);
			parent.removeChild(cat);
			parent.addChild(child);

			this.recurseEliminateSingletons(child);
		}
		else
		{
			for(Category child : children)
			{
				this.recurseEliminateSingletons(child);
			}
		}
		
	}
	
	public String dumpTree()
	{
		String dumpString = "";
		for(Category franchise : this.franchises)
		{
			dumpString += this.recurseDumpTree(franchise);
		}
		
		return dumpString;
	}
	
	public String recurseDumpTree(Category cat)
	{
		// Process this level
		int depth = cat.getDepth();
		String dumpString = cat.getSingularName() + " (" + cat.getId() + ")\n";
		if(cat.isSubEquivalence())
		{
			if(cat.isEquivalence())
			{
				dumpString = "(E) " + dumpString;
			}
			else
			{
				dumpString = "(e) " + dumpString;
			}
		}
		
		for(int i=0; i < depth; i++)
		{
			dumpString = "\t" + dumpString;
		}
		
		// Recurse on children
		for(Category child : cat.getChildren())
		{
			dumpString += this.recurseDumpTree(child);
		}
		return dumpString;
	}
}
