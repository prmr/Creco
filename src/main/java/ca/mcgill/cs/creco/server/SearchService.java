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
package ca.mcgill.cs.creco.server;

import java.io.IOException;
import java.util.List;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.logic.AttributeExtractor;
import ca.mcgill.cs.creco.logic.model.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.CategorySearch;
import ca.mcgill.cs.creco.logic.search.ProductSearch;
import ca.mcgill.cs.creco.web.model.search.ProductSearchResult;

public class SearchService {

	private CategorySearch categorySearch;
	private ProductSearch productSearch;
	
	public SearchService() throws IOException
	{
		this.categorySearch = new CategorySearch();
		// TODO: add product search here
		//this.productSearch = new ProductSearch();
	}
	
	public List<Category> searchCategories(String query)
	{
		return categorySearch.queryCategories(query);
	}
	
	public RankedFeaturesProducts getRankedFeaturesProducts(Category eqClass, String query)
	{
		ProductSearchResult prodSearch = this.getProductSearchResult(eqClass, query);
		List<ScoredAttribute> ratingList = this.getRatingList(eqClass, prodSearch);
		List<ScoredAttribute> specList = this.getSpecList(eqClass, prodSearch);
		return new RankedFeaturesProducts(ratingList, specList, prodSearch);
	}
	
	private List<ScoredAttribute> getRatingList(Category eqClass, ProductSearchResult prodSearch)
	{
		AttributeExtractor ae = new AttributeExtractor(prodSearch, eqClass);
		return ae.getScoredRatingList();
	}
	
	private List<ScoredAttribute> getSpecList(Category eqClass, ProductSearchResult prodSearch)
	{
		AttributeExtractor ae = new AttributeExtractor(prodSearch, eqClass);
		return ae.getScoredSpecList();
	}
	
	private ProductSearchResult getProductSearchResult(Category eqClass, String query)
	{
		// Gowri -- make this code call your product search.
		return null;
	}
	/*
	public List<ScoredProduct> searchProducts(String eqId, String query)
	{
		return categorySearch.queryCategories(query);
	}
	*/
}
