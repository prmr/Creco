package ca.mcgill.cs.creco.data;


import ca.mcgill.cs.creco.data.stubs.CategoryStub;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CategoryReader {
	
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
		CategoryList catList = new CategoryList(jaccardThreshhold);
		
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