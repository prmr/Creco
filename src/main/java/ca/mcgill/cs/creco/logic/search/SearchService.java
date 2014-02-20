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

import java.io.IOException;
import java.util.List;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.logic.model.ScoredAttribute;

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
	/*
	public List<ScoredProduct> searchProducts(String eqId, String query)
	{
		return categorySearch.queryCategories(query);
	}
	*/
	
	
}
