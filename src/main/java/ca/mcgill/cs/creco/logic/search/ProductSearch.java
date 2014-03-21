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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;

/**
 *Lists products alphabetically after taking a category id
 */
@Component
public class ProductSearch implements IProductSearch
{
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductSearch.class);
	
	
	private IDataStore aDataStore;
	

	/**
	 * Constructor.
	 * @param pDataStore The database whose products will be in the search index. 
	 * @throws IOException If an exception is thrown during the creation of the product index.
	 */
	@Autowired
	public ProductSearch(IDataStore pDataStore) throws IOException
	{
		aDataStore = pDataStore;
		
		
	}
	
	

	@Override
	public List<Product> returnProductsAlphabetically(String pCategoryID)
	{
		// TODO Auto-generated method stub
		Category category = aDataStore.getCategory(pCategoryID);
		if (category == null)
		{
			LOG.error("Invalid category ID: " + pCategoryID);
			return null;
		} 
		
	
		List<Product> scoredProducts = new ArrayList<Product>();
		Map<String, Product> map = new HashMap<String, Product>();
		
		
		for(Product product :category.getProducts())
		{
			map.put(product.getName(), product );
		}
		
		Map<String, Product> treeMap = new TreeMap<String, Product>(map);
		
		for (Map.Entry entry : treeMap.entrySet()) 
		{
			Product p = (Product) entry.getValue();
			scoredProducts.add(p);
		}
		
		return scoredProducts;
	}

}