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

import ca.mcgill.cs.creco.data.Product;

/**
 * 
 * Product scored according to the search query and its features.
 */
public class ScoredProduct
{
	
	private final Product aProduct;
	private float aLuceneScore;
	private String aCategoryID;
		
	public ScoredProduct(Product pProduct, float pLuceneScore, String pCategoryID)
	{
		this.aProduct = pProduct;
		this.aLuceneScore = pLuceneScore;
		this.aCategoryID = pCategoryID;
	}
	
	public Product getProduct()
	{
		return aProduct;
	}
		
	public float getLuceneScore()
	{
		return aLuceneScore;
	}
	
	public String getEqClassId()
	{
		return aCategoryID;
	}	
		
}