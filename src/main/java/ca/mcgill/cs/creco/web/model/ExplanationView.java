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

import java.util.List;

import ca.mcgill.cs.creco.logic.ScoredAttribute;

/**
 *
 */
public class ExplanationView 
{
	private String aName;
	private double aValue = 10;
	private double aValueRank;
	private double aAttrRank;
	
	
	public ExplanationView(String pName, double pValue, double pValueRank, double pAttrRank)
	{
		aName = pName;
		aValue = pValue;
		aValueRank = pValueRank;
		aAttrRank = pAttrRank;
	}
	
	public String getName() 
	{
		return aName;
	}
	
	public double getValue() 
	{
		return aValue;
	}
	
	public double getValueRank() 
	{
		return aValueRank;
	}
	
	public double getAttrRank() 
	{
		return aAttrRank;
	}
	

}
