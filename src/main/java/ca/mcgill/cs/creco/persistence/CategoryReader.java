package ca.mcgill.cs.creco.persistence;


import org.json.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;

public class CategoryReader {
/* This is a factory for building a CategoryList from a json file representing 
 * the category tree, in the format returned from the CR API
 */
	
	private static final String filename = "categories.json";
	
	private static final String[] getExpectedStringFields() {
		String[] expectedStringFields = {"pluralName", "parent", "productGroupId", 
			"url", "name", "id", "imageCanonical", "singularName", 
			"franchise", "type", "children"};
		return expectedStringFields;
	}
	
	private static final String[] getExpectedIntFields() {
		String[] expectedInts = {"materialsCount", "depth", "productsCount", 
			"testedProductsCount", "servicesCount", "ratedProductsCount"};
		return expectedInts;
	}
	
	public static CategoryList read(String fname) throws IOException 
	{
		// Read the categories Tree, as returned from CR API, into a Java object
		FileReader reader;
		reader = new FileReader(fname + CategoryReader.filename);
		JSONTokener tokener = new JSONTokener(reader);
		JSONArray a = new JSONArray(tokener);
		
		// Make an empty CategoryList
		CategoryList catList = new CategoryList();
		
		// For each category in the Json, make a Category, and put it in the 
		// CategoryList
		int i;
		for(i=0; i<a.length(); i++) {
			CategoryReader.recursePutCategory(a.getJSONObject(i), catList, 0);
		}
		
		return catList;
	}
	
	/**
	 * Given a JSONObject representing a category at any level, turn it into a 
	 * Category, put it in the Categorylist, and recurse on the JSONObject 
	 * category's children.
	 * 
	 * @param subtree
	 * 		A JSONObject built from the CR API's nested JSON representation of 
	 * 		catogories
	 */
	private static void recursePutCategory(JSONObject subtree, CategoryList catList, int depth) {

		// Build the category for the root of the given subtree
		Category cat = CategoryReader.buildCat(subtree);
		String id = cat.get_id();
		JSONArray jsonChildren = CategoryReader.getChildren(subtree);
		catList.put(id, cat);
		
		// Print a message to show build progress
		String msg = cat.getSingularName();
		int i;
		for(i=0; i<depth; i++) {
			msg = "\t" + msg;
		}
		System.out.println(msg);
				
		// Recurse on the children, if any
		for(i=0; i<jsonChildren.length(); i++) {
			JSONObject childSubtree = jsonChildren.getJSONObject(i);
			CategoryReader.recursePutCategory(childSubtree, catList, depth+1);
		}
		
	}
	
	private static Category buildCat(JSONObject jsonCat) {
		// Make an empty category
		Category cat = new Category();
		
		// Copy all of the string fields the jsonCat to the Category
		String[] strFields = CategoryReader.getExpectedStringFields();
		int i = 0;
		for(i=0; i<strFields.length; i=i+1) {
			String key = strFields[i];
			if(jsonCat.has(key)) {
				cat.setString(key, jsonCat.getString(key));
			} else {
				cat.setString(key,  null);
			}
		}
		
		// Copy all of the Integer fields from the jsonCat to the Category
		String[] intFields = CategoryReader.getExpectedIntFields();
		for(i=0; i<intFields.length; i=i+1) {
			String key = intFields[i];
			if(jsonCat.has(key)) {
				cat.setInt(key, jsonCat.getInt(key));
			} else {
				cat.setInt(key,  null);
			}
		}
		
		// Copy the children from the jsonCat to the Category
		JSONArray jsonChildren = CategoryReader.getChildren(jsonCat);
		String[] children;
		if(jsonChildren != null) {
			children = new String[jsonChildren.length()];
			for(i=0; i<jsonChildren.length(); i++) {
				JSONObject jsonChildCat = jsonChildren.getJSONObject(i);
				children[i] = jsonChildCat.getString("id");
			}
		} else {
			children = new String[0];
		}
		cat.setChildren(children);
		
		return cat;
	}
		
	private static JSONArray getChildren(JSONObject jsonCat) {
		// if the jsonCat has no children, return an empty JSONArray
		if(!jsonCat.has("downLevel")) {
			return new JSONArray();
		}
		
		JSONObject downLevel = jsonCat.getJSONObject("downLevel");
		String[] downLevelFields = JSONObject.getNames(downLevel);
		
		// It is still possible there are no children!
		if(downLevelFields == null) {
			return new JSONArray();
		}
		
		return downLevel.getJSONArray(downLevelFields[0]);

	}
		
		
		
	
}