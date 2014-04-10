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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.ScoredAttribute.Direction;

/**
 * Ranks a collection of products according to a given set of attributes.
 */
@Component
public class ProductRanker 
{
	private static final double MISSING_ATTRIBUTE_PENALTY = -1.0;
	private static final int HUNDRED = 100;
	@Autowired
	private IDataStore aDataStore;
	@Autowired
	private AttributeExtractor aAttributeExtractor;

	/**
	 * Ranks a collection of products according to a given set of attributes.
	 * @param pScoredAttributes The set of attributes used to rank the products.
	 * @param pCategory The category of products being ranked.
	 * @return The ranked list of products, ordered from highest to lowest score.
	 */
	public List<RankExplanation> rankProducts(List <ScoredAttribute> pScoredAttributes, Category pCategory)
	{
		Map<Product, Double> scoredProducts = new HashMap<Product, Double>();
		Collection<Product> products = pCategory.getProducts();
		// Initialize the map with scores of 0 for all products
		for (Product product : products)
		{
			scoredProducts.put(product, 0.0);
		}
		
		for (ScoredAttribute scoredAttribute : pScoredAttributes)
		{	
			for (Product product : products)
			{
				double updateValue = 0;
				
				Attribute attribute = product.getAttribute(scoredAttribute.getAttributeID());
				
				if (attribute == null)
				{
					updateValue = MISSING_ATTRIBUTE_PENALTY;
				} 
				else if (attribute.getTypedValue().isNumeric())
				{
					updateValue = numericUpdateEquation(scoredAttribute, attribute.getTypedValue().getNumeric());
				}
				else if (attribute.getTypedValue().isString())
				{
					updateValue = stringUpdateEquation(scoredAttribute, attribute.getTypedValue().getString());
				}
				else if (attribute.getTypedValue().isBoolean())
				{
					updateValue = stringUpdateEquation(scoredAttribute, String.valueOf(attribute.getTypedValue().getBoolean()));
				}
				
				// Update the product's score according to the attribute's value
				scoredProducts.put(product, scoredProducts.get(product) + updateValue);
			}
		}

		List<Product> sortedProducts = sortProductsByScore(scoredProducts);
		List<RankExplanation> prodExp = new ArrayList<RankExplanation>();
		
		for (Product p :sortedProducts)
		{
			prodExp.add(new RankExplanation(p, pCategory, pScoredAttributes));						
		}
		return prodExp; 	
	}
	
	/**
	 * Computes a score update depending on a numeric attribute value.
	 * @param pScoredAttribute The score attribute according to which the update value will be computed.
	 * @param pAttributeValue The numeric attribute value.
	 * @return The score update value.
	 */
	private double numericUpdateEquation(ScoredAttribute pScoredAttribute, double pAttributeValue)
	{
		int direction = 1;
		if (pScoredAttribute.getDirection() == Direction.LESS_IS_BETTER)
		{
			direction = -1;
		}
		
		// The attribute's correlation with the products' overall score is used as a weight
		double attributeWeight = Math.abs(pScoredAttribute.getCorrelation());
		
		double normalization = 1 / pScoredAttribute.getMax().getNumeric();
		
		return direction * attributeWeight * normalization * pAttributeValue;
	}
	
	private double stringUpdateEquation(ScoredAttribute pScoredAttribute, String pAttributeValue)
	{
		Double labelValue = pScoredAttribute.getLabelMeanScores().get(pAttributeValue);
		if (labelValue == null)
		{
			labelValue = 0.0;
		}
		
		double normalization = 1.0/HUNDRED;
		
		double attributeWeight = Math.abs(pScoredAttribute.getCorrelation());
		
		return attributeWeight * normalization * labelValue;
	}
	
	/**
	 * Sorts a map of products and scores, from highest scored product to lowest.
	 * @param pScoredProducts The map of products with their corresponding scores.
	 * @return A list sorted from highest scored product to lowest.
	 */
	private List<Product> sortProductsByScore(Map<Product, Double> pScoredProducts)
	{
		List<Map.Entry<Product, Double>> productEntries = new ArrayList<Map.Entry<Product, Double>>(pScoredProducts.entrySet());
		
		Collections.sort(productEntries, new ScoreComparator());
		
		// Place the sorted entries back into a list
		List<Product> sortedProducts = new ArrayList<Product>();
		for (Map.Entry<Product, Double> entry : productEntries)
		{
			sortedProducts.add(entry.getKey());
		}
		return sortedProducts;
	}
	
	/**
	 * Comparator to sort a map of products according to their respective scores.
	 * Sorts from highest to lowest scored product.
	 */
	private class ScoreComparator implements Comparator<Map.Entry<Product, Double>>
	{
		public int compare(Map.Entry<Product, Double> pEntry1, Map.Entry<Product, Double> pEntry2) 
        {
            return -pEntry1.getValue().compareTo(pEntry2.getValue());
        }
	}
}
