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
package ca.mcgill.cs.creco.web.model;

/**
 * Represents information about a product to show in the web front-end. Immutable.
 */
public class ProductView
{
	private String aId;
	private String aName;
	private String aUrl;
	private String aExplanation;
	
	/**
	 * Creates a new product.
	 * @param pId The product's idea.
	 * @param pName The display name of the product.
	 * @param pUrl The url pointing to the product's web page on the CR database.
	 * @param pExplanation 
	 */
	public ProductView( String pId, String pName, String pUrl, String pExplanation)
	{
		aId = pId;
		aName = pName;
		aUrl = pUrl;
		aExplanation = pExplanation;
	}

	/**
	 * @return The product's id.
	 */
	public String getId()
	{
		return aId;
	}

	/**
	 * @return The product's display name.
	 */
	public String getName()
	{
		return aName;
	}
	
	/**
	 * @return The product page's URL.
	 */
	public String getUrl()
	{
		return aUrl;
	}

	/***
	 * 
	 * @return The product Ranking Explanation
	 */
	public String getExplanation() 
	{
		return aExplanation;
	}	
}
