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
package ca.mcgill.cs.creco.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.search.ICategorySearch;

/**
 * Default implementation of the service layer.
 */
@Component
public class ConcreteServiceFacade implements ServiceFacade 
{
	private static final int MIN_NUMBER_OF_TYPED_LETTERS = 3;
	
	@Autowired
	private IDataStore aDataStore;
	
	@Autowired
	private ICategorySearch aCategorySearch;

	@Override
	public String getCompletions(String pInput)
	{
		String response = "";
		
		if(pInput.length() >= MIN_NUMBER_OF_TYPED_LETTERS)
		{
			for(Category category : aDataStore.getCategories()) 
			{
				if(category.getNumberOfProducts() > 0 && category.getName().toLowerCase().contains(pInput.toLowerCase()))
				{
					response = response.concat(category.getName() + ",");
				}
			}
		
			Set<String> collectedtillnow = new HashSet<String>();
			for(Product productname : aDataStore.getProducts()) 
			{
				if(productname.getName().toLowerCase().contains(pInput.toLowerCase()))
				{
					for (String productspace: productname.getName().toLowerCase().split(" "))
					{
						if(productspace.contains(pInput.toLowerCase()) && !collectedtillnow.contains(productspace))
						{
							collectedtillnow.add(productspace);		
							response = response.concat(productspace+",");
						}
					}
				}
			}
		
			for(Product product : aDataStore.getProducts()) 
			{
				if(product.getName().toLowerCase().contains(pInput.toLowerCase()))
				{
					response = response.concat(product.getName()+ ",");
				}
			}
		}
		return response;
	}

	@Override
	public Collection<Category> searchCategories(String pQuery)
	{
		return aCategorySearch.queryCategories(pQuery);
	}

	@Override
	public Category getCategory(String pId) 
	{
		return aDataStore.getCategory(pId);
	}
}
