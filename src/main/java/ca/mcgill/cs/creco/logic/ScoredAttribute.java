/**
 * Copyright 2014 McGill University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;

import com.google.common.collect.Lists;

/**
 *
 */
public class ScoredAttribute
{
	
	
	private static final double CONSIDERATION_THRESHOLD = 0.8;
	private static final double DEFAULT_MIN = 10000000;
	private static final double DEFAULT_MAX = -10000000;
	public static enum Direction {MORE_IS_BETTER, LESS_IS_BETTER};
	private final Logger logger = LoggerFactory.getLogger(ScoredAttribute.class);

	
	public static final Comparator<ScoredAttribute> SORT_BY_CORRELATION = 
			new Comparator<ScoredAttribute>() 
		    {
				
				/**
				 * Compare a scoredAttribute placing the highest absolute correlation first.
				 * @param pA scoredAttribute A
				 * @param pB scoredAttribute B
				 * @return -1 for A > B, 1 for B > A, 0 if A==B
				 */
				@Override
		        public int compare(ScoredAttribute pA, ScoredAttribute pB) 
		        {
		        	if(Math.abs(pA.getCorrelation()) >  Math.abs(pB.getCorrelation()))
		        	{
		        		return -1;
		        	}
		        	else if(Math.abs(pA.getCorrelation()) <  Math.abs(pB.getCorrelation()))
		        	{
		        		return 1;
		        	}
		        	else
		        	{
		        		return 0;
		        	}
		        }

		     };
	public static final Comparator<ScoredAttribute> SORT_BY_SCORE = 
	new Comparator<ScoredAttribute>() 
    {
		
		/**
		 * Compare a scoredAttribute placing the highest score first.
		 * @param pA scoredAttribute A
		 * @param pB scoredAttribute B
		 * @return -1 for A > B, 1 for B > A, 0 if A==B
		 */
		@Override
        public int compare(ScoredAttribute pA, ScoredAttribute pB) 
        {
        	if(pA.getAttributeScore() >  pB.getAttributeScore())
        	{
        		return -1;
        	}
        	else if(pA.getAttributeScore() < pB.getAttributeScore())
        	{
        		return 1;
        	}
        	else
        	{
        		return 0;
        	}
        }

     };
     /**
		 * Compare a scoredAttribute placing the highest entropy first.
		 * @param pA scoredAttribute A
		 * @param pB scoredAttribute B
		 * @return -1 for A > B, 1 for B > A, 0 if A==B
		 */
     public static final Comparator<ScoredAttribute> SORT_BY_ENTROPY = 
    			new Comparator<ScoredAttribute>() 
    		    {
    				
    				/**
    				 * Compare a score placing the highest score first.
    				 * @param pA score A
    				 * @param pB score B
    				 * @return -1 for A > B, 1 for B > A, 0 if A==B
    				 */
    				@Override
    		        public int compare(ScoredAttribute pA, ScoredAttribute pB) 
    		        {
    		        	if(pA.getEntropy() >  pB.getEntropy())
    		        	{
    		        		return -1;
    		        	}
    		        	else if(pA.getEntropy() < pB.getEntropy())
    		        	{
    		        		return 1;
    		        	}
    		        	else
    		        	{
    		        		return 0;
    		        	}
    		        }

    		     };
 	@Autowired
 	private IDataStore aDataStore;
    private String aAttributeID;
 	private String aAttributeName;
 	private double aAttributeScore;
 	private TypedValue aDefaultValue;

 	private String aCategoryID;
 	private String aAttributeDesc;
 	private double aEntropy;
 	private double aCorrelation;
 	private Direction aDirection;
 	
 	private TypedValue aMin;
 	private TypedValue aMax;
 	private List<TypedValue> aStringValues;

	   
 	private enum Type
 	{ NULL, NA, BOOLEAN, NUMERIC, STRING }

	/**Constructor from an attribute.
	 * @param pAttribute attribute to build score for.
	 * @param Category in which the attribute is present
	 */
	public ScoredAttribute(Attribute pAttribute, Category pCat)
	{

		aAttributeID = pAttribute.getId();
		aAttributeScore = 0.0;
		aAttributeName = pAttribute.getName();
		aAttributeDesc = pAttribute.getDescription();
		aEntropy = 0;
		aCorrelation = 0;
		if(pCat != null)
		{
			Collection<Product> products = pCat.getProducts();
			setStats(products);
			AttributeCorrelator ac = new AttributeCorrelator(pCat);
			aCorrelation = ac.computeCorrelation(aAttributeID);
			aDirection = ac.computeAttributeDirection(aAttributeID);
			
		}
		else
		{
			aDefaultValue = new TypedValue();
			aCorrelation = 0;
			aDirection = Direction.MORE_IS_BETTER;
		}
		

	}
	private void setStats(Collection<Product> pProducts)
	{
		ArrayList<TypedValue> values = new ArrayList<TypedValue>();
		for(Product p : pProducts)
		{
			
			//Warning: change after collapse into attribute
			Attribute a = p.getSpec(aAttributeID);
			if(a == null)
			{
				a = p.getRating(aAttributeID);
			}
			if( a != null)
			{
				values.add(a.getTypedValue());
			}
			
		}
		// goes only over the products that had a non null attribute value
		Type attributeMainType = getMainType(values);
		if(attributeMainType == Type.NUMERIC)
		{
			setNumericStats(values);
		}
		else if(attributeMainType == Type.BOOLEAN)
		{
			setBooleanStats(values);
		}
		else if(attributeMainType == Type.STRING)
		{
			setStringStats(values);
		}
		else
		{
			aEntropy = 0;
			aDefaultValue = new TypedValue();
		}
	}
	
	private void setNumericStats(ArrayList<TypedValue> pValues)
	{
		SummaryStatistics ss = new SummaryStatistics();
		double min = DEFAULT_MIN;
		double max = DEFAULT_MAX;
		for(TypedValue tv : pValues)
		{
			if(tv.isNumeric())
			{
				ss.addValue(tv.getNumeric());
				if(tv.getNumeric() < min)
				{
					min = tv.getNumeric();
					
				}
				if(tv.getNumeric() > max)
				{
					max = tv.getNumeric();
					
				}
			}
		}
		if(Double.isNaN(min))
		{
			logger.error("Min value is NaN: " + aAttributeID +", "+ aAttributeName + ", "+ aCategoryID);
		}
		if(Double.isNaN(max))
		{
			logger.error("Max value is NaN: " + aAttributeID +", "+ aAttributeName + ", "+ aCategoryID);
		}
		aMin = new TypedValue(min);
		aMax = new TypedValue(max);
		double mean = ss.getGeometricMean();
		double variance = ss.getStandardDeviation()*ss.getStandardDeviation();
		
		double entropy = 0;
		for(TypedValue tv : pValues)
		{
			if(tv.isNumeric())
			{
				double prob = computeNormalProbability(tv, mean, variance);
				entropy = entropy - prob * (Math.log(prob));
			}
		}
		aDefaultValue = new TypedValue(mean);
		if(!Double.isNaN(entropy))
		{
			aEntropy = entropy;
		}
		else
		{
			aEntropy = 0;
		}
		
	}
	
	private void setStringStats(ArrayList<TypedValue> pValues)
	{
		HashMap<String, Double> stringCounts = new HashMap<String, Double>();
		ArrayList<TypedValue> dictionary = new ArrayList<TypedValue>();
		double count;
		double totalCount = 0;
		for(TypedValue tv : pValues)
		{
			if(tv.isString())
			{
				totalCount += 1;
				if (stringCounts.containsKey(tv.getString()))
				{
					count = stringCounts.get(tv.getString());
				}
				else
				{
					count = 0;
				}
				stringCounts.put(tv.getString(), count + 1);
			}
		}
		double entropy = 0;
		double maxCount = 0;
		String mode = "";
		for(String key : stringCounts.keySet())
		{
			dictionary.add(new TypedValue(key));
			if(stringCounts.get(key) > maxCount)
			{
				mode = key;
				maxCount = stringCounts.get(key);
			}
			double probKey = stringCounts.get(key)/totalCount;
			entropy = entropy - probKey * (Math.log(probKey));
		}
		aStringValues = dictionary;
		aDefaultValue = new TypedValue(mode);
		if(!Double.isNaN(entropy))
		{
			aEntropy = entropy;
		}
		else
		{
			aEntropy = 0;
		}
		
	}
	
	private void setBooleanStats(ArrayList<TypedValue> pValues)
	{
		double trueCount = 0;
		double totalCount = 0;
		for(TypedValue tv : pValues)
		{
			if(tv.isBoolean())
			{
				totalCount = totalCount + 1;
				if (tv.getBoolean())
				{
					trueCount = trueCount + 1;
				}
			}
		}
		double entropy = 0;
		boolean mode = false;
		double probTrue= trueCount/totalCount;
		double probFalse= 1- probTrue;
		entropy = -probTrue * (Math.log(probTrue)) - probFalse * (Math.log(probFalse));
		if(trueCount >= totalCount/2)
		{
			mode = true;
		}
		
		aDefaultValue = new TypedValue(mode);
		if(!Double.isNaN(entropy))
		{
			aEntropy = entropy;
		}
		else
		{
			aEntropy = 0;
		}
		
	}
 
	private double computeNormalProbability(TypedValue pValue, double pMean, double pVariance)
	{
		if(!pValue.isNumeric())
		{
			return 0.0;
		}
		double x = pValue.getNumeric();
		double probability;
		probability = 1/(Math.sqrt(2*Math.PI*pVariance)) * Math.exp(-((x-pMean)*(x-pMean))/2*pVariance);

		return probability;
	}

	
	private Type getMainType(ArrayList<TypedValue> pValues)
	{
		float stringCount = 0;
		float numericCount = 0;
		float booleanCount = 0;
		for(TypedValue tv : pValues)
		{
			if(tv.isBoolean())
			{
				booleanCount += 1;
			}
			else if (tv.isNumeric())
			{
				numericCount += 1;
			}
			else if(tv.isString())
			{
				stringCount += 1;
			}
		}
		float totalCount = pValues.size();
		if((booleanCount/totalCount) >=CONSIDERATION_THRESHOLD)
		{
			return Type.BOOLEAN;
		}
		else if((numericCount/totalCount) >=CONSIDERATION_THRESHOLD)
		{
			return Type.NUMERIC;
		}
		else if((stringCount/totalCount) >=CONSIDERATION_THRESHOLD)
		{
			return Type.STRING;
		}
		else
		{
			return Type.NA;
		}
	}
	
	
	/**
	 * @return attribute or category ID check first
	 * @see isCat()
	 */
	public String getAttributeID() 
	{
		return aAttributeID;
	}

	/**
	 * @return the score of the attribute as a double
	 * 
	 */
	public double getAttributeScore() 
	{
		return aAttributeScore;
	}

	/**

	 * {@param pAttributeScore score to set the attribute to
	 */
	public void setAttributeScore(double pAttributeScore)
	{
		this.aAttributeScore = pAttributeScore;
	}

	/**
	 * @return name of the attribute as String
	 */
	public String getAttributeName() 
	{
		return aAttributeName;
	}

	@Override
	public String toString()
	{
		return aAttributeName + ", " + aAttributeID + ", "+ aAttributeDesc +": " + aAttributeScore + ", " ;
	}

	/**
	 * @return mean or mode of this attribute given a product list used to 
	 * calculate the score
	 */
	public TypedValue getAttributeDefault() 
	{
		return aDefaultValue;
	}
	
	/**
	 * @param pDefault mean or mode of this attribute given a product list used to 
	 * calculate the score
	 */
	public void setAttributeDefault(TypedValue pDefault) 
	{
		aDefaultValue = pDefault;
	}

//	/**
//	 * @param pAttributeMean mean or mode of this attribute given a product list used to 
//	 * calculate the score
//	 */
//	public void setAttributeMean(TypedValue pAttributeMean) 
//	{
//		this.aAttributeMean = pAttributeMean;
//	}
	/**
	 * @return true if the attribute was derived from a cat
	 */
	public TypedValue getMin()
	{
		return aMin;
	}
	
	public TypedValue getMax()
	{
		return aMax;
	}
	
	public List<TypedValue> getDict()
	{
		return aStringValues;
	}
	
	/**
	 * @return String representing the attribute description
	 * */	
	public String getAttributeDesc() 
	{
		return aAttributeDesc;
	}

	
	/**
	 * @param pAttributeDesc description of this attribute
	 * ***/	
	public void setAttributeDesc(String pAttributeDesc)
	{
		this.aAttributeDesc = pAttributeDesc;
	}

	public double getEntropy()
	{
		return aEntropy;
	}
	
	public boolean isBoolean()
	{
		return aDefaultValue.isBoolean();
	}
	public boolean isNumeric()
	{
		return aDefaultValue.isNumeric();
	}
	
	public boolean isString()
	{
		return aDefaultValue.isString();
	}
	
	public boolean isNA()
	{
		return aDefaultValue.isNA();
	}
	public double getCorrelation() {
		return aCorrelation;
	}
	public boolean moreIsBetter() {
		if(aDirection == Direction.MORE_IS_BETTER)
		{
			return true;
		}
		return false;
		
	}
	public boolean lessIsBetter() {
		if(aDirection == Direction.LESS_IS_BETTER)
		{
			return true;
		}
		return false;
		
	}
	
	
	
}
