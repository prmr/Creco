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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestNominalCorrelator
{
	@Autowired
	IDataStore aDataStore;
	
	private static final String HUMIDIFIER_CATEGORY_ID = "32968";
	private static final String HUMIDIFIER_COLOR_ID = "6";
	private static final double DELTA = 0.1;
	
	@Test
	public void testLabelMeanScores() 
	{
		final String WHITE = "White";
		final double WHITE_MEAN_SCORE = 66.06;
		final String BLACK = "Black";
		final double BLACK_MEAN_SCORE = 46.09;
		
		Category category = aDataStore.getCategory(HUMIDIFIER_CATEGORY_ID);
		NominalCorrelator attributeCorrelator = new NominalCorrelator(category);
		
		Map<String, Double> labelMeanScores = attributeCorrelator.getLabelMeanScores(HUMIDIFIER_COLOR_ID);
		
		assertEquals(WHITE_MEAN_SCORE, labelMeanScores.get(WHITE), DELTA);
		assertEquals(BLACK_MEAN_SCORE, labelMeanScores.get(BLACK), DELTA);
	}
	
	@Test
	public void testAttributeWeight()
	{
		final double COLOR_ATTRIBUTE_WEIGHT = 0.55;
		
		Category category = aDataStore.getCategory(HUMIDIFIER_CATEGORY_ID);
		NominalCorrelator attributeCorrelator = new NominalCorrelator(category);
		
		double computedAttributeWeight = attributeCorrelator.computeAttributeWeight(HUMIDIFIER_COLOR_ID);
		
		assertEquals(COLOR_ATTRIBUTE_WEIGHT, computedAttributeWeight, DELTA);
	}

}