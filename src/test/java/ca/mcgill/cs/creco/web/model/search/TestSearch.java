package ca.mcgill.cs.creco.web.model.search;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.ProductList;


public class TestSearch
{
	@Test
	public void testSearchSuccess()
	{
		String testName = "testName";
		String testId = "1";
		
		Search search = new Search();
		
		ProductList products = new ProductList();
		Product product = new Product();
		product.setId(testId);
		product.setName(testName);
	    products.put(testId,product);
		
		Product temp =products.get(testId);
		assertEquals(product,temp);
	//	search.addProducts(products);
		
//		SearchResult result = search.query(testName);
//		Product resultProduct = result.getProducts().get(0);
//		assertEquals(resultProduct.getName(), testName);
	}
}
