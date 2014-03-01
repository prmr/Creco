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
		CategoryList catList = new CategoryList();
		
		Gson gson = new Gson();
		FileReader fr = new FileReader(aPath + aCategoryFileName);
				
		CategoryStub[] catArray = gson.fromJson(fr, CategoryStub[].class);
				
		// Note that when the franchise category gets built, its children also 
		// get built, and so on, recursively.
		for(CategoryStub catStub : catArray)
		{
			catList.addFranchise(new Category(catStub, null, 0));
		}
				
		// Build a hashtable that provides random access to the categories
		catList.index();

		return catList;
	}

	@Override
	public ProductList loadProducts() throws IOException 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
