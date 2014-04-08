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

/**
 * TODO fix the Attribtue correlation
 */
package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;

/**
 * An attribute associated with a ranking.
 */
public class ScoredAttribute
{
	/** Denotes the "natural" direction of an attribute. */
	public static enum Direction 
	{ MORE_IS_BETTER, LESS_IS_BETTER };
	
	private static final Logger LOG = LoggerFactory.getLogger(ScoredAttribute.class);
	
	private static final double CONSIDERATION_THRESHOLD = 0.8;
	private static final double DEFAULT_MIN = 10000000;
	private static final double DEFAULT_MAX = -10000000;
	
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

 	private Category aCategory;
 	private String aAttributeDesc;
 	private double aEntropy;
 	private double aCorrelation;
 	
 	private TypedValue aMin;
 	private TypedValue aMax;
 	private List<TypedValue> aStringValues;
 	private Map<String, Double> aLabelMeanScores;
 	private Map<Double, Integer> aNumericValueRank;
 	private Map<String, Integer> aStringValueRank;
 	
 	private Type aAttributeMainType;
 	private Direction aDirection;
 	private boolean aIsPrice;
 	
   
 	/**
 	 * The type of attribute.
 	 */
 	private enum Type
 	{ NULL, NA, BOOLEAN, NUMERIC, STRING }

 	
 	/**Constructor for a null scored attribute.
	 */
	public ScoredAttribute()
	{
		aAttributeID = "0";
		aAttributeScore = 0.0;
		aAttributeName = "";
		aAttributeDesc = "";
		aEntropy = 0;
		aCorrelation = 0;
		aAttributeMainType = Type.NULL;
		aDefaultValue = new TypedValue();
		aDirection = Direction.MORE_IS_BETTER;
	}
	/**Constructor from an attribute.
	 * @param pAttribute attribute to build score for.
	 * @param pCategory in which the attribute is present
	 */
	public ScoredAttribute(Attribute pAttribute, Category pCategory)
	{

		aAttributeID = pAttribute.getId();
		aAttributeScore = 0.0;
		aAttributeName = pAttribute.getName();
		aAttributeDesc = pAttribute.getDescription();
		aEntropy = 0;
		aCorrelation = 0;
		aLabelMeanScores = new HashMap<String, Double>();
		aNumericValueRank = new HashMap<Double, Integer>();
		aStringValueRank = new HashMap<String, Integer>();
		aCategory = pCategory;
		
		aIsPrice = pAttribute.isPrice();
		
		if( pCategory != null )
		{
			Collection<Product> products = pCategory.getProducts();
			setStats(products);	
		}
		else
		{
			aDefaultValue = new TypedValue();
		}
		

	}
	private void setStats(Collection<Product> pProducts)
	{
		ArrayList<TypedValue> values = new ArrayList<TypedValue>();
		for(Product p : pProducts)
		{
			
			Attribute a = p.getAttribute(aAttributeID);
			if( a != null)
			{
				values.add(a.getTypedValue());
			}
			
		}
		if(values.size()>0)
		{
			aAttributeMainType = getMainType(values);
		}
		else
		{
			aAttributeMainType = Type.NA;
		}
		
		// goes only over the products that had a non null attribute value
		if(aAttributeMainType == Type.NUMERIC)
		{
			setNumericStats(values);
			
		}
		else if(aAttributeMainType == Type.BOOLEAN)
		{
			setBooleanStats(values);
		}
		else if(aAttributeMainType == Type.STRING)
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
		ArrayList<Double> values = new ArrayList<Double>(pValues.size());
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
				values.add(tv.getNumeric());
			}
			
		}
		//Set bounds
		if(Double.isNaN(min))
		{
			LOG.error("Min value is NaN: " + aAttributeID +", "+ aAttributeName + ", "+ aCategory.getId());
		}
		if(Double.isNaN(max))
		{
			LOG.error("Max value is NaN: " + aAttributeID +", "+ aAttributeName + ", "+ aCategory.getId());
		}
		aMin = new TypedValue(min);
		aMax = new TypedValue(max);
		double mean = ss.getGeometricMean();
		double variance = ss.getStandardDeviation()*ss.getStandardDeviation();
		//Calculate Entropy
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
		
		//Get the correlation
		NumericCorrelator ac = new NumericCorrelator(aCategory);
		aCorrelation = ac.computeCorrelation(aAttributeID, CONSIDERATION_THRESHOLD);
			
		if(aIsPrice)
		{
			aDirection = Direction.LESS_IS_BETTER;
		}
		else
		{
			aDirection = ac.computeAttributeDirection(aAttributeID, CONSIDERATION_THRESHOLD);
		}
		//Calculate Ranking
		if(aDirection == Direction.LESS_IS_BETTER)
		{
			Collections.sort(values);
		}
		else
		{
			Collections.sort(values,Collections.reverseOrder());
		}
		double previousValue = 0;
		boolean notFirst = false;
		int rank = 1;
		for(Double val : values)
		{			
			if(notFirst)
			{
				if(previousValue != val.doubleValue()){
					if(aNumericValueRank.containsKey(val))
					{
						LOG.error("setNumericStats sort error in Attribute " +aAttributeID+ "in Category " + aCategory.getId());
					}
					else{
						aNumericValueRank.put(val, rank);
						previousValue = val.doubleValue();
					}
				}			
			}
			else//It is the first Item
			{
				aNumericValueRank.put(val, rank);
				previousValue = val.doubleValue();
				notFirst = true;
			}
			rank ++;
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
		// Compute Correlation and rankings
		aLabelMeanScores = new HashMap<String, Double>();
		
		NominalCorrelator nominalCorrelator = new NominalCorrelator(aCategory);
		ArrayList<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>();
		entryList.addAll(nominalCorrelator.getLabelMeanScores(aAttributeID).entrySet());
		sortEntries(entryList);
		int rank = 1;
		for (Map.Entry<String, Double> entry : entryList)
		{
			aLabelMeanScores.put(entry.getKey(), entry.getValue());
			aStringValueRank.put(entry.getKey(), rank);
			rank++;
		}
		
		aCorrelation = nominalCorrelator.computeAttributeWeight(aAttributeID);
		

		
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
		double probTrue = trueCount/totalCount;
		double probFalse = 1 - probTrue;
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
		// Compute Correlation
		NominalCorrelator nominalCorrelator = new NominalCorrelator(aCategory);
		ArrayList<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>();
		entryList.addAll(nominalCorrelator.getLabelMeanScores(aAttributeID).entrySet());
		sortEntries(entryList);
		int rank = 1;
		for (Map.Entry<String, Double> entry : entryList)
		{
			aLabelMeanScores.put(entry.getKey(), entry.getValue());
			aStringValueRank.put(entry.getKey(), rank);
			if(entry.getKey().equals(String.valueOf(true)))
			{
				rank += trueCount;
			}
			else
			{
				rank += trueCount-totalCount;
			}
		}
		
		aCorrelation = nominalCorrelator.computeAttributeWeight(aAttributeID);
		
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
				booleanCount += 1.0;
			}
			else if (tv.isNumeric())
			{
				numericCount += 1.0;
			}
			else if(tv.isString())
			{
				stringCount += 1.0;
			}
		}
		float totalCount = pValues.size();
		if((booleanCount/totalCount) >= CONSIDERATION_THRESHOLD)
		{
			return Type.BOOLEAN;
		}
		else if((numericCount/totalCount) >= CONSIDERATION_THRESHOLD)
		{
			return Type.NUMERIC;
		}
		else if((stringCount/totalCount) >= CONSIDERATION_THRESHOLD)
		{
			return Type.STRING;
		}
		else
		{
			return Type.NA;
		}
	}
	
	private void sortEntries(List<Map.Entry<String, Double>> pEntries)
	{
		Collections.sort(pEntries, 
				new Comparator<Map.Entry<String, Double>>() 
				{
					@Override
					public int compare(Map.Entry<String, Double> pA, Map.Entry<String, Double> pB) 
					{
						return -Double.compare(pA.getValue(), pB.getValue());
					}
				});
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
	 * @return name of the attribute as String
	 */
	public String getAttributeName() 
	{
		return aAttributeName;
	}

	/**
	 * To be Changed
	 */
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
	 * @return The minimum value for this attribute.
	 */
	public TypedValue getMin()
	{
		return aMin;
	}
	
	/**
	 * @return The maximum value for this attribute.
	 */
	public TypedValue getMax()
	{
		return aMax;
	}
	
	/**
	 * @return The potential string values for this attribute.
	 */
	public List<TypedValue> getDict()
	{
		return aStringValues;
	}
	
	/**
	 * @return The potential string values for this attribute.
	 */
	public Map<String, Double> getLabelMeanScores()
	{
		return aLabelMeanScores;
	}
	
	/**
	 * @return String representing the attribute description
	 * */	
	public String getAttributeDesc() 
	{
		return aAttributeDesc;
	}
	/**
	 * @return The entropy for this attribute.
	 */
	public double getEntropy()
	{
		return aEntropy;
	}
	
	/**
	 * @return The correlation for this attribute.
	 */
	public double getCorrelation()
	{
		return aCorrelation;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a non-available value.
	 */
	public boolean isNA()
	{
		return aAttributeMainType == Type.NA;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a null value.
	 */
	public boolean isNull()
	{
		return aAttributeMainType == Type.NULL;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a boolean value.
	 */
	public boolean isBoolean()
	{
		return aAttributeMainType == Type.BOOLEAN;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a numeric.
	 */
	public boolean isNumeric()
	{
		return aAttributeMainType == Type.NUMERIC;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a string value.
	 */
	public boolean isString()
	{
		return aAttributeMainType == Type.STRING;
	}
	/**
	 * @return The threshold at which an attribute is determined
	 * to be of a certain type. An attribute must have more than
	 * the threshold fraction of it's attribute of a same type 
	 * to be considered of that type. This is currently set to 
	 * 0.8.
	 */
	public double getTypeThreshold()
	{
		return CONSIDERATION_THRESHOLD;
	}
	/**
	 * @return The directions in which the atrtibute is worth more
	 * MORE_IS_BETTER or LESS_IS_BETTER. 
	 */
	public Direction getDirection()
	{
		return aDirection;
	}
	/**
	 * This will return the rank of the Typed value passed. If the value is not found,
	 * this will throw an IllegalArgeumetnException error.
	 * 
	 * For booleans, true always has rank 1 and false always has rank 2;
	 * 
	 * @param pValue the value to be checkAgainst
	 * @return the rank of the value in this ScoredAttribute
	 * @throws IllegalArgumentException If the value wasn't found or if the type is incompatible
	 */
	public Integer getValueRank(TypedValue pValue)
	{
		if(pValue.isNumeric())
		{
			if(aNumericValueRank.containsKey(pValue.getNumeric()))
			{
				return aNumericValueRank.get(pValue.getNumeric());
			}
			else
			{
				throw new IllegalArgumentException("TypedValue Incompatible with Ranking "
						+ "in Attribute " +aAttributeID+ "in Category " + aCategory.getId());
			}
		}
		else if(pValue.isString())
		{
			if(aStringValueRank.containsKey(pValue.getString()))
			{
				return aStringValueRank.get(pValue.getString());
			}
			else
			{
				throw new IllegalArgumentException("TypedValue Incompatible with Ranking "
						+ "in Attribute " +aAttributeID+ "in Category " + aCategory.getId());
			}
		}
		else if(pValue.isBoolean()) //Assume it is always better to have 
		{
			if(aStringValueRank.containsKey(String.valueOf(pValue.getBoolean())))
			{
				return aStringValueRank.get(String.valueOf(pValue.getBoolean()));
			}
			else
			{
				throw new IllegalArgumentException("TypedValue Incompatible with Ranking "
						+ "in Attribute " +aAttributeID+ "in Category " + aCategory.getId());
			}
		}
		else
		{
			throw new IllegalArgumentException("TypedValue Incompatible with Ranking "
					+ "in Attribute " +aAttributeID+ "in Category " + aCategory.getId());
		}
	}
	
	/**
	 * This will return the score for a  TypedValue passed. If the value is not found,
	 * this will throw an IllegalArgeumetnException error.
	 * 
	 * For numeric TypedValues the score is the value if the attribute is numeric and zero otherwise.
	 * 
	 * @param pValue the value to be checkAgainst
	 * @return the score of the value in this ScoredAttribute
	 * @throws IllegalArgumentException If the value wasn't found or if the type is incompatible
	 */
	public double getValueScore(TypedValue pValue)
	{
		if(pValue.isNumeric())
		{
			if(aAttributeMainType == Type.NUMERIC)
			{
				return pValue.getNumeric();
			}
			return 0.0;
			
		}
		else if(pValue.isString() || pValue.isBoolean())
		{
			if(aLabelMeanScores.containsKey(pValue.getString()))
			{
				return aLabelMeanScores.get(pValue.getString());
			}
			else
			{
				throw new IllegalArgumentException("TypedValue Incompatible with Ranking "
						+ "in Attribute " +aAttributeID+ "in Category " + aCategory.getId());
			}
		}
		else if(pValue.isBoolean()) //Assume it is always better to have 
		{
			if(aLabelMeanScores.containsKey(String.valueOf(pValue.getBoolean())))
			{
				return aLabelMeanScores.get(String.valueOf(pValue.getBoolean()));
			}
			else
			{
				throw new IllegalArgumentException("TypedValue Incompatible with Ranking "
						+ "in Attribute " +aAttributeID+ "in Category " + aCategory.getId());
			}
		}
		else
		{
			throw new IllegalArgumentException("TypedValue Incompatible with Ranking "
					+ "in Attribute " +aAttributeID+ "in Category " + aCategory.getId());
		}
	}
	
}

