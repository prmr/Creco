package ca.mcgill.cs.creco.persistence;

import org.json.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;
import java.io.IOException;

public class CategoryList {
	private Hashtable<String, Category> hash = new Hashtable<String, Category>();
	private ArrayList<Category> franchises = new ArrayList<Category>();
	private ArrayList<Category> equivalenceClasses = new ArrayList<Category>();
	private ArrayList<Category> subEquivalenceClasses = new ArrayList<Category>();
	
	public CategoryList() {}
	
	public Category get(String key) 
	{
		return this.hash.get(key);
	}
	
	public void refresh() 
	{
		// TODO 
		//  - calculate depths
		//  - tally product counts
		//  - tally attributes
		//		- calculate the penetration of attributes
		// 		- calculate jaccard
		
		Iterator<Entry<String, Category>> it = this.getIterator();
		while(it.hasNext()) 
		{
			Category cat = it.next().getValue();
			if(cat.getInt("depth") == 0) 
			{
				this.franchises.add(cat);
			}
			
			String catType = cat.getString("type");
			if(catType == null) 
			{
				continue;
			}
			else 
			{
				if(catType.equals("equivalenceClass")) 
				{
					this.equivalenceClasses.add(cat);
				}
				else if(catType.equals("subEquivalenceClass"))
				{
					this.subEquivalenceClasses.add(cat);
				}
			}
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
			
			prod.setCategory(cat);
			
			cat.putProduct(prod);
			cat.putRatings(prod.getRatings());
			cat.putSpecs(prod.getSpecs());
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
			
			String parentId = cat.getParent();
			Category parent = this.get(parentId);
			
			child.setParent(parentId);
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
		String dumpString = cat.getSingularName() + " (" + cat.getId() + ")" + "\n";
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
