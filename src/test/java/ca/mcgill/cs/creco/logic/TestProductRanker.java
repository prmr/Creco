package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;

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
	
	@Test
	public void testRankingContainsAllProducts()
	{    
		Category category = aDataStore.getCategory(TOASTER_CATEGORY_ID); 
		List<ScoredAttribute> scoredAttributes = aAttributeExtractor.getAttributesForCategory(category.getId());
		    
		List<Product> scoredProducts = aProductRanker.rankProducts(scoredAttributes, category.getProducts());
		assertEquals(TOASTER_CATEGORY_NUM_PRODUCTS, scoredProducts.size());
	}
}
