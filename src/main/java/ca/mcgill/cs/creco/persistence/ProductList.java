package ca.mcgill.cs.creco.persistence;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

public class ProductList {
	private Hashtable<String, Product> hash = new Hashtable<String, Product>();
	
	public ProductList() {}
	
	public Product get(int key) 
	{
		return this.hash.get(key);
	}
	
	public void put(String key, Product val) 
	{
		this.hash.put(key, val);
	}
	
	public int size()
	{
		return this.hash.size();
	}
	
	public Iterator<Entry<String, Product>> getIterator() {
		Iterator<Entry<String, Product>> it = hash.entrySet().iterator();
		return it;
	}
}
