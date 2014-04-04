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
	
	@Autowired
	AttributeExtractor attributeExtractor;
	
	private static final String CATEGORY_ID = "32968";
	
	@Test
	public void testCorrelation() 
	{
		String attributeId = "6";
		Category category = aDataStore.getCategory(CATEGORY_ID);
		NominalCorrelator attributeCorrelator = new NominalCorrelator(category);
		
		for (ScoredAttribute scoredAttribute : attributeExtractor.getAttributesForCategory(CATEGORY_ID))
		{
			System.out.println(scoredAttribute.getAttributeName() + " " + scoredAttribute.getAttributeID());
		}
		
		for (Map.Entry<String, Double> entry : attributeCorrelator.getLabelMeanScores(attributeId).entrySet())
		{
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		
		System.out.println("Attribute weight: " + attributeCorrelator.computeAttributeWeight(attributeId));
		
	}
}