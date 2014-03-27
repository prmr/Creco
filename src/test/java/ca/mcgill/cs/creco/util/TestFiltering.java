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
package ca.mcgill.cs.creco.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.AttributeStat;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;
import ca.mcgill.cs.creco.logic.AttributeExtractor;
import ca.mcgill.cs.creco.logic.RankedFeaturesProducts;
import ca.mcgill.cs.creco.logic.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.ICategorySearch;
import ca.mcgill.cs.creco.logic.search.IProductSearch;
import ca.mcgill.cs.creco.web.controller.SiteController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestFiltering {

	private static final Logger LOG = LoggerFactory.getLogger(SiteController.class);
	
	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	IProductSearch aProductSearch;
	
	@Autowired
	private ICategorySearch aCategorySearch;
	
	@Autowired
	private AttributeExtractor aAttributeExtractor;
	
	private static String SMARTPHONE_CATEGORY_ID = "28726";
	
	
	@Test
	public void testFunction_initialreturn() throws IOException
	{
		 List<Category> categoryList = aCategorySearch.queryCategories("Toaster");
		    Category target = null;
		    for (Category cat: categoryList) {
		    	if (cat.getName().equals("Toaster")) {
		    		target = cat;
		    	}
		    }
		    
			List<Product> prodSearch = aProductSearch.returnProductsAlphabetically(target.getId());
			List<ScoredAttribute> attrList = aAttributeExtractor.getAttributesForCategory(target.getId());
		    RankedFeaturesProducts rankedProducts =new RankedFeaturesProducts(attrList, prodSearch);
		    List<Product> scoredProducts = rankedProducts.getaProductSearchResult();
			assertEquals(407, scoredProducts.size());
	}
	
	@Test
	public void testFunction_intialreturn2() throws IOException
	{
		 List<Category> categoryList = aCategorySearch.queryCategories("Digital SLR camera");
		    Category target = null;
		    for (Category cat: categoryList) {
		    	if (cat.getName().equals("Digital SLR camera")) {
		    		target = cat;
		    	}
		    }
		    
			List<Product> prodSearch = aProductSearch.returnProductsAlphabetically(target.getId());
			List<ScoredAttribute> attrList = aAttributeExtractor.getAttributesForCategory(target.getId());
		    RankedFeaturesProducts rankedProducts =new RankedFeaturesProducts(attrList, prodSearch);
		    List<Product> scoredProducts = rankedProducts.getaProductSearchResult();
			assertEquals(111, scoredProducts.size());
	}
	//TODO: write new test here
	//Test had deprecated methods and methods that don't exist anymore
//	@Test
//	public void testFunction_product_with_selected_feature() throws IOException
//	{
//		List<Category> categoryList = aCategorySearch.queryCategories("Point & shoot digital camera");
//		Category target = null;
//		for (Category cat: categoryList) 
//		{
//			if (cat.getName().equals("Point & shoot digital camera"))
//		    {
//		    	target = cat;
//		    }
//		}
//		    
//		List<Product> prodSearch = aProductSearch.returnProductsAlphabetically(target.getId());
//		List<ScoredAttribute> attrList = aAttributeExtractor.getAttributesForCategory(target.getId());
//	    RankedFeaturesProducts rankedProducts =new RankedFeaturesProducts(attrList, prodSearch);
//	    List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();
//		Attribute test= Attribute.buildSpecification( "doesnt matter", "Manual controls", "doesn't matter", true );
//		ScoredAttribute test_score = new ScoredAttribute(test,new Category("empty", "test", "test", new ArrayList<Product>(), 
//					new ArrayList<AttributeStat>(), new ArrayList<AttributeStat>()));
//		TypedValue temporary = new TypedValue(false);
//		test_score.setAttributeDefault(temporary); 
//		userScoredFeaturesSpecs.add(test_score);
//		RankedFeaturesProducts Products = new RankedFeaturesProducts();
//		List<Product> productsToDisplay = Products.FilterandReturn(userScoredFeaturesSpecs);
//		assertEquals(60, productsToDisplay.size());
//	}
	//TODO: write new test here
	//Test had deprecated methods and methods that don't exist anymore
//	@Test
//	public void testFunction_feature_setas_NA() throws IOException
//	{
//		 List<Category> categoryList = aCategorySearch.queryCategories("Convertible car seat");
//		    Category target = null;
//		    for (Category cat: categoryList) {
//		    	if (cat.getName().equals("Convertible car seat")) {
//		    		target = cat;
//		    	}
//		    }
//		    
//			List<Product> prodSearch = aProductSearch.returnProductsAlphabetically(target.getId());
//			List<ScoredAttribute> attrList = aAttributeExtractor.getAttributesForCategory(target.getId());
//		    RankedFeaturesProducts rankedProducts =new RankedFeaturesProducts(attrList, prodSearch);
//		    List<Product> scoredProducts = rankedProducts.getaProductSearchResult();
//		    List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();
//			Attribute test= Attribute.buildSpecification( "doesnt matter", "Multiple recline positions", "doesn't matter", true );
//		  ScoredAttribute test_score = new ScoredAttribute(test,new Category("empty", "test", "test", new ArrayList<Product>(), 
//					new ArrayList<AttributeStat>(), new ArrayList<AttributeStat>()));
//
//		  TypedValue temporary = new TypedValue("NA");
//		  test_score.setAttributeDefault(temporary); 
//		  userScoredFeaturesSpecs.add(test_score);
//			RankedFeaturesProducts Products = new RankedFeaturesProducts();
//			List<Product> productsToDisplay = Products.FilterandReturn(userScoredFeaturesSpecs);
//		LOG.debug(new Integer(productsToDisplay.size()).toString());
//			assertEquals(scoredProducts.size(), productsToDisplay.size());
//	}
}
