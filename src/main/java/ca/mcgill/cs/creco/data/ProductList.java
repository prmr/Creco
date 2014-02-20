package ca.mcgill.cs.creco.data;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

public class ProductList implements Iterable<Product> 
{
	private Hashtable<String, Product> hash = new Hashtable<String, Product>();
	
	public ProductList() {}
	
	public Product get(String key) 
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
	
	public Iterator<Product> iterator() 
	{
		return Collections.unmodifiableCollection(hash.values()).iterator();
	}

	
}
