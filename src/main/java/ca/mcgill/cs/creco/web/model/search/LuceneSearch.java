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
package ca.mcgill.cs.creco.web.model.search;

import java.util.ArrayList;

import ca.mcgill.cs.creco.web.model.ProductVO;

public class LuceneSearch implements ISearch
{
	private CategorySearch aCategorySearch;
	private ProductSearch aProductSearch;
	
	public void init()
	{
		aCategorySearch = new CategorySearch();
		aProductSearch = new ProductSearch();
	}
	public SearchResult query(String pQueryString)
	{
		//Category category = aCategorySearch.query(pQueryString);
		//List<Product> products = aProductSearch.query(pQueryString);
		return new SearchResult(new ArrayList<ProductVO>());
	}
}
