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

public class NominalCorrelator {

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
	
	public double computeAttributeWeight(String pFirstAttributeId)
	{
		Map<String, Double> labelMeanScores = getLabelMeanScores(pFirstAttributeId);
		int numClusters = labelMeanScores.entrySet().size();
		int correctCount = 0;
		int totalCount = 0;
		
		for (Entry<String, Double> labelScorePair : getLabelScorePairs(pFirstAttributeId))
		{
			double minimumDistance = Double.MAX_VALUE;
			String centroidLabel = "";
			
			for (Entry<String, Double> labelMeanScore : labelMeanScores.entrySet())
			{
				double distance = Math.abs(labelScorePair.getValue() - labelMeanScore.getValue());
				
				if (distance < minimumDistance)
				{
					minimumDistance = distance;
					centroidLabel = labelMeanScore.getKey();
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
