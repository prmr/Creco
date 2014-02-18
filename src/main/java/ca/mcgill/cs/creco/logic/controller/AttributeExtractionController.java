/**
 * 
 */
package ca.mcgill.cs.creco.logic.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Lists;
//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;












import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.PrincipalComponents;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.UnsupervisedAttributeEvaluator;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import ca.mcgill.cs.creco.data.CRData;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.CategoryList;
import ca.mcgill.cs.creco.data.ProductList;
import ca.mcgill.cs.creco.data.Spec;
import ca.mcgill.cs.creco.data.SpecStat;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedVal;
import ca.mcgill.cs.creco.logic.model.AttributeValue;
import ca.mcgill.cs.creco.logic.model.ScoredAttribute;


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

	private List<Product> aProductList;
	private Category aEquivalenceClass;
	private Iterable<SpecStat> aSpecList;
	private ArrayList<ScoredAttribute> aScoredAttributeList;
	
	public static void main(String[] args) throws Exception
	{
		
		CRData data = new CRData();
		CategoryList catList = data.getCategoryList();
		
		Category humidifiers = catList.get("32968");
		Iterable<Product> products = humidifiers.getProducts();
		
		List<Product> productList = Lists.newArrayList(products);
		
		AttributeExtractionController aec = new AttributeExtractionController(productList,humidifiers);
		ArrayList<ScoredAttribute> sal = aec.getScoredAttributeList();
		
		System.out.println(sal);
		
		
	}
	
	public static AttributeValue extractMean(List<Product> pProductList, String attributeID)
	{
		double numericCount = 0;
		double numericSum = 0;
		int trueCount = 0;
		int falseCount = 0;
		HashMap<String, Integer> nominalCounts = new HashMap<String, Integer>();
		for( Product p : pProductList)
		{
			Spec s = p.getSpec(attributeID);
			if( s != null)
			{
				String specString = s.getValue().toString();
				String type = s.getType();
				if(type.equals("int") || type.equals("double") || type.equals("float"))
				{
					numericCount ++;
					numericSum += Double.parseDouble(specString);					
				}
				else if (type.equals("boolean"))
				{
					if(specString.equals("true"))
					{
						trueCount ++;
					}
					else{
						falseCount ++;
					}
				}
				else
				{
					int count = nominalCounts.containsKey(specString) ? nominalCounts.get(specString) : 0;
					nominalCounts.put(specString, count + 1);
				}
			}
			
		}
		if(numericCount > 0)
		{
			return new AttributeValue(numericSum/numericCount);
		}
		else if (trueCount > 0 || falseCount > 0)
		{
			if(trueCount > falseCount) return new AttributeValue(true);
			return new AttributeValue(false);
			
		}
		//to change
		String maxAtt = "N/A";
		int max = 0;
		for(String key : nominalCounts.keySet())
		{
			int count = nominalCounts.get(key);
			if(count > max)
			{
				maxAtt = key;
				max = count;			
			}
		}
		return new AttributeValue(maxAtt);
	}
	
	public AttributeExtractionController(List<Product> pProductList, Category pEquivalenceClass)
	{
		aProductList = pProductList;
		aEquivalenceClass = pEquivalenceClass;
		aSpecList = aEquivalenceClass.getSpecs();
	}
		
	private void generateAttributeList()
	{
		updateRelativeAttributeScores();
		Collections.sort(aScoredAttributeList, ScoredAttribute.sortByScore);		
	}
	
	private void updateRelativeAttributeScores()
	{
//		this is where the magic happens
//		make instances object
//		to keep track of which attributes are being taken into account		
		ArrayList<ScoredAttribute> scoredAttributes = new ArrayList<ScoredAttribute>();
		FastVector attributeVector = new FastVector();
		for(SpecStat a : aSpecList)
		{
			
//			check value type
			if(a.getValueMin() != null && a.getValueEnum().size() > 0)
			{
				continue;
			}
//			numerical attribute
			else if(a.getValueMin() != null)
			{
				Attribute newAttribute = new Attribute(a.getName());
				scoredAttributes.add(new ScoredAttribute(a.getAttribute()));
				attributeVector.addElement(newAttribute);
			}
//			nominal attribute
			else if(a.getValueEnum().size() > 0)
			{

				FastVector nominalValues = new FastVector();
//				add the N/A String in case the object doesn't have the attribute
				nominalValues.addElement("N/A");
				for (String value : a.getValueEnum())
				{
					nominalValues.addElement(value);
				}
				Attribute newAttribute = new Attribute(a.getName(),nominalValues);
				scoredAttributes.add(new ScoredAttribute(a.getAttribute()));
				attributeVector.addElement(newAttribute);
			}

		}
		Instances dataset = new Instances("attributes", attributeVector, aProductList.size());
//		
//		make all instances and add them to the instances object
//		
		
		
		for (Iterator<Product> it = aProductList.iterator(); it.hasNext(); )
		{		
		
			Product p = it.next();
			Instance inst = new Instance(attributeVector.size()); 
			for(ScoredAttribute att : scoredAttributes)
			{
				Attribute wekaAtt = dataset.attribute(att.getAttributeName());
				
				String type = "";
				String value = "";
				try
				{
					type = p.getSpec(att.getAttributeID()).getType();
					value = p.getSpec(att.getAttributeID()).getValue().toString();
					//System.out.println("VAL: " + value);
				}
				catch(NullPointerException npe)
				{
					if(wekaAtt.isNominal())
					{
//					entry zero should be N/A
						type = "String";
						value = wekaAtt.value(0);
					}
					else
					{
//						default no value for now 
						type = "int";
						value = "-127";
					}
				}
				
				
				if(type.equals("int") || type.equals("double") || type.equals("float"))
				{
					inst.setValue(wekaAtt, Double.parseDouble(value));
				}
				else
				{
					inst.setValue(wekaAtt, value);
				}	
			}
			inst.setDataset(dataset); 
			dataset.add(inst);
		}
		
//		use tree to extract feature importance
		try
		{
			AttributeSelection attsel = new AttributeSelection();  // package weka.attributeSelection!
			//CfsSubsetEval eval = new CfsSubsetEval();
			PrincipalComponents eval = new PrincipalComponents();
			//GreedyStepwise search = new GreedyStepwise();
			//search.setSearchBackwards(true);
			Ranker search=new Ranker();
			attsel.setEvaluator(eval);
			attsel.setSearch(search);
			attsel.SelectAttributes(dataset);
			double[][] meritScores = attsel.rankedAttributes();
			//System.out.println(Utils.arrayToString(meritScores));
			for(double[] score :meritScores)
			{
				scoredAttributes.get((int)score[0]).setAttributeScore(score[1]);
			}
			for(ScoredAttribute sa: scoredAttributes)
			{
				sa.setAttributeMean(AttributeExtractionController.extractMean(aProductList, sa.getAttributeID()));
			}
			
//			InfoGainAttributeEval eval = new InfoGainAttributeEval();
//			AttributeSelection trainSelector = new	AttributeSelection();
//			Ranker searchMethod=new Ranker();
//            trainSelector.setSearch(searchMethod);
//			trainSelector.setEvaluator(eval);
//			trainSelector.SelectAttributes(dataset);
//			String Results = trainSelector.toResultsString();
//			System.out.println(Results);
		}
		catch(Exception e)
		{
			System.out.println("Weka ERROR:\n" + e);
		}
		aScoredAttributeList = scoredAttributes;
		
		
	}
		
	
	public List<Product> getProductList() 
	{
		return aProductList;
	}

	public void setProductList(List<Product> pProductList) 
	{
		this.aProductList = pProductList;
	}

	public Category getEquivalenceClass() 
	{
		return aEquivalenceClass;
	}

	public void setEquivalenceClass(Category pEquivalenceClass) 
	{
		this.aEquivalenceClass = pEquivalenceClass;
	}

	public ArrayList<ScoredAttribute> getScoredAttributeList() 
	{
		generateAttributeList();
		return aScoredAttributeList;
	}


	
}
