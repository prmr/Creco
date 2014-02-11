package ca.mcgill.cs.creco.persistence;


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
	
	
	public static CategoryList read(String path, double jaccardThreshhold) throws IOException 
	{
		// Make an empty category
		CategoryList catList = new CategoryList(jaccardThreshhold);
		
		// Make some tools to help CategoryReader
		Gson gson = new Gson();
		FileReader fr = new FileReader(path + CategoryReader.getCategoryFileName());
		
		// Read the json onto an array of categories
		Category[] catArray = gson.fromJson(fr, Category[].class);
		
		// The catArray is deep.  
		// Recursively flatten it, putting Category's in catList.
		for(Category cat : catArray)
		{
			CategoryReader.recursePutCategory(cat, catList, 0, null);
		}
		
		catList.refresh();

		return catList;
	}
	
	
	/**
	 * Given a deep category, as is returned when parsing the CR categories.json file,
	 * Traverse it and put it and all subcategories into a flat CategoryList
	 *  
	 * @param cat
	 * 		a deep category
	 * 
	 * @param catList
	 * 		A flat list of categories, onto which the found Category's will be added
	 * 
	 * @depth
	 * 		The depth of the passed Category.  Franchises should be passed with depth 0
	 * 
	 */
	private static void recursePutCategory(Category cat, CategoryList catList, int depth, Category parent) {

		// Check whether this category should be included
		if(CategoryReader.isExcluded(cat.getId()))
		{
			return;
		}
		
		// Work on this level.  Set depth, parent, and put the category in the catList
		cat.setInt("depth", depth);
		cat.setParent(parent);
		cat.setCatList(catList);
		cat.setRatings(new ArrayList<RatingStat>());
		cat.setSpecs(new ArrayList<SpecStat>());
		
		String id = cat.getString("id");
		catList.put(id, cat);
		
		// Call recursively on children
		Category[] catArray = cat.getDownLevel();
		if(catArray != null)
		{
			for(Category childCat : cat.getDownLevel()) 
			{
				cat.addChild(childCat);
				CategoryReader.recursePutCategory(childCat, catList, depth + 1, cat);
			}
		}
	}
	
	
		
}