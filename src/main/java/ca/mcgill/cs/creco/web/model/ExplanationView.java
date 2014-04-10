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

import ca.mcgill.cs.creco.data.TypedValue;

/**
 * View object for the explanation of products ranks.
 */
public class ExplanationView 
{
	private String aName;
	private TypedValue aValue;
	private int aValueRank;
	private double aAttrScore;
	private boolean aIsBoolean;
	private boolean aIsString;
	private boolean aIsNumeric;

	/**
	 * 
	 * @param pName name of the attribute to explain.
	 * @param pValue value of the attribute.
	 * @param pValueRank rank of the value.
	 * @param pAttrScore score of the attribute.
	 */
	public ExplanationView(String pName, TypedValue pValue, int pValueRank, double pAttrScore)
	{
		aName = pName;
		aValue = pValue;
		aValueRank = pValueRank;
		aAttrScore = pAttrScore;
		aIsBoolean = pValue.isBoolean();
		aIsString = pValue.isString();
		aIsNumeric = pValue.isNumeric();
	}
	
	/**
	 * 
	 * @return the name of the attribute to explain. 
	 */
	public String getName() 
	{
		return aName;
	}
	/**
	 * 
	 * @return the value of the attribute to explain.
	 */
	public TypedValue getValue() 
	{
		return aValue;
	}
	
	/**
	 * 
	 * @return the rank of the attribute value among a given category.
	 */
	public int getValueRank() 
	{
		return aValueRank;
	}
	
	/**
	 * 
	 * @return true if the attribute is of type boolean.
	 */
	public boolean getIsBoolean()
	{
		return aIsBoolean;
	}

	/**
	 * 
	 * @return true if the attribute is of type Numeric.
	 */
	public boolean getIsNumeric()
	{
		return aIsNumeric;
	}
	/**
	 * 
	 * @return true if the attribute is of type String.
	 */
	public boolean getIsString()
	{
		return aIsString;
	}

	/**
	 * 
	 * @return the score of the attribute.
	 */
	public double getAttrScore()
	{
		return aAttrScore;
	}	

}
