package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestProductRanker {
	
	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	AttributeExtractor aAttributeExtractor;
	
	@Autowired
	ProductRanker aProductRanker;
	
	private static String TOASTER_CATEGORY_ID = "28732";
	private static int TOASTER_CATEGORY_NUM_PRODUCTS = 407;
	
	private static final String HUMIDIFIER_CATEGORY_ID = "32968";
	private static final String HUMIDIFIER_OUTPUT_ID = "4556";
	private static final String HUMIDIFIER_FULL_TANK_WEIGHT_ID = "6929";
	
	@Test
	public void testRankingContainsAllProducts()
	{    
		Category category = aDataStore.getCategory(TOASTER_CATEGORY_ID); 
		List<ScoredAttribute> scoredAttributes = aAttributeExtractor.getAttributesForCategory(category.getId());
		    
		List<RankingExplanation> scoredProducts = aProductRanker.rankProducts(scoredAttributes, category.getProducts());
		
		assertEquals(TOASTER_CATEGORY_NUM_PRODUCTS, scoredProducts.size());
	}
	
	@Test
	public void testRankNumericMoreIsBetter()
	{
		Category category = aDataStore.getCategory(HUMIDIFIER_CATEGORY_ID); 
		ScoredAttribute humidifierOutputAttribute = aAttributeExtractor.getScoredAttributeInCategory(HUMIDIFIER_CATEGORY_ID, HUMIDIFIER_OUTPUT_ID);
		
		List<ScoredAttribute> scoredAttributes = new ArrayList<ScoredAttribute>();
		scoredAttributes.add(humidifierOutputAttribute);
		
		List<RankingExplanation> scoredProducts = aProductRanker.rankProducts(scoredAttributes, category.getProducts());
		
		// Highest humidifier output should be 5.0/5.0
		assertEquals(scoredProducts.get(0).getaProduct().getAttribute(HUMIDIFIER_OUTPUT_ID).getTypedValue().getNumeric(), 5.0, 0.00001);
	}
	
	@Test
	public void testRankNumericLessIsBetter()
	{
		Category category = aDataStore.getCategory(HUMIDIFIER_CATEGORY_ID); 
		
		ScoredAttribute humidifierWeightAttribute = aAttributeExtractor.getScoredAttributeInCategory(HUMIDIFIER_CATEGORY_ID, HUMIDIFIER_FULL_TANK_WEIGHT_ID);
		
		List<ScoredAttribute> scoredAttributes = new ArrayList<ScoredAttribute>();
		scoredAttributes.add(humidifierWeightAttribute);
		
		List<RankingExplanation> scoredProducts = aProductRanker.rankProducts(scoredAttributes, category.getProducts());
		
		// Lowest weight is 7.5
		assertEquals(scoredProducts.get(0).getaProduct().getAttribute(HUMIDIFIER_FULL_TANK_WEIGHT_ID).getTypedValue().getNumeric(), 7.5, 0.0001);	
	}
	
	@Test
	public void testRankByTwoNumeric()
	{
		Category category = aDataStore.getCategory(HUMIDIFIER_CATEGORY_ID); 
		
		ScoredAttribute humidifierOutputAttribute = aAttributeExtractor.getScoredAttributeInCategory(HUMIDIFIER_CATEGORY_ID, HUMIDIFIER_OUTPUT_ID);
		ScoredAttribute humidifierWeightAttribute = aAttributeExtractor.getScoredAttributeInCategory(HUMIDIFIER_CATEGORY_ID, HUMIDIFIER_FULL_TANK_WEIGHT_ID);
		
		List<ScoredAttribute> scoredAttributes = new ArrayList<ScoredAttribute>();
		scoredAttributes.add(humidifierOutputAttribute);
		scoredAttributes.add(humidifierWeightAttribute);
		
		List<RankingExplanation> scoredProducts = aProductRanker.rankProducts(scoredAttributes, category.getProducts());
		
		assertEquals(scoredProducts.get(0).getaProduct().getName(), "Safety 1st 49292");
	}
}
