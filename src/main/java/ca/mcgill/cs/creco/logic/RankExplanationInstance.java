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
package ca.mcgill.cs.creco.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;



/**
 *
 */
public class RankExplanationInstance 
{
	private static final int RANKING_NOT_AVAILABLE = -1;
	private static final Logger LOG = LoggerFactory.getLogger(RankExplanationInstance.class);
	private ScoredAttribute aScoredAttribute;
	private int aAttributeRank;
	private TypedValue aAttributeValue;	
	private Product aProduct;
	
	/**
	 * @param pScoredAttribute the explained attribute 
	 * @param pProduct product to provide explanation to
	 * @throws 
	 */
	public RankExplanationInstance(Product pProduct, ScoredAttribute pScoredAttribute) throws IllegalArgumentException
	{
		aProduct  = pProduct;
		aScoredAttribute = pScoredAttribute;
		aAttributeRank =  RANKING_NOT_AVAILABLE;
		aAttributeValue =  new TypedValue();
		Attribute attribute = pProduct.getAttribute(aScoredAttribute.getAttributeID());
		if(attribute != null)
		{
			aAttributeValue = attribute.getTypedValue();
			aAttributeRank = pScoredAttribute.getValueRank(aAttributeValue);
		}	
		else
		{
			aAttributeRank = -1;
		}		
	}
	
	/**
	 * 
	 * @return product object.
	 */
	public Product getaProduct()
	{
		return aProduct;
	}
	
	/**
	 * 
	 * @param pProduct product associated with this explanation.
	 */
	public void setaProduct(Product pProduct)
	{
		this.aProduct = pProduct;
	}

	/**
	 * 
	 * @return the value of the attribute.
	 */
	public TypedValue getaAttributeValue()
	{
		return this.aAttributeValue;
	}
	
	/**
	 * 
	 * @return the rank of the attribute.
	 */
	public int getaAttributeRank()
	{
		return this.aAttributeRank;
	}
	/**
	 * 
	 * @return scored attribute associated with the explanation.
	 */
	public ScoredAttribute getaAttribute()
	{
		return aScoredAttribute;		
	}
	
}
