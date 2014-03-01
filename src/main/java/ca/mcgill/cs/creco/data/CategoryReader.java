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


import java.io.FileReader;
import java.io.IOException;

import ca.mcgill.cs.creco.data.json.CategoryStub;

import com.google.gson.Gson;

public class CategoryReader 
{
	
	public static final String getCategoryFileName() 
	{
		return "categories.json";
	}
	
	
	public static final String[] getExcludedCategories() 
	{
		return new String[] {"28985", "33546", "34458"};
		//			babies and kids,  food,     money
	}
	
	
	public static final boolean isExcluded(String catId) 
	{
		for(String excludedId : CategoryReader.getExcludedCategories())
		{
			if(catId.equals(excludedId)) 
			{
				return true;
			}
		}
		return false;
	}
	
	
	public static CategoryList read(String path, String categoryFileName, double jaccardThreshhold) throws IOException 
	{
		// Make an empty CategoryList
		CategoryList catList = new CategoryList();
		
		// Make some tools to help CategoryReader
		Gson gson = new Gson();
		FileReader fr = new FileReader(path + categoryFileName);
		
		// Read the json onto an array of categories
		CategoryStub[] catArray = gson.fromJson(fr, CategoryStub[].class);
		
		// Build these into full Category objects.
		// Note that when the franchise category gets built, its children also 
		// get built, and so on, recursively.
		for(CategoryStub catStub : catArray)
		{
			catList.addFranchise(new Category(catList, catStub, null, 0));
		}
		
		// Build a hashtable that provides random access to the categories
		catList.index();

		return catList;
	}
}