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

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataCollector;
import ca.mcgill.cs.creco.data.IDataLoadingService;
import ca.mcgill.cs.creco.data.Product;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * A service to load the Consumer Reports data from JSON files.
 */
public class JsonLoadingService implements IDataLoadingService 
{
	private String aPath;
	private String aCategoryFileName;
	private String[] aProductFileNames;
	
	public JsonLoadingService(String pPath, String pCategoryFileName, String[] pProductFileNames)
	{
		aPath = pPath;
		aCategoryFileName = pCategoryFileName;
		aProductFileNames = pProductFileNames;
	}
	
	@Override
	public void loadCategories(IDataCollector pCollector) throws IOException 
	{
		CategoryStub[] inputCategories = new Gson().fromJson(new FileReader(aPath + aCategoryFileName), CategoryStub[].class);
				
		// Note that when the franchise category gets built, its children also 
		// get built, and so on, recursively.
		for(CategoryStub inputCategory : inputCategories)
		{
			Category outputCategory = buildCategory(inputCategory, null);
			pCollector.addCategory(outputCategory);
		}
	}
	
	private static Category buildCategory(CategoryStub pCategoryStub, Category pParent)
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
				lReturn.addSubcategory(buildCategory(childCatStub, lReturn));
			}
		}
		
		return lReturn;
	}

	@Override
	public void loadProducts(IDataCollector pCollector) throws IOException 
	{
		for(String fileName : aProductFileNames)
		{
			readFile(aPath + fileName, pCollector);
		}
	}
	
	private static void readFile(String filePath, IDataCollector pCollector) throws IOException
	{
		InputStream in = new FileInputStream(filePath);
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
				
		reader.beginArray();
		while(reader.hasNext()) 
		{
			ProductStub prodStub = new Gson().fromJson(reader, ProductStub.class);
			pCollector.addProduct(buildProduct(prodStub));
			
		}
		reader.endArray();
		reader.close();
		in.close();
	}
	
	private static Product buildProduct(ProductStub pProductStub)
	{
		Product lReturn ;
		
		if(pProductStub.brand != null)
		{
			 lReturn = new Product(pProductStub.id, pProductStub.displayName, pProductStub.isTested, pProductStub.category.id, pProductStub.brand.displayName);
		}
		else
		{
			 lReturn = new Product(pProductStub.id, pProductStub.displayName, pProductStub.isTested, pProductStub.category.id, null);
		}
		
		if(pProductStub.specs != null)
		{
			for(SpecStub spec : pProductStub.specs)
			{
				lReturn.addSpec(new Attribute(spec.attributeId, spec.displayName, spec.description, spec.value));
			}
		}
		
		if(pProductStub.ratings != null)
		{
			for(RatingStub rating : pProductStub.ratings)
			{
				lReturn.addRating(new Attribute(rating.attributeId, rating.displayName, rating.description, rating.value));
			}
		}
		return lReturn;
	}

}