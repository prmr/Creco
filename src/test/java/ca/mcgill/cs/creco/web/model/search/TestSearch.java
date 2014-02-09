package ca.mcgill.cs.creco.web.model.search;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.mcgill.cs.creco.web.model.ProductVO;

public class TestSearch
{
	@Test
	public void testSearchSuccess()
	{
		String testName = "testName";
		String testId = "1";
		
		Search search = new Search();
		
		List<ProductVO> products = new ArrayList<ProductVO>();
		ProductVO product = new ProductVO();
		product.setId(testId);
		product.setName(testName);
		products.add(product);
		search.addProducts(products);
		
		SearchResult result = search.query(testName);
		ProductVO resultProduct = result.getProducts().get(0);
		assertEquals(resultProduct.getName(), testName);
	}
}
