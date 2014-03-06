package ca.mcgill.cs.creco.logic.search;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.IDataStore;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestProductSearch {

	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	IProductSearch aProductSearch;
	
	private static String SMARTPHONE_CATEGORY_ID = "28726";
	
	@Test
	public void testExactStringMatch() throws IOException
	{
		List<ScoredProduct> scoredProducts = aProductSearch.queryProducts("iPhone 5s (16GB) (Verizon)", SMARTPHONE_CATEGORY_ID);
		
		assertEquals("iPhone 5s (16GB) (Verizon)", scoredProducts.get(0).getProduct().getName());
	}
	
	@Test
	public void testTypoStringMatch() throws IOException
	{
		List<ScoredProduct> scoredProducts = aProductSearch.queryProducts("iPhone 5s (16GB) (Veribon)", SMARTPHONE_CATEGORY_ID);
		
		assertEquals("iPhone 5s (16GB) (Verizon)", scoredProducts.get(0).getProduct().getName());
	}
	
	@Test
	public void testReturnAllProducts() throws IOException
	{
		List<ScoredProduct> scoredProducts = aProductSearch.queryProductsReturnAll("query_matches_nothing", SMARTPHONE_CATEGORY_ID);
		
		assertEquals(133, scoredProducts.size());
	}
	
	@Test
	public void testNoDuplicateProducts() throws IOException
	{
		List<ScoredProduct> scoredProducts = aProductSearch.queryProductsReturnAll("iphone", SMARTPHONE_CATEGORY_ID);
		Set<ScoredProduct> scoredProductsNoDuplicates = new HashSet<ScoredProduct>(scoredProducts);
		assertEquals(scoredProductsNoDuplicates.size(), scoredProducts.size());
	}
	
	@Test
	public void testInvalidCategory() throws IOException
	{
		List<ScoredProduct> scoredProducts = aProductSearch.queryProducts("query doesn't matter", "123456789");
		
		assertEquals(null, scoredProducts);
	}
	

		
}
