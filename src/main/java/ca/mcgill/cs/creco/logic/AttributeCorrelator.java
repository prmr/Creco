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
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;

/**
 * Computes the correlation between attributes of products within a category.
 * By default, attributes are correlated with the products' Overall Score.
 */
public class AttributeCorrelator 
{
	private static final String OVERALL_SCORE_ATTRIBUTE_ID = "254";
	private static final double LESS_IS_BETTER_THRESHOLD = -0.15;
	
	private Category aCategory;
		
	/**
	 * New Correlator for this Category.
	 * @param pCategory The category whose products we want to correlate.
	 */
	public AttributeCorrelator(Category pCategory)
	{
		aCategory = pCategory;
	}
	
	/**
	 * Computes the attribute's direction. The computation is based on the correlation with the
	 * overall score. If the attribute is negatively correlated with the overall score below the
	 * LESS_IS_BETTER_THRESHOLD, then LESS_IS_BETTER. Otherwise, as is more common, MORE_IS_BETTER.
	 * All attributes must be numeric.
	 * @param pAttributeId The attribute for which to compute the direction.
	 * @return Either LESS_IS_BETTER or MORE_IS_BETTER.
	 */
	public ScoredAttribute.Direction computeAttributeDirection(String pAttributeId)
	{
		return computeAttributeDirection(pAttributeId, 1.0);
	}
	/**
	 * Computes the attribute's direction. The computation is based on the correlation with the
	 * overall score. If the attribute is negatively correlated with the overall score below the
	 * LESS_IS_BETTER_THRESHOLD, then LESS_IS_BETTER. Otherwise, as is more common, MORE_IS_BETTER.
	 * As opposed to @see computeAttributeDirection(String) it takes into account
	 *  a minimum fraction of attributes that need to be numeric.
	 * @param pAttributeId The attribute for which to compute the direction.
	 * @param pThreshold The minimum fraction of attributes that need to be numeric
	 * @return Either LESS_IS_BETTER or MORE_IS_BETTER.
	 */
	public ScoredAttribute.Direction computeAttributeDirection(String pAttributeId, double pThreshold)
	{
		double correlation = computeCorrelation(pAttributeId, pThreshold);
		
		if (correlation < LESS_IS_BETTER_THRESHOLD)
		{
			return ScoredAttribute.Direction.LESS_IS_BETTER;
		}
		else
		{
			return ScoredAttribute.Direction.MORE_IS_BETTER;
		}
	}
	/**
	 * Computes the correlation between the given attribute and the overall score of products 
	 * in the category.
	 * All attributes must be numeric.
	 * @param pAttributeId The attribute to correlate with the overall score.
	 * @return The Pearson's correlation score between the two attributes.
	 */
	public double computeCorrelation(String pAttributeId)
	{
		return computeCorrelation(OVERALL_SCORE_ATTRIBUTE_ID, pAttributeId, 1.0);
	}
	
	/**
	 * Computes the correlation between the given attribute and the overall score of products
	 *  in the category. As opposed to @see computeCorrelation(String) it takes into account
	 *  a minimum fraction of attributes that need to be numeric.
	 * @param pAttributeId The attribute to correlate with the overall score.
	 * @param pThreshold The minimum fraction of attributes that need to be numeric
	 * @return The Pearson's correlation score between the two attributes.
	 */
	public double computeCorrelation(String pAttributeId, double pThreshold)
	{
		return computeCorrelation(OVERALL_SCORE_ATTRIBUTE_ID, pAttributeId, pThreshold);
	}
	
	private double computeCorrelation(String pFirstAttributeId, String pSecondAttributeId, double pThreshold)
	{
		List<Double> firstValues = new ArrayList<Double>();
		List<Double> secondValues = new ArrayList<Double>();
		
		double existingCount = 0.0;
		double nonNumericCount = 0.0;
		
		for (Product product : aCategory.getProducts())
		{
			Attribute firstAttribute = product.getAttribute(pFirstAttributeId);
			Attribute secondAttribute = product.getAttribute(pSecondAttributeId);
			
			// Skip the product if it's missing either attribute
			if( missing(firstAttribute) || missing(secondAttribute))
			{
				continue;
			}
			//add if the value is not missing
			existingCount++;
			//if the attribute is not numeric keep count
			if (!firstAttribute.getTypedValue().isNumeric() ||
					!secondAttribute.getTypedValue().isNumeric())
			{
				nonNumericCount++;
			}
			//else add the values to the correlation array
			else
			{
				double firstValue = firstAttribute.getTypedValue().getNumeric();
				double secondValue = secondAttribute.getTypedValue().getNumeric();
				
				if (firstValue > 0)
				{
					firstValues.add(firstValue);
					secondValues.add(secondValue);
				}
			}
			
			
		}
		double ratio = 1 - nonNumericCount/existingCount;
		if(ratio < pThreshold)
		{
			throw new IllegalArgumentException("Threshold for correlation was not met: "
					+ ratio + "<" + pThreshold + " count: " + existingCount + " NNcount: " + nonNumericCount);
		}
		
		double[] firstArray = ArrayUtils.toPrimitive(firstValues.toArray(new Double[0]));
		double[] secondArray = ArrayUtils.toPrimitive(secondValues.toArray(new Double[0]));
		
		PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
		
		//TODO look at this in more detail.
		if(firstArray.length < 2 || secondArray.length < 2)
		{
			return 0;
		}
		return pearsonsCorrelation.correlation(firstArray, secondArray);
	}
	
	private static boolean missing( Attribute pAttribute )
	{
		return pAttribute == null || pAttribute.getTypedValue().isNull() || pAttribute.getTypedValue().isNA();
	}
	
}