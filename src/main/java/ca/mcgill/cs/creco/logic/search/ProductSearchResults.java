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

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.ProductList;

/**
 * 
 * A list of all returned products
 * The final return object of ProductSearch(Lucene)
 */


class ProductSearchResults{
	
	private List<ProductSearchResult> finalScoredProducts = new ArrayList<ProductSearchResult>();
	
	public ProductSearchResults(List<ProductSearchResult> finalScoredProducts){
	
		this.finalScoredProducts = finalScoredProducts;
	}
	
	
	public List<ProductSearchResult> getfinalScoredProducts()
	{
		return this.finalScoredProducts;
	}
	
	
	
	
	
	
	
	
	
}