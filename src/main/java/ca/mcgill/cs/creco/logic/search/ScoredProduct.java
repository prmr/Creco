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

import java.util.List;

import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.ProductList;

/**
 * 
 * A single product returned by ProductSearch(Lucene)
 */


class ScoredProduct{
	
private final Product product;
private float luceneScore;
private String eqClassId;
	
	public ScoredProduct(Product product, float luceneScore, String eqClassId){

		this.product = product;
		this.luceneScore=luceneScore;
		this.eqClassId=eqClassId;
	}
	
	public Product getProduct()
	{
		return product;
	}
	
	public float getLuceneScore()
	{
		return luceneScore;
	}

	public String getEqClassId()
	{
		return eqClassId;
	}
	
	
	
}