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
package ca.mcgill.cs.creco.logic.search;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;

/**
 *Lists products alphabetically after taking a category id.
 */
@Component
public class ProductSort 
{
	private static final Logger LOG = LoggerFactory.getLogger(ProductSort.class);
	private IDataStore aDataStore;	

	/**
	 * Constructor.
	 * @param pDataStore The database whose products will be in the search index. 
	 * @throws IOException If an exception is thrown during the creation of the product index.
	 */
	@Autowired
	public ProductSort(IDataStore pDataStore) throws IOException
	{
		aDataStore = pDataStore;
	}
	
	/**
	 * Lists top 20 products according to their overall score in a category.
	 * @param pCategoryID The category whose products are to be displayed
	 * @return scoredproducts - The list of products.
	 */
	public List<Product> returnTopProducts(String pCategoryID)
	{
		Category category = aDataStore.getCategory(pCategoryID);
		if (category == null)
		{
			LOG.error("Invalid category ID: " + pCategoryID);
			return null;
		} 
		
		List<Product> allScoredProducts = new ArrayList<Product>();
		Map<Double, Product> map = new TreeMap<Double , Product>(Collections.reverseOrder());
		List<Product> scoredProducts = new ArrayList<Product>();
		
		// The products get sorted as they are added to a tree map.
				for(Product product :category.getProducts())
				{
					if(product.getOverallScore() != null)
					{
						map.put(product.getOverallScore(), product );
					}
				}
				
				
				// Repackage the treemap as a list.
				for( Product product : map.values()) 
				{
					allScoredProducts.add(product);
				}

		
				// 20 products with the highest overall score will be displayed initially
				
				if(allScoredProducts.size() >=20)
				{
					for(int i = 0; i < 20; i++)
					{
						scoredProducts.add(allScoredProducts.get(i));
					}
				
					return scoredProducts;
				} 
				else
				{
					return allScoredProducts;
				}
	}
}