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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;

/**
 * Computes how well nominal attributes correspond to a product's overall score.
 */
public class NominalCorrelator 
{

	private static final String OVERALL_SCORE_ATTRIBUTE_ID = "254";
	
	private Category aCategory;
	
	/**
	 * New Correlator for this Category.
	 * @param pCategory The category whose products we want to correlate.
	 */
	public NominalCorrelator(Category pCategory)
	{
		aCategory = pCategory;
	}
	
	/**
	 * Computes the mean overall score of all products which share the same label
	 * (or value) of a given nominal attribute.
	 * @param pFirstAttributeId The nominal attribute for which to get all labels.
	 * @return A mapping of label values and the mean overall score of products which have that value.
	 */
	public Map<String, Double> getLabelMeanScores(String pFirstAttributeId)
	{
		Map<String, Double> labelScores = new HashMap<String, Double>();
		
		List<Entry<String, Double>> labelScorePairs = getLabelScorePairs(pFirstAttributeId);
		
		for (String uniqueLabel : getUniqueLabels(labelScorePairs))
		{
			int labelCount = 0;
			double scoreSum = 0;
			
			for (Entry<String, Double> labelScorePair : labelScorePairs)
			{
				if (labelScorePair.getKey().equals(uniqueLabel))
				{
					labelCount++;
					scoreSum += labelScorePair.getValue();
				}
			}
			labelScores.put(uniqueLabel, scoreSum / labelCount);
		}
		return labelScores;
	}
	
	/**
	 * Computes the attribute's weight, which is a measure of how well nominal labels
	 * can predict a product's overall score. If the attribute values clearly differentiate
	 * products with low score from products with high score, the attribute's weight will be high (1).
	 * If nominal attribute values share similar product overall scores, the weight will be low (0).
	 * @param pFirstAttributeId The nominal attribute for which to compute the weight.
	 * @return The attribute's weight, between 0.0 (terrible indicator of overall score) 
	 * and 1.0 (great indicator of overall score).
	 */
	public double computeAttributeWeight(String pFirstAttributeId)
	{
		Map<String, Double> labelCentroids = getLabelMeanScores(pFirstAttributeId);
		int numClusters = labelCentroids.entrySet().size();
		int correctCount = 0;
		int totalCount = 0;
		
		for (Entry<String, Double> labelScorePair : getLabelScorePairs(pFirstAttributeId))
		{
			double minimumDistance = Double.MAX_VALUE;
			String centroidLabel = "";
			
			for (Entry<String, Double> labelCentroid : labelCentroids.entrySet())
			{
				double distance = Math.abs(labelScorePair.getValue() - labelCentroid.getValue());
				
				if (distance < minimumDistance)
				{
					minimumDistance = distance;
					centroidLabel = labelCentroid.getKey();
				}
			}
			
			if (centroidLabel.equals(labelScorePair.getKey()))
			{
				correctCount++;
			}
			totalCount++;
		}
		
		double accuracy = correctCount / (double) totalCount;
		double uniformAccuracy = 1.0 / numClusters;
		double normalizedWeight = (accuracy - uniformAccuracy) / ( 1 - uniformAccuracy);
		
		return normalizedWeight;
	}
	
	/**
	 * Given some nominal attribute, gets a list of products which have that nominal
	 * attribute and have an overall score. Assembles a list of nominal label and score
	 * pairs.
	 * @param pFirstAttributeId The nominal attribute.
	 * @return A list of nominal value and overall score pairs.
	 */
	private List<Entry<String, Double>> getLabelScorePairs(String pFirstAttributeId)
	{
		List<Entry<String, Double>> labelScorePairs = new ArrayList<Entry<String, Double>>();
		
		for (Product product : aCategory.getProducts())
		{
			Attribute label = product.getAttribute(pFirstAttributeId);
			Attribute score = product.getAttribute(OVERALL_SCORE_ATTRIBUTE_ID);
			
			// Skip the product if it's missing either attribute
			if( missing(label) || missing(score))
			{
				continue;
			}
			
			if (label.getTypedValue().isString() && score.getTypedValue().isNumeric())
			{
				String entryLabel = label.getTypedValue().getString();
				Double entryScore = score.getTypedValue().getNumeric();
				
				labelScorePairs.add(new SimpleEntry<String, Double>(entryLabel, entryScore));
			}
			
			if (label.getTypedValue().isBoolean() && score.getTypedValue().isNumeric())
			{
				String entryLabel = String.valueOf(label.getTypedValue().getBoolean());
				Double entryScore = score.getTypedValue().getNumeric();
				
				labelScorePairs.add(new SimpleEntry<String, Double>(entryLabel, entryScore));
			}
		}
		
		return labelScorePairs;
	}
	
	/**
	 * Given a list of nominal value and score pairs, returns the set of all nominal
	 * values possible.
	 * @param pLabelScorePairs The list of nominal label and score pairs.
	 * @return The set of nominal labels.
	 */
	private Set<String> getUniqueLabels(List<Entry<String, Double>> pLabelScorePairs)
	{
		Set<String> uniqueLabels = new HashSet<String>();
		
		for (Entry<String, Double> labelScorePair : pLabelScorePairs)
		{
			uniqueLabels.add(labelScorePair.getKey());
		}
		
		return uniqueLabels;
	}
	
	private static boolean missing( Attribute pAttribute )
	{
		return pAttribute == null || pAttribute.getTypedValue().isNull() || pAttribute.getTypedValue().isNA();
	}
	
}
