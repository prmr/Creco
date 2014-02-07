package ca.mcgill.cs.creco.persistence;

import org.json.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;

public class CategoryList {
	private Hashtable<String, Category> hash = new Hashtable<String, Category>();
	
	public CategoryList() {}
	
	public Category get(int key) {
		return this.hash.get(key);
	}
	
	public void put(String key, Category val) {
		this.hash.put(key, val);
	}
}
