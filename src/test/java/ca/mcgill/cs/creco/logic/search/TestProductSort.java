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
package ca.mcgill.cs.creco.logic.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestProductSort {

	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	ProductSort aProductSort;
	
	@Test
	public void testInvalidCategory() throws IOException
	{
		List<Product> sortedProducts = aProductSort.returnTopProducts("123456789");
		assertEquals(null, sortedProducts);
	}
	
	@Test 
	public void testSizeOfProductsReturned()
	{
		List<Product> sortedProducts = aProductSort.returnTopProducts("32968");
		assertEquals(20, sortedProducts.size());
		
	}
	
	@Test
	public void testTopProductSort()
	{
		List<Product> sortedProducts = aProductSort.returnTopProducts("28701");
		Double previousScore = sortedProducts.get(0).getOverallScore();
		for(Product product : sortedProducts)
		{
			assertTrue(product.getOverallScore() <= previousScore );
			previousScore = product.getOverallScore();
		}
		
	}
	
}
