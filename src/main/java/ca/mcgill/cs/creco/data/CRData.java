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
import java.util.Collection;
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
public final class CRData implements IDataStore
{
	//TODO: line-coverage by tests
	
	private static final String DEFAULT_CATEGORY_FILENAME = "category.json";
	private static final double JACCARD_THRESHOLD = 0.8;
	
	private static final String[] DEFAULT_PRODUCT_FILENAMES = 
		{
			"appliances.json", "electronicsComputers.json",
			"cars.json", "health.json", "homeGarden.json", 
			"food.json", "babiesKids.json", "money.json"
		};
	
	private HashMap<String, Product> aProducts = new HashMap<String, Product>();
	private HashMap<String, Category> aCategory2Index = new HashMap<String, Category>();
	
	private CRData() throws IOException
	{
		this(DEFAULT_PRODUCT_FILENAMES, DEFAULT_CATEGORY_FILENAME);
	}
	
	private CRData(String[] pProductFileNames, String pCategoryFileName) throws IOException
	{
		
		IDataLoadingService loadingService = new JsonLoadingService(DataPath.get(), DEFAULT_CATEGORY_FILENAME, DEFAULT_PRODUCT_FILENAMES);
		CategoryTree lCatTree = new CategoryTree();
		
		loadingService.loadCategories(lCatTree);
		
		// Build the categoryTree index
		lCatTree.indexRootCategories(); // TODO: Check if this is actually needed 
		lCatTree.eliminateAllSingletons();
		
		loadingService.loadProducts(lCatTree);
		
		// Put links from products to categories and vice-versa
		lCatTree.associateProducts();
		
		// Roll up useful pre-processed statistics and find equivalence classes
		lCatTree.refresh();
		lCatTree.findEquivalenceClasses();
		
		// Copy the Products and Categories to CRData
		for(Product prod : lCatTree.getProducts()) {
			addProduct(prod);
		}
		for(Category cat : lCatTree.getCategories()) {
			aCategory2Index.put(cat.getId(), cat);
		}
		
	}
	
	/**
	 * @param pIndex The requested index.
	 * @return The category corresponding to pIndex.
	 */
	public Category getCategory(String pIndex)
	{
		return aCategory2Index.get(pIndex);
	}
	
	public void addProduct(Product pProduct)
	{
		aProducts.put(pProduct.getId(), pProduct);
	}

	@Override
	public Product getProduct(String pId)
	{
		return aProducts.get(pId);
	}
	
	@Override
	public Collection<Category> getCategories()
	{
		return Collections.unmodifiableCollection(aCategory2Index.values());
	}
	
	/**
	 * @return An iterator on the product list.
	 */
	@Override
	public Collection<Product> getProducts() 
	{
		return Collections.unmodifiableCollection(aProducts.values());
	}
	

}
