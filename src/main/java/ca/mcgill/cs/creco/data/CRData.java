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
public final class CRData implements IDataCollector
{
	private static final String DEFAULT_CATEGORY_FILENAME = "category.json";
	private static final double JACCARD_THRESHOLD = 0.8;
	
	private static final String[] DEFAULT_PRODUCT_FILENAMES = {
			"appliances.json", "electronicsComputers.json",
			"cars.json", "health.json", "homeGarden.json", 
			"food.json", "babiesKids.json", "money.json"
		};
	
	private static CRData instance = null;
	
	private HashMap<String, Product> aProducts = new HashMap<String, Product>();
	
	private Hashtable<String, Category> aCategoryIndex = new Hashtable<String, Category>();
	private ArrayList<Category> aFranchises = new ArrayList<Category>();
	private ArrayList<Category> aEquivalenceClasses = new ArrayList<Category>();
	private ArrayList<Category> aSubEquivalenceClasses = new ArrayList<Category>();
	private ArrayList<Category> aLeaves = new ArrayList<Category>();
	
	private CRData(String[] pProductFileNames, String pCategoryFileName) throws IOException
	{
		IDataLoadingService loadingService = new JsonLoadingService(DataPath.get(), pCategoryFileName, pProductFileNames);
				
		loadingService.loadCategories(this);
		index();
		eliminateSingletons();
		
		loadingService.loadProducts(this);
		
		// Put links from products to categories and vice-versa
		associateProducts(getProducts()); 
		
		// Roll up useful pre-processed statistics and find equivalence classes
		refresh();
		findEquivalenceClasses();		
	}
	
	/**
	 * Initializes the CR Data on the first call, and subsequently
	 * returns the singleton instance of the CR data.
	 * @return CRData singleton
	 * @throws IOException if the data cannot be accessed
	 */
	public static CRData getData() throws IOException
	{
		if (instance == null)
		{
			instance = new CRData(DEFAULT_PRODUCT_FILENAMES, DEFAULT_CATEGORY_FILENAME);
		}
		return instance;
	}
	
	/**
	 * Get a category object based on its index.
	 * @param pIndex The requested index.
	 * @return The category corresponding to pIndex.
	 */
	public Category get(String pIndex) 
	{
		return aCategoryIndex.get(pIndex);
	}
	
	@Override
	public void addCategory(Category pCategory)
	{
		addFranchise(pCategory);
	}
	
	@Override
	public void addProduct(Product pProduct)
	{
		aProducts.put(pProduct.getId(), pProduct);
	}
	
	/**
	 * @return The equivalence classes
	 */
	public Iterable<Category> getEquivalenceClasses()
	{
		return Collections.unmodifiableCollection(aEquivalenceClasses);
	}
	
	/**
	 * Initializes the CRData based on specified filenames.
	 * @return CRData singleton
	 * @param pProductFileNames The list of file names for the product data.
	 * @param pCategoryFileName The name of the category file.
	 * @throws IOException If the database was already initialized.
	 */
	public static CRData setupWithFileNames(String[] pProductFileNames, String pCategoryFileName) throws IOException 
	{
		if (instance == null)
		{
			instance = new CRData(pProductFileNames, pCategoryFileName);
		} 
		else
		{
			throw new IOException("CR Database was already initialized. Use CRData.getData() instead.");
		}
		return instance;
	}
	
	/**
	 * @return An iterator on all the franchises
	 */
	public Iterator<Category> getCategories()
	{
		return iterator();
	}
	
	/**
	 * @return An iterator on the product list.
	 */
	public Iterator<Product> getProducts() 
	{
		return Collections.unmodifiableCollection(aProducts.values()).iterator();
	}
	
	// ----- ***** ----- ***** PRIVATE METHODS ***** ----- ***** -----
	
	private void addFranchise(Category pFranchise)
	{
		aFranchises.add(pFranchise);
	}
	
	private void findEquivalenceClasses() 
	{
		for(Category franchise : aFranchises)
		{
			recurseFindEquivalenceClasses(franchise, 0);
		}
	}
	
	private void recurseFindEquivalenceClasses(Category pCategory, int pMode)
	{
		Iterable<Category> children = pCategory.getChildren();
		int numChildren = pCategory.getNumChildren();
		
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
				Double jaccard = pCategory.getJaccard();
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
		return Collections.unmodifiableCollection(this.aCategoryIndex.values()).iterator();
	}
	
	private void index()
	{
		for(Category franchise : this.aFranchises)
		{
			this.recursiveIndex(franchise);
		}
	}
	
	private void recursiveIndex(Category pCategory)
	{
		this.put(pCategory.getId(), pCategory);
		for(Category child : pCategory.getChildren())
		{
			recursiveIndex(child);
		}
		if(pCategory.getNumChildren() == 0)
		{
			this.aLeaves.add(pCategory);
		}
	}
	
	private void refresh() 
	{			
		// Now recursively refresh the category list
		for(Category franchise : this.aFranchises)
		{
			this.recursiveRefresh(franchise, 0);
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
		if(pCategory.getNumChildren() > 0)
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
			pCategory.mergeSpecs(child.getSpecs());
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
	
	private void put(String pKey, Category pValue) 
	{
		aCategoryIndex.put(pKey, pValue);
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
			Category category = get(product.getCategoryId());
			
			// Create two way link between category and product
			category.putProduct(product);
			product.setCategory(category);
			
			// Aggregate some product info in the category
			category.putRatings(product.getRatings());
			category.putSpecs(product.getSpecs());
			
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
	
	private void eliminateSingletons()
	{
		for(Category franchise : this.aFranchises)
		{
			this.recurseEliminateSingletons(franchise);
		}
	}
	
	private void recurseEliminateSingletons(Category pCategory)
	{
		// detect if this is a singleton, if so, splice it out of the hierarchy
		if(pCategory.getNumChildren() == 1)
		{
			Category child = pCategory.getChildren().iterator().next();
			Category parent = pCategory.getParent();
			child.setParent(parent);
			parent.removeChild(pCategory);
			parent.addChild(child);

			recurseEliminateSingletons(child);
		}
		else
		{
			for(Category child : pCategory.getChildren())
			{
				recurseEliminateSingletons(child);
			}
		}
	}
}
