package ca.mcgill.cs.creco.persistence;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import ca.mcgill.cs.creco.util.*;

public class TestPersist {
	public static void main(String[] args) throws IOException, ParseException {
		
		// Get the path to the data
		String dataPath = DataPath.get();

		// Use a category reader to build the categories list
		CategoryList catList = CategoryReader.read(dataPath);
		catList.eliminateSingletons();
		
		// Use a ProductReader to build the products list
		ProductList prodList = ProductReader.read(dataPath);
		
		// Put links from products to categories and vice-versa
		// Also, collect 
		catList.associateProducts(prodList);
		catList.refresh();
		
		System.out.println(catList.dumpTree());
		for(Spec rating : catList.get("34506").getSpecs())
		{
			System.out.println(rating.getAttributeName());
		}
	}
}