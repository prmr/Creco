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
import java.util.List;
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
	private static final int MIN_NUMBER_OF_TYPED_LETTERS = 2;
	
	@Autowired
	private IDataStore aDataStore;
	
	@Autowired
	private ICategorySearch aCategorySearch;
	
	@Autowired
	private ProductRanker aProductRanker;

	@Override
	public String getCompletions(String pInput)
	{
		if(pInput.length() <= MIN_NUMBER_OF_TYPED_LETTERS)
		{
			return "";
		}

		String response = "";

		for(Category category : aDataStore.getCategories()) 
		{
			if(category.getNumberOfProducts() == 0)
			{
				continue;
			}
			if(category.getName().toLowerCase().contains(pInput.toLowerCase()))
			{
				response = response.concat(category.getName() + "| " +"Category"+"| ");
			}
		}
		Set<String> collectedbrandstillnow = new HashSet<String>();
		Set<String> collectedtexttillnow = new HashSet<String>();
		Set<String> Brands = new HashSet<String>();
		Set<String> Text_search = new HashSet<String>();
		for (Product productname : aDataStore.getProducts()) 
		{
			if(productname.getName().toLowerCase().contains(pInput.toLowerCase()))
			{
				for (String productspace: productname.getName().toLowerCase().split(" "))
				{
					if(productspace.contains(pInput.toLowerCase()))
					{
						if(productspace.equals(productname.getBrandName().toLowerCase()))
						{
							if(collectedbrandstillnow.contains(productspace))
							{
								
							}
							else
							{
							collectedbrandstillnow.add(productspace);
							Brands.add(productspace);
							}
						}
						else if(collectedtexttillnow.contains(productspace)||collectedbrandstillnow.contains(productspace))
							{
							}
						else
						{
							collectedtexttillnow.add(productspace);
								int count=0;
								for(int i=0;i<productspace.length();i++)
								{
									if(Character.isDigit(productspace.charAt(i)))
										count++;
								}
								if(count<2&&!productspace.contains("(")&&!productspace.contains(")"))
									Text_search.add(productspace);
						}
					}
				}
			}
		}
		
for(String brandname : Brands)
		response = response.concat(brandname+"| " +"Brand" +"| ");

for(String textname : Text_search)
	response = response.concat(textname+"| " +"Text Search" +"| ");
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
	
	@Override
	public List<Product> rankProducts(List<ScoredAttribute> pScoredAttributes, Collection<Product> pProducts)
	{
		return aProductRanker.rankProducts(pScoredAttributes, pProducts);
	}
}
