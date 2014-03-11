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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.TypedValue;
import ca.mcgill.cs.creco.logic.AttributeExtractor;
import ca.mcgill.cs.creco.logic.AttributeValue;
import ca.mcgill.cs.creco.logic.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.ICategorySearch;
import ca.mcgill.cs.creco.logic.search.IProductSearch;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestFiltering {

	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	IProductSearch aProductSearch;
	
	@Autowired
	private ICategorySearch aCategorySearch;
	
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
		    
			List<ScoredProduct> prodSearch = aProductSearch.queryProductsReturnAll("Toaster", target.getId());
			AttributeExtractor ae = new AttributeExtractor(prodSearch, target);
			List<ScoredAttribute> ratingList = ae.getScoredRatingList();
			List<ScoredAttribute> specList = ae.getScoredSpecList();
		    RankedFeaturesProducts rankedProducts =new RankedFeaturesProducts(ratingList, specList, prodSearch);
		    List<ScoredProduct> scoredProducts = rankedProducts.getaProductSearchResult();
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
		    
			List<ScoredProduct> prodSearch = aProductSearch.queryProductsReturnAll("Digital SLR camera", target.getId());
			AttributeExtractor ae = new AttributeExtractor(prodSearch, target);
			List<ScoredAttribute> ratingList = ae.getScoredRatingList();
			List<ScoredAttribute> specList = ae.getScoredSpecList();
		    RankedFeaturesProducts rankedProducts =new RankedFeaturesProducts(ratingList, specList, prodSearch);
		    List<ScoredProduct> scoredProducts = rankedProducts.getaProductSearchResult();
			assertEquals(111, scoredProducts.size());
	}
	
	@Test
	public void testFunction_product_with_selected_feature() throws IOException
	{
		 List<Category> categoryList = aCategorySearch.queryCategories("Point & shoot digital camera");
		    Category target = null;
		    for (Category cat: categoryList) {
		    	if (cat.getName().equals("Point & shoot digital camera")) {
		    		target = cat;
		    	}
		    }
		    
			List<ScoredProduct> prodSearch = aProductSearch.queryProductsReturnAll("Point & shoot digital camera", target.getId());
			AttributeExtractor ae = new AttributeExtractor(prodSearch, target);
			List<ScoredAttribute> ratingList = ae.getScoredRatingList();
			List<ScoredAttribute> specList = ae.getScoredSpecList();
		    RankedFeaturesProducts rankedProducts =new RankedFeaturesProducts(ratingList, specList, prodSearch);
		    List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();
			Attribute test= new Attribute( "doesnt matter", "Manual controls", "doesn't matter", true );
		  ScoredAttribute test_score = new ScoredAttribute(test,new Category("test", "test", null));
		  TypedValue temporary = new TypedValue(false);
		  test_score.setAttributeMean(temporary); 
		  userScoredFeaturesSpecs.add(test_score);
			RankedFeaturesProducts Products = new RankedFeaturesProducts();
			List<ScoredProduct> productsToDisplay = Products.FilterandReturn(userScoredFeaturesSpecs);
		System.out.println(productsToDisplay.size());
			assertEquals(60, productsToDisplay.size());
	}
	@Test
	public void testFunction_feature_setas_NA() throws IOException
	{
		 List<Category> categoryList = aCategorySearch.queryCategories("Convertible car seat");
		    Category target = null;
		    for (Category cat: categoryList) {
		    	if (cat.getName().equals("Convertible car seat")) {
		    		target = cat;
		    	}
		    }
		    
			List<ScoredProduct> prodSearch = aProductSearch.queryProductsReturnAll("Convertible car seat", target.getId());
			AttributeExtractor ae = new AttributeExtractor(prodSearch, target);
			List<ScoredAttribute> ratingList = ae.getScoredRatingList();
			List<ScoredAttribute> specList = ae.getScoredSpecList();
		    RankedFeaturesProducts rankedProducts =new RankedFeaturesProducts(ratingList, specList, prodSearch);
		    List<ScoredProduct> scoredProducts = rankedProducts.getaProductSearchResult();
		    List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();
			Attribute test= new Attribute( "doesnt matter", "Multiple recline positions", "doesn't matter", true );
		  ScoredAttribute test_score = new ScoredAttribute(test,new Category("test", "test", null));
		  TypedValue temporary = new TypedValue("NA");
		  test_score.setAttributeMean(temporary); 
		  userScoredFeaturesSpecs.add(test_score);
			RankedFeaturesProducts Products = new RankedFeaturesProducts();
			List<ScoredProduct> productsToDisplay = Products.FilterandReturn(userScoredFeaturesSpecs);
		System.out.println(productsToDisplay.size());
			assertEquals(scoredProducts.size(), productsToDisplay.size());
	}
}
