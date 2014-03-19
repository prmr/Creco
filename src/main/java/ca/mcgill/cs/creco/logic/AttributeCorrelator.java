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
	private Category aCategory;
	
	private final String OVERALL_SCORE_ATTRIBUTE_ID = "254";
	private final double LESS_IS_BETTER_THRESHOLD = -0.15;
	
	public AttributeCorrelator(Category pCategory)
	{
		aCategory = pCategory;
	}
	
	/**
	 * Computes the attribute's direction. The computation is based on the correlation with the
	 * overall score. If the attribute is negatively correlated with the overall score below the
	 * LESS_IS_BETTER_THRESHOLD, then LESS_IS_BETTER. Otherwise, as is more common, MORE_IS_BETTER.
	 * @param pAttributeId The attribute for which to compute the direction.
	 * @return Either LESS_IS_BETTER or MORE_IS_BETTER.
	 */
	public ScoredAttribute.Direction computeAttributeDirection(String pAttributeId)
	{
		double correlation = computeCorrelation(pAttributeId);
		
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
	 * Computes the correlation between the given attribute and the overall score of products in the category.
	 * @param pAttributeId The attribute to correlate with the overall score.
	 * @return The Pearson's correlation score between the two attributes.
	 */
	public double computeCorrelation(String pAttributeId)
	{
		return computeCorrelation(OVERALL_SCORE_ATTRIBUTE_ID, pAttributeId);
	}
	
	private double computeCorrelation(String pFirstAttributeId, String pSecondAttributeId)
	{
		List<Double> firstValues = new ArrayList<Double>();
		List<Double> secondValues = new ArrayList<Double>();
		
		for (Product product : aCategory.getProducts())
		{
			Attribute firstAttribute = product.getRating(pFirstAttributeId);
			Attribute secondAttribute = product.getRating(pSecondAttributeId);

			//TODO Remove this if statement once specs and ratings are merged together
			if (secondAttribute == null)
			{
				secondAttribute = product.getSpec(pSecondAttributeId);
			}
			
			// Skip the product if it's missing either attribute
			if (firstAttribute == null || secondAttribute == null
					|| firstAttribute.getTypedValue().isNull()
					|| secondAttribute.getTypedValue().isNull()
					|| firstAttribute.getTypedValue().isNA()
					|| secondAttribute.getTypedValue().isNA())
			{
				continue;
			}
			
			if (!firstAttribute.getTypedValue().isNumeric() ||
					!secondAttribute.getTypedValue().isNumeric())
			{
				throw new IllegalArgumentException("Can only correlate numeric attributes but one of the attributes was not numeric: "
						+ firstAttribute.getTypedValue() + " and " + secondAttribute.getTypedValue());
			}
			
			
			double firstValue = firstAttribute.getTypedValue().getNumeric();
			double secondValue = secondAttribute.getTypedValue().getNumeric();
			
			if (firstValue > 0)
			{
				firstValues.add(firstValue);
				secondValues.add(secondValue);
			}
		}
		
		double[] firstArray = ArrayUtils.toPrimitive(firstValues.toArray(new Double[0]));
		double[] secondArray = ArrayUtils.toPrimitive(secondValues.toArray(new Double[0]));
		
		PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
		return pearsonsCorrelation.correlation(firstArray, secondArray);
	}
	
}