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

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.ScoredAttribute.Direction;
import ca.mcgill.cs.creco.data.*;

/**
 *
 */
public class RankExplanation 
{
	private List<ScoredAttribute> aAttribute;
	private double aAttributeRank;
	private double aAttributeValue;	
	private Product aProduct;
	/**
	 * 
	 * @param pProduct product to provide explanation to
	 */
	public RankExplanation(Product pProduct)
	{
		this.setaProduct(pProduct);
		this.aAttribute = new ArrayList<ScoredAttribute>();
	}
	
	/**
	 * @param pAttr
	 * @param pProduct
	 * @return ranking explanation
	 */
	public RankExplanation getExplanation(Product pProduct, ScoredAttribute pAttr)
	{
//		this.aAttribute.add(pAttr);
		this.aProduct = pProduct;
/*		if(pProduct.getAttribute(pAttr.getAttributeID()) == null)
		{
			this.aAttributeValue = -1;		
			this.aAttributeRank = -1;
		}
		else
		{
*/			TypedValue tval = pProduct.getAttribute(pAttr.getAttributeID()).getTypedValue();
			if(tval.isNull() || tval.isNA())
			{
				this.aAttributeValue = 0;	
				this.aAttributeRank = 0;
			}
			else
			{
				this.aAttribute.add(pAttr);				
				this.aAttributeValue = pProduct.getAttribute(pAttr.getAttributeID()).getTypedValue().getNumeric();		
				this.aAttributeRank = pAttr.getValueRank(pProduct.getAttribute(pAttr.getAttributeID()).getTypedValue());
				
			}						
//		}				
		return this;
		
	}
	public Product getaProduct() {
		return aProduct;
	}
	public void setaProduct(Product aProduct) {
		this.aProduct = aProduct;
	}

	public double getaAttributeValue()
	{
		return this.aAttributeValue;
	}
	public double getaAttributeRank()
	{
		return this.aAttributeRank;
	}
	
	public void setaAttribute(List<ScoredAttribute> pAttribute)
	{
		this.aAttribute = pAttribute;
		
	}
	
	public List<ScoredAttribute> getaAttribute()
	{
		return this.aAttribute;		
	}
	
	public void addaAttribute(ScoredAttribute pAttribute)
	{
		this.aAttribute.add(pAttribute);
		
	}
}
