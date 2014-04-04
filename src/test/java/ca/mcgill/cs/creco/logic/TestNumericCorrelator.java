package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestNumericCorrelator
{
	@Autowired
	IDataStore aDataStore;
	
	private static final String HUMIDIFIER_CATEGORY_ID = "32968";
	private static final String HUMIDIFIER_OUTPUT_ID = "4556";
	private static final String HUMIDIFIER_FULL_TANK_WEIGHT_ID = "6929";
	
	@Test
	public void testCorrelation() 
	{
		Category category = aDataStore.getCategory(HUMIDIFIER_CATEGORY_ID);
		NumericCorrelator attributeCorrelator = new NumericCorrelator(category);
		
		double correlation = attributeCorrelator.computeCorrelation(HUMIDIFIER_OUTPUT_ID);
		
		assertEquals(0.952, correlation, 0.01);
	}
	
	@Test
	public void testMoreIsBetterDirection() 
	{
		Category category = aDataStore.getCategory(HUMIDIFIER_CATEGORY_ID);
		NumericCorrelator attributeCorrelator = new NumericCorrelator(category);
		
		ScoredAttribute.Direction direction = 
				attributeCorrelator.computeAttributeDirection(HUMIDIFIER_OUTPUT_ID);
		
		assert(direction == ScoredAttribute.Direction.MORE_IS_BETTER);
	}
	
	@Test
	public void testLessIsBetterDirection() 
	{
		Category category = aDataStore.getCategory(HUMIDIFIER_CATEGORY_ID);
		NumericCorrelator attributeCorrelator = new NumericCorrelator(category);
		
		ScoredAttribute.Direction direction = 
				attributeCorrelator.computeAttributeDirection(HUMIDIFIER_FULL_TANK_WEIGHT_ID);
		
		assert(direction == ScoredAttribute.Direction.LESS_IS_BETTER);
	}
}