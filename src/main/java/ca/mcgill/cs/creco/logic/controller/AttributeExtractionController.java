/**
 * 
 */
package ca.mcgill.cs.creco.logic.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.ProductList;
import ca.mcgill.cs.creco.data.SpecStat;
import ca.mcgill.cs.creco.data.Product;


 /**
 * This class handles the extraction of most relevant attribute from an equivalence class
 * with respect to a product list.
 * 
 * @see ProductList
 * @see Category
 * @see Attribute
 */

public class AttributeExtractionController
{

	private ProductList aProductList;
	private Category aEquivalenceClass;
	private Iterable<SpecStat> aSpecList;
	
	public AttributeExtractionController(ProductList pProductList, Category pEquivalenceClass)
	{
		aProductList = pProductList;
		aEquivalenceClass = pEquivalenceClass;
		aSpecList = aEquivalenceClass.getSpecs();
	}
		
	private void generateAttributeList()
	{
		updateRelativeAttributeScores();
//		aAttributeList.sort();
		
	}
	
	private void updateRelativeAttributeScores()
	{
//		this is where the magic happens
//		make instances object
//				
		FastVector fvAttributeList = new FastVector();
		for(SpecStat a : aSpecList)
		{
//			numerical attribute
			if(a.getValueMax())
			
			Attribute newAttribute = new Attribute(a.getName());
			fvAttributeList.addElement(newAttribute);	
			
//			nominal attribute
		}
		Instances dataset = new Instances("attributes", fvAttributeList, aProductList.size());
//		
//		make all instances and add them to the instances object
//		
		
		for (Iterator<Entry<String,Product>> it = aProductList.getIterator(); it.hasNext(); )
		{
			Entry<String,Product> e = it.next();
			Product p = e.getValue();
			String key = e.getKey();
			weka.core.Instance i = new Instance(aSpecList.size());
			for(Object a : fvAttributeList.toArray())
			{
				
			}
			
		}
//		for(Product p : aProductList )
//		{
//			
//		}
//		
//		use tree to extract feature importance
		
	}
		
	
	public ProductList getProductList() 
	{
		return aProductList;
	}

	public void setProductList(ProductList aProductList) 
	{
		this.aProductList = aProductList;
	}

	public Category getEquivalenceClass() 
	{
		return aEquivalenceClass;
	}

	public void setEquivalenceClass(Category aEquivalenceClass) 
	{
		this.aEquivalenceClass = aEquivalenceClass;
	}

//	public List getAttributeList() 
//	{
//		generateAttributeList();
//		return aAttributeList;
//	}


	
}
