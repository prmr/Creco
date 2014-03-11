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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.springframework.stereotype.Component;

import ca.mcgill.cs.creco.data.json.JsonLoadingService;

/**
 * Root of the object graph representing the consumer reports database. 
 * All of the data is accessible through a singleton CRData object. Just 
 * create a new CRData object to get started. Normally in production this
 * will be built when the server starts up, and might be provided to your
 * class by a master controller. For now, make one yourself:
 * CRData crData = new CRData(DataPath.get()); 
 * The main entry points to the data are via the CategoryList and ProductList.
 * CategoryList catList = crData.getCategoryList();
 * These provide access to categories or products by id, and are iterables.
 */
@Component
public final class CRData implements IDataCollector, IDataStore
{
	private static final String DEFAULT_CATEGORY_FILENAME = "category.json";
	private static final double JACCARD_THRESHOLD = 0.8;
	
	private static final String[] DEFAULT_PRODUCT_FILENAMES = 
		{
			"appliances.json", "electronicsComputers.json",
			"cars.json", "health.json", "homeGarden.json", 
			"food.json", "babiesKids.json", "money.json"
		};
	
	private HashMap<String, Product> aProducts = new HashMap<String, Product>();
	
	private Hashtable<String, Category> aCategoryIndex = new Hashtable<String, Category>();
	private ArrayList<Category> aRootCategories = new ArrayList<Category>();					// Top-level categories
	private ArrayList<Category> aEquivalenceClasses = new ArrayList<Category>();
	private ArrayList<Category> aSubEquivalenceClasses = new ArrayList<Category>();
	
	private CRData() throws IOException
	{
		this(DEFAULT_PRODUCT_FILENAMES, DEFAULT_CATEGORY_FILENAME);
	}
	
	private CRData(String[] pProductFileNames, String pCategoryFileName) throws IOException
	{
		IDataLoadingService loadingService = new JsonLoadingService(DataPath.get(), pCategoryFileName, pProductFileNames);
				
		loadingService.loadCategories(this);
		
		// Build an index of all categories.
		for(Category category : aRootCategories)
		{
			index(category);
			eliminateSingletons(category);
		}
		
		loadingService.loadProducts(this);
		
		// Put links from products to categories and vice-versa
		associateProducts(getProducts()); 
		
		// Roll up useful pre-processed statistics and find equivalence classes
		refresh();
		findEquivalenceClasses();
	}
	
	
	/**
	 * Get a category object based on its index.
	 * @param pIndex The requested index.
	 * @return The category corresponding to pIndex.
	 */
	@Override
	public Category getCategory(String pIndex) 
	{
		return aCategoryIndex.get(pIndex);
	}
	
	@Override
	public void addCategory(Category pCategory)
	{
		aRootCategories.add(pCategory);
	}
	
	@Override
	public void addProduct(Product pProduct)
	{
		aProducts.put(pProduct.getId(), pProduct);
	}
	
	/**
	 * @return The equivalence classes
	 */
	@Override
	public Iterable<Category> getEquivalenceClasses()
	{
		return Collections.unmodifiableCollection(aEquivalenceClasses);
	}
	
	/**
	 * @return An iterator on all the franchises
	 */
	@Override
	public Iterator<Category> getCategories()
	{
		return iterator();
	}
	
	/**
	 * @return An iterator on the product list.
	 */
	@Override
	public Iterator<Product> getProducts() 
	{
		return Collections.unmodifiableCollection(aProducts.values()).iterator();
	}
	
	// ----- ***** ----- ***** PRIVATE METHODS ***** ----- ***** -----
	
	private void findEquivalenceClasses() 
	{
		for(Category franchise : aRootCategories)
		{
			recurseFindEquivalenceClasses(franchise, 0);
		}
	}
	
	private void recurseFindEquivalenceClasses(Category pCategory, int pMode)
	{
		Iterable<Category> children = pCategory.getChildren();
		int numChildren = pCategory.getNumberOfChildren();
		
		if(pMode == 1)
		{
			this.aSubEquivalenceClasses.add(pCategory);
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
				this.aEquivalenceClasses.add(pCategory);
				this.aSubEquivalenceClasses.add(pCategory);
				return;
			}
			else
			{
				Double jaccard = pCategory.getJaccardIndex();
				if(jaccard == null || jaccard < JACCARD_THRESHOLD)
				{
					for(Category child : children)
					{
						this.recurseFindEquivalenceClasses(child, 0);
					}
					return;
				}
				else
				{
					this.aEquivalenceClasses.add(pCategory);
					for(Category child : children)
					{
						this.recurseFindEquivalenceClasses(child, 1);
					}
					return;
				}
			}
		}
	}
	
	private Iterator<Category> iterator()
	{
		return Collections.unmodifiableCollection(aCategoryIndex.values()).iterator();
	}
	
	private void index(Category pCategory)
	{
		aCategoryIndex.put(pCategory.getId(), pCategory);
		for(Category child : pCategory.getChildren())
		{
			index(child);
		}
	}
	
	private void refresh() 
	{			
		// Now recursively refresh the category list
		for(Category franchise : aRootCategories)
		{
			recursiveRefresh(franchise, 0);
		}
	}
	
	private void recursiveRefresh(Category pCategory, int pDepth)
	{
		for(Category child : pCategory.getChildren()) 
		{
			recursiveRefresh(child, pDepth + 1);
		}
		
		// We will be "rolling up" counts and various collections from the leaves up to the
		// roots (franchises).  Make sure, for all non-leaves, that these are cleared out to start
		if(pCategory.getNumberOfChildren() > 0)
		{
			pCategory.setRatedCount(0);
			pCategory.setTestedCount(0);
			pCategory.setCount(0);
			pCategory.clearRatings();
			pCategory.clearSpecs();
			pCategory.restartRatingIntersection();
			pCategory.restartSpecIntersection();
		}
	
		// Roll up counts and collections
		for(Category child : pCategory.getChildren())
		{
			// aggregate children's collections
			pCategory.mergeRatings(child.getRatings());
			pCategory.mergeSpecs(child.getSpecifications());
			pCategory.intersectRatings(child);
			pCategory.intersectSpecs(child);
			
			// aggregate children's counts
			pCategory.putProducts(child.getProducts());
			pCategory.incrementRatedCount(child.getRatedCount());
			pCategory.incrementTestedCount(child.getTestedCount());
			pCategory.incrementCount(child.getCount());
		}
		pCategory.calculateJaccard();
	}

	/**
	 * Associate products with categories and vice-versa.
	 * @param pProducts The list of products.
	 */
	private void associateProducts(Iterator<Product> pProducts) 
	{
		while( pProducts.hasNext())
		{
			Product product = pProducts.next();
			Category category = getCategory(product.getCategoryId());
			
			// Create two way link between category and product
			category.addProduct(product);
			product.setCategory(category);
			
			// Aggregate some product info in the category
			category.addRatings(product.getRatings());
			category.putSpecifications(product.getSpecs());
			
			// Increment the counts in this category
			category.incrementCount(1);
			if(product.getIsTested())
			{
				category.incrementTestedCount(1);
			}
			if(product.getNumRatings() > 0)
			{
				category.incrementRatedCount(1);
			}
		}
	}
	
	private void eliminateSingletons(Category pCategory)
	{
		// detect if this is a singleton, if so, splice it out of the hierarchy
		if(pCategory.getNumberOfChildren() == 1)
		{
			Category child = pCategory.getChildren().iterator().next();
			Category parent = pCategory.getParent();
			child.setParent(parent);
			parent.removeChild(pCategory);
			parent.addSubcategory(child);

			eliminateSingletons(child);
		}
		else
		{
			for(Category child : pCategory.getChildren())
			{
				eliminateSingletons(child);
			}
		}
	}
}
