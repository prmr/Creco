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
package ca.mcgill.cs.creco.data.json;

import java.io.FileReader;
import java.io.IOException;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.CategoryList;
import ca.mcgill.cs.creco.data.IDataLoadingService;
import ca.mcgill.cs.creco.data.ProductList;

import com.google.gson.Gson;

/**
 * A service to load the Consumer Reports data from JSON files.
 */
public class JsonLoadingService implements IDataLoadingService 
{
	private String aPath;
	private String aCategoryFileName;
	
	public JsonLoadingService(String pPath, String pCategoryFileName)
	{
		aPath = pPath;
		aCategoryFileName = pCategoryFileName;
	}
	
	@Override
	public CategoryList loadCategories() throws IOException 
	{
		CategoryStub[] inputCategories = new Gson().fromJson(new FileReader(aPath + aCategoryFileName), CategoryStub[].class);
		CategoryList outputCategories = new CategoryList();
				
		// Note that when the franchise category gets built, its children also 
		// get built, and so on, recursively.
		for(CategoryStub inputCategory : inputCategories)
		{
			Category outputCategory = buildCategory(inputCategory, null);
			outputCategories.addFranchise(outputCategory);
		}
				
		// Build a hashtable that provides random access to the categories
		outputCategories.index();

		return outputCategories;
	}
	
	private Category buildCategory(CategoryStub pCategoryStub, Category pParent)
	{
		Category lReturn = new Category(pCategoryStub.id, pCategoryStub.singularName, pParent);
		
		if(pCategoryStub.downLevel != null)
		{
			// figure out where the children are, if any
			CategoryStub[] childCatStubs = new CategoryStub[0];
			
			if(pCategoryStub.downLevel.supercategory != null)
			{
				childCatStubs = pCategoryStub.downLevel.supercategory;
			}
			else if(pCategoryStub.downLevel.category != null)
			{
				childCatStubs = pCategoryStub.downLevel.category;
			}
			else if(pCategoryStub.downLevel.subfranchise != null)
			{
				childCatStubs = pCategoryStub.downLevel.subfranchise;
			}
			else if(pCategoryStub.downLevel.subcategory != null)
			{
				childCatStubs = pCategoryStub.downLevel.subcategory;
			}
						
			for(CategoryStub childCatStub : childCatStubs)
			{
				lReturn.addChild(buildCategory(childCatStub, lReturn));
			}
		}
		
		return lReturn;
	}

	@Override
	public ProductList loadProducts() throws IOException 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
