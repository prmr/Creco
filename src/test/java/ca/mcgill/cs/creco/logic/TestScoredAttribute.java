package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.*;

import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.TypedValue;
import ca.mcgill.cs.creco.logic.AttributeExtractor.SORT_METHOD;
import ca.mcgill.cs.creco.logic.ScoredAttribute.Direction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestScoredAttribute {

	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	AttributeExtractor aAttributeExtractor;
	
	@Autowired
	ProductRanker aProductRanker;
	
	private static final String HUMIDIFIER_CATEGORY_ID = "32968";
	
	@Test
	public void testSorting() throws NoSuchMethodException, SecurityException 
	{
		
		ArrayList<ScoredAttribute> correlation = new ArrayList<ScoredAttribute>(aAttributeExtractor.getAttributesForCategory(HUMIDIFIER_CATEGORY_ID,
				SORT_METHOD.CORRELATION));
		
		ArrayList<ScoredAttribute> entropy = new ArrayList<ScoredAttribute>(aAttributeExtractor.getAttributesForCategory(HUMIDIFIER_CATEGORY_ID,
				SORT_METHOD.ENTROPY));
		
		ArrayList<ScoredAttribute> score= new ArrayList<ScoredAttribute>(aAttributeExtractor.getAttributesForCategory(HUMIDIFIER_CATEGORY_ID,
				SORT_METHOD.SCORE));
		
		for(int i = 0; i< correlation.size()-1 ; i++)
		{
			assertTrue(Math.abs(correlation.get(i).getCorrelation()) >= Math.abs(correlation.get(i+1).getCorrelation()));
			assertTrue(entropy.get(i).getEntropy() >= entropy.get(i+1).getEntropy());
			assertTrue(score.get(i).getAttributeScore() >= score.get(i+1).getAttributeScore());
		}

	}
	
	@Test
	public void testRanking() throws NoSuchMethodException, SecurityException 
	{
		
		ArrayList<ScoredAttribute> scoredAttributes = new ArrayList<ScoredAttribute>(aAttributeExtractor.getAttributesForCategory(HUMIDIFIER_CATEGORY_ID,
				SORT_METHOD.CORRELATION));
		
		for(ScoredAttribute scoredA : scoredAttributes)
		{
			if(scoredA.isBoolean())
			{
				int rankTrue = scoredA.getValueRank(new TypedValue(true));
				int rankFalse = scoredA.getValueRank(new TypedValue(false));
				
				assertNotEquals(rankTrue, rankFalse);
				
				if(rankTrue != 1 && rankFalse != 1)
				{
					fail("no rank 1 in boolean " + rankTrue + ", " + rankFalse);
				}
				if(rankTrue != 2 && rankFalse != 2)
				{
					fail("no rank 2 in boolean " + rankTrue + ", " + rankFalse);
				}
			}
			else if(scoredA.isString())
			{
				List<TypedValue> tvs = scoredA.getDict();
				for(TypedValue tv : tvs)
				{
					try
					{
						scoredA.getValueRank(tv);
					}
					catch(Exception e)
					{
						fail("Some values in the possible values don't have a rank");
					}
				}
			}
			else if(scoredA.isNumeric())
			{
				try
				{
					Field f = scoredA.getClass().getDeclaredField("aNumericValueRank"); //NoSuchFieldException
					f.setAccessible(true);
					Map<Double,Integer> numRanks = (Map<Double,Integer>) f.get(scoredA); //IllegalAccessException
					ArrayList<Double> values = new ArrayList<Double>(numRanks.keySet());
					
					for(int j = 0; j < values.size() -1; j++)
					{
						System.out.println(values.get(j)+":"+numRanks.get(values.get(j)) + ", " +values.get(j+1)+":"+numRanks.get(values.get(j+1)) );
						if(values.get(j) < values.get(j+1))
						{
							if(scoredA.getDirection() == Direction.LESS_IS_BETTER)
							{
								assertTrue(numRanks.get(values.get(j)) < numRanks.get(values.get(j+1)));
							}
							else
							{
								assertTrue(numRanks.get(values.get(j)) > numRanks.get(values.get(j+1)));
							}
							
						}
						else if(values.get(j) > values.get(j+1))
						{
							if(scoredA.getDirection() == Direction.LESS_IS_BETTER)
							{
								assertTrue(numRanks.get(values.get(j)) > numRanks.get(values.get(j+1)));
							}
							else
							{
								assertTrue(numRanks.get(values.get(j)) < numRanks.get(values.get(j+1)));
							}
						}
						else if(values.get(j) == values.get(j+1))
						{
							assertTrue(numRanks.get(values.get(j)) == numRanks.get(values.get(j+1)));
						}
					}
				
				}
				catch(Exception e)
				{
					fail("error reflecting field in ScoredAttribute: " + e);
				}

			}
		}

	}

}
