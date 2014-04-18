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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import ca.mcgill.cs.creco.data.json.JsonLoadingService;

/**
 * Root of the object graph representing the consumer reports database. 
 * All of the data is accessible through a singleton CRData object. Just 
 * create a new CRData object to get started. Normally in production this
 * will be built when the server starts up.
 */
@Component
public final class CRData implements IDataStore
{
	private static final String DEFAULT_CATEGORY_FILENAME = "category.json";
	private static final String DEFAULT_DEAD_LINKS_FILENAME = "dead_links.json";
	
	private static final String[] DEFAULT_PRODUCT_FILENAMES = 
		{
			"appliances.json", "electronicsComputers.json",
			"cars.json", "health.json", "homeGarden.json", 
			"food.json", "babiesKids.json", "money.json"
		};
	
	private HashMap<String, Product> aProducts = new HashMap<String, Product>();
	private HashMap<String, Category> aCategoryIndex = new HashMap<String, Category>();
	
	private CRData() throws IOException
	{
		this(DEFAULT_PRODUCT_FILENAMES, DEFAULT_CATEGORY_FILENAME, DEFAULT_DEAD_LINKS_FILENAME);
	}
	
	private CRData(String[] pProductFileNames, String pCategoryFileName) throws IOException
	{
		this(pProductFileNames, pCategoryFileName, DEFAULT_DEAD_LINKS_FILENAME);
	}
	
	private CRData(String[] pProductFileNames, String pCategoryFileName, String pDeadlinksFilename) throws IOException
	{
		
		IDataLoadingService loadingService = new JsonLoadingService(DataPath.get(), 
				pCategoryFileName, pProductFileNames, pDeadlinksFilename);
		CategoryTree lCatTree = new CategoryTree();
		
		loadingService.loadCategories(lCatTree);
		
		// Build the categoryTree index
		lCatTree.indexRootCategories();
		lCatTree.eliminateAllSingletons();
		
		loadingService.loadProducts(lCatTree);
		
		// Put links from products to categories and vice-versa
		lCatTree.associateProducts();
		
		// Roll up useful pre-processed statistics and find equivalence classes
		lCatTree.refresh();
		lCatTree.findEquivalenceClasses();
		

		// Copy the Products and Categories to CRData
		for(Product prod : lCatTree.getProducts()) 
		{
			aProducts.put(prod.getId(), prod);
		}
		for(CategoryNode catNode : lCatTree.getCategories()) 
		{
			Category newCategory = new Category(catNode.getId(), catNode.getName(), catNode.getRootCategoryName(), catNode.getProducts());
			aCategoryIndex.put(newCategory.getId(), newCategory);

			// Associate products to the equivalence class that contains them
			for(Product prod : newCategory.getProducts())
			{
				prod.setCategory(newCategory);
			}
		}
		
	}
	
	/**
	 * @param pIndex The requested index.
	 * @return The category corresponding to pIndex.
	 */
	public Category getCategory(String pIndex)
	{
		return aCategoryIndex.get(pIndex);
	}
	

	@Override
	public Product getProduct(String pId)
	{
		return aProducts.get(pId);
	}
	
	@Override
	public Collection<Category> getCategories()
	{
		return Collections.unmodifiableCollection(aCategoryIndex.values());
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
