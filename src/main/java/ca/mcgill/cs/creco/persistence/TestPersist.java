package ca.mcgill.cs.creco.persistence;

import java.io.IOException;

import ca.mcgill.cs.creco.util.*;

public class TestPersist {
	public static void main(String[] args) throws IOException {
		
		// Get the path to the data
		String dataPath = DataPath.get();

		// Use a category reader to build the categories list
		CategoryList catList = CategoryReader.read(dataPath);
		
		// Use a ProductReader to build the products list
		//ProductList prodList = ProductReader.read(dataPath);
	}
}