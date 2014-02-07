package ca.mcgill.cs.creco.persistence;

import java.util.Hashtable;

public class ProductList {
	private Hashtable<String, Product> hash = new Hashtable<String, Product>();
	
	public ProductList() {}
	
	public Product get(int key) {
		return this.hash.get(key);
	}
	
	public void put(String key, Product val) {
		this.hash.put(key, val);
	}
}
