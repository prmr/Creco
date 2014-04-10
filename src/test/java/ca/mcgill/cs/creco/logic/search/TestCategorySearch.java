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

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestCategorySearch {

	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	ICategorySearch aCategorySearch;
	

	@Test
	public void testExactStringMatch() throws IOException
	{
		List<Category> categories = aCategorySearch.queryCategories("Smart phone");
		
		assertEquals("Smart phone",categories.get(0).getName());
	}
	
	@Test
	public void testTypoStringMatch() throws IOException
	{
		List<Category> categories = aCategorySearch.queryCategories("Smart phoneZ");
		
		assertEquals("Smart phone",categories.get(0).getName());
	}
	
	@Test
	public void testExactProductMatch() throws IOException
	{
		List<Category> categories = aCategorySearch.queryCategories("iPhone 5");
		
		assertEquals("Smart phone",categories.get(0).getName());
	}
	
	@Test
	public void testNoDuplicateCategories() throws IOException
	{
		List<Category> categories = aCategorySearch.queryCategories("Toaster oven");
		
		Set<Category> categoriesNoDuplicates = new HashSet<Category>(categories);
		
		assertEquals(categories.size(), categoriesNoDuplicates.size());
	}
		
}
