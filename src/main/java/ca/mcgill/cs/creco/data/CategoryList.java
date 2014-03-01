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
import java.util.Hashtable;
import java.util.Iterator;

public class CategoryList implements Iterable<Category> 
{
	private Hashtable<String, Category> hash;
	private ArrayList<Category> franchises;
	private ArrayList<Category> equivalenceClasses;
	private ArrayList<Category> subEquivalenceClasses;
	private ArrayList<Category> leaves;
	private double jaccardThreshhold;
	
	public CategoryList(double jaccardThreshhold) 
	{
		this.jaccardThreshhold = jaccardThreshhold;
		this.hash = new Hashtable<String, Category>();
		this.franchises = new ArrayList<Category>();
		this.equivalenceClasses = new ArrayList<Category>();
		this.subEquivalenceClasses = new ArrayList<Category>();
		this.leaves = new ArrayList<Category>();
	}
	
	void addFranchise(Category franchise)
	{
		this.franchises.add(franchise);
	}
	
	void addLeaf(Category leaf)
	{
		this.leaves.add(leaf);
	}
	
	public Category get(String key) 
	{
		return this.hash.get(key);
	}
	
	public Iterable<Category> getEqClasses()
	{
		return Collections.unmodifiableCollection(this.equivalenceClasses);
	}
	
	public int getNumEqClasses()
	{
		return this.equivalenceClasses.size();
	}
	
	public int getNumSubEqClasses()
	{
		return this.subEquivalenceClasses.size();
	}
	
	public Iterable<Category> getSubEqClasses()
	{
		return Collections.unmodifiableCollection(this.subEquivalenceClasses);
	}
	
	public void findEquivalenceClasses() {
		for(Category franchise : this.franchises)
		{
			this.recurseFindEquivalenceClasses(franchise, 0);
		}
	}
	
	public void recurseFindEquivalenceClasses(Category cat, int mode)
	{
		Iterable<Category> children = cat.getChildren();
		int numChildren = cat.getNumChildren();
		
		if(mode == 1)
		{
			cat.setClassType("subequivalence");
			this.subEquivalenceClasses.add(cat);
			for(Category child : children)
			{
				this.recurseFindEquivalenceClasses(child, 1);
			}
			return;
		}
		else
		{
			if(numChildren == 0)
			{
				cat.setClassType("equivalence");
				this.equivalenceClasses.add(cat);
				this.subEquivalenceClasses.add(cat);
				return;
			}
			else
			{
				Double jaccard = cat.getJaccard();
				if(jaccard == null || jaccard < this.jaccardThreshhold)
				{
					cat.setClassType("category");
					for(Category child : children)
					{
						this.recurseFindEquivalenceClasses(child, 0);
					}
					return;
				}
				else
				{
					cat.setClassType("equivalence");
					this.equivalenceClasses.add(cat);
					for(Category child : children)
					{
						this.recurseFindEquivalenceClasses(child, 1);
					}
					return;
				}
			}
		}
	}
	
	public Iterator<Category> iterator()
	{
		return Collections.unmodifiableCollection(this.hash.values()).iterator();
	}
	
	public void index()
	{
		for(Category franchise : this.franchises)
		{
			this.recursiveIndex(franchise);
		}
	}
	
	private void recursiveIndex(Category cat)
	{
		this.put(cat.getId(), cat);
		for(Category child : cat.getChildren())
		{
			recursiveIndex(child);
		}
		if(cat.getNumChildren() == 0)
		{
			this.leaves.add(cat);
		}
	}
	
	public void refresh() 
	{			
		// Now recursively refresh the category list
		for(Category franchise : this.franchises)
		{
			this.recursiveRefresh(franchise, 0);
		}
	}
	
	public void recursiveRefresh(Category cat, int depth)
	{
		cat.setDepth(depth);
		
		for(Category child : cat.getChildren()) {
			recursiveRefresh(child, depth + 1);
		}
		
		// We will be "rolling up" counts and various collections from the leaves up to the
		// roots (franchises).  Make sure, for all non-leaves, that these are cleared out to start
		if(cat.getNumChildren() > 0)
		{
			cat.setRatedCount(0);
			cat.setTestedCount(0);
			cat.setCount(0);
			cat.clearRatings();
			cat.clearSpecs();
			cat.restartRatingIntersection();
			cat.restartSpecIntersection();
		}
	
		// Roll up counts and collections
		for(Category child : cat.getChildren())
		{
			// aggregate children's collections
			cat.mergeRatings(child.getRatings());
			cat.mergeSpecs(child.getSpecs());
			cat.intersectRatings(child);
			cat.intersectSpecs(child);
			
			// aggregate children's counts
			cat.putProducts(child.getProducts());
			cat.incrementRatedCount(child.getRatedCount());
			cat.incrementTestedCount(child.getTestedCount());
			cat.incrementCount(child.getCount());
		}
		cat.calculateJaccard();
	}
	
	public void put(String key, Category val) {
		this.hash.put(key, val);
	}

	public void associateProducts(ProductList prodList) 
	{
		for(Product prod : prodList)
		{
			Category cat = this.get(prod.getCategoryId());
			
			// Create two way link between category and product
			cat.putProduct(prod);
			prod.setCategory(cat);
			
			// Aggregate some product info in the category
			cat.putRatings(prod.getRatings());
			cat.putSpecs(prod.getSpecs());
			
			// Increment the counts in this category
			cat.incrementCount(1);
			if(prod.getIsTested())
			{
				cat.incrementTestedCount(1);
			}
			if(prod.getNumRatings() > 0)
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
		
		// detect if this is a singleton, if so, splice it out of the hierarchy
		if(cat.getNumChildren() == 1)
		{
			Category child = cat.getChildren().iterator().next();
			Category parent = cat.getParent();
			child.setParent(parent);
			parent.removeChild(cat);
			parent.addChild(child);

			this.recurseEliminateSingletons(child);
		}
		else
		{
			for(Category child : cat.getChildren())
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
		String dumpString = cat.getName() + " (" + cat.getId() + ")\n";
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
