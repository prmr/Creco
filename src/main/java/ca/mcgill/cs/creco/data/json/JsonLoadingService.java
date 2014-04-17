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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.CategoryNode;
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
	private String aDeadLinksFileName;
	private String[] aProductFileNames;
	private static HashMap<String, Integer> aDeadLinks = new HashMap<String, Integer>();
	private static boolean aDoCheckDeadLinks = false;
	
	public JsonLoadingService(String pPath, String pCategoryFileName, String[] pProductFileNames, String pDeadLinksFileName)
	{
		aPath = pPath;
		aCategoryFileName = pCategoryFileName;
		aDeadLinksFileName = pDeadLinksFileName;
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
			CategoryNode outputCategory = buildCategory(inputCategory, null);
			pCollector.addCategory(outputCategory);
		}
	}
	
	// TODO: remove the pParent parameter -- it's not being used!
	private static CategoryNode buildCategory(CategoryStub pCategoryStub, CategoryNode pParent)
	{
		CategoryNode lReturn = new CategoryNode(pCategoryStub.id, pCategoryStub.singularName, pParent);
		
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
		readDeadLinks();
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
	
	private void readDeadLinks() throws FileNotFoundException, IOException
	{
		// Try to read the dead links file
		InputStream in;
		try 
		{
			in = new FileInputStream(aPath + aDeadLinksFileName);
		}
		catch (FileNotFoundException e)
		{
			return;
		}
		
		// Flag that we were succesful finding the deadlins file, so deadlinks
		// will be checked while building products
		aDoCheckDeadLinks = true;
		
		// Make a json reader for the deadlinks file
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		reader.beginArray();
		
		// Iterate over each entry in the deadlinks file, putting a records in a HashTable
		while(reader.hasNext()) 
		{
			LinkResponseStub responseStub = new Gson().fromJson(reader, LinkResponseStub.class);
			aDeadLinks.put(responseStub.product_id, responseStub.state);
		}

		reader.endArray();
		reader.close();
		in.close();
	}

	private static Product buildProduct(ProductStub pProductStub)
	{
		// Collect all of the attributes for this product.  Starting with specifications.
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		if(pProductStub.specs != null)
		{
			for(SpecStub spec : pProductStub.specs)
			{
				atts.add(Attribute.buildSpecification(spec.attributeId, spec.displayName, spec.description, spec.value));
			}
		}
		
		// Collect the ratings as Attributes.
		if(pProductStub.ratings != null)
		{
			for(RatingStub rating : pProductStub.ratings)
			{
				atts.add(Attribute.buildRating(rating.attributeId, rating.displayName, rating.description, rating.value));
			}
		}
		
		// Add the price is an Attribute
		if(pProductStub.price != null)
		{
			PriceStub price = pProductStub.price;
			atts.add(Attribute.buildPrice(price.attributeId, price.displayName, price.description, price.value));
		}
		
		// Work out the brandName
		String brandName;
		if(pProductStub.brand != null)
		{
			brandName = pProductStub.brand.displayName;
		}
		else
		{
			brandName = null;
		}
		
		// Work out the product detail URL.  All null or broken URLs are stored as empty strings
		String prodUrl = pProductStub.modelOverviewPageUrl;
		if(aDoCheckDeadLinks)
		{
			if(aDeadLinks.containsKey(pProductStub.id))
			{
				if(pProductStub.modelOverviewPageUrl == null || aDeadLinks.get(pProductStub.id) != 200)
				{
					prodUrl = "";
				}
			}
			else
			{
				prodUrl = "";
			}
		}
		
		return new Product(pProductStub.id, pProductStub.displayName, pProductStub.isTested, pProductStub.category.id, brandName, prodUrl, atts,  pProductStub.imageThumbnail, pProductStub.description);
	}

}