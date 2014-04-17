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

import java.util.ArrayList;

/**
 * A class representing a single feature (i.e., rating and spec) to be 
 * displayed to the user front end.
 *
 */

public class FeatureView
{
	private String aId;
	private String aName;
	private double aScore;
	private String aDesc;
	private String aType;
	private ArrayList<String> aValue;
	private String aUValue;	
	private Number aMinValue;
	private Number aMaxValue;
	private Boolean aVisible;
	private Boolean aSpec;
	

	/**
	 * 
	 * @return the ID of the attribute.
	 */
	public String getId()
	{
		return aId;
	}

	/**
	 * 
	 * @param pId the ID of the attribute.
	 */
	public void setId(String pId)
	{
		this.aId = pId;
	}

	/**
	 * 
	 * @return the name of the attribute.
	 */
	public String getName()
	{
		return aName;
	}
	/**
	 * 
	 * @param pName name of the attribute.
	 */
	public void setName(String pName)
	{
		this.aName = pName;
	}
	/**
	 * 
	 * @return the value of the attribute.
	 */
	public ArrayList<String> getValue()
	{
		return aValue;
	}
	/**
	 * 
	 * @param pValue the value of the attribute.
	 */
	public void setValue(ArrayList<String> pValue)
	{
		this.aValue = pValue;
	}
	/**
	 * 
	 * @return min value of the attribute.
	 */
	public Number getMinValue()
	{
		return aMinValue;
	}
	
	/**
	 * 
	 * @param pMinValue minimum value of the attribute.
	 */
	public void setMinValue(Number pMinValue)
	{
		this.aMinValue = pMinValue;
	}
	
	/**
	 * 
	 * @return the max value of the attribute.
	 */
	public Number getMaxValue()
	{
		return aMaxValue;
	}
	/**
	 * 
	 * @param pMaxValue max value of the attribute.
	 */
	public void setMaxValue(Number pMaxValue)
	{
		this.aMaxValue = pMaxValue;
	}
	/**
	 * 
	 * @return type of the attribute.
	 */
	public String getType()
	{
		return aType;
	}
	/***
	 * 
	 * @param pType type of the attribute (Numeric, Boolean, String)
	 */
	public void setType(String pType)
	{
		this.aType = pType;
	}

	/**
	 * 
	 * @return the score rank given to the attribute.
	 */
	public double getScore()
	{
		return aScore;
	}
	/***
	 * 
	 * @param pScore the rank score given to the attribute.
	 */
	public void setScore(double pScore)
	{
		this.aScore = pScore;
	}
	/**
	 * 
	 * @return boolean value representing whether or not to expose the feature in the UI.
	 */
	public Boolean getVisible() 
	{
		return aVisible;
	}
	
	/**
	 * 
	 * @param pVisible true if the feature to be exposed to the UI.
	 */
	public void setVisible(Boolean pVisible)
	{
		this.aVisible = pVisible;
	}

	
	/**
	 * 
	 * @return true if the the feature is of type specification.
	 */
	public Boolean isSpec()
	{
		return this.aSpec;		
	}
	/**
	 * 
	 * @return user entered value.
	 */
	public String getuValue()
	{
		return aUValue;
	}
	/***
	 * 
	 * @param pUValue user entered value.
	 */
	public void setuValue(String pUValue)
	{
		this.aUValue = pUValue;
	}
	/***
	 * 
	 * @return description of the feature.
	 */
	public String getDesc() 
	{
		return aDesc;
	}
	/***
	 * 
	 * @param pDesc description of the feature
	 */
	public void setDesc(String pDesc)
	{
		this.aDesc = pDesc;
	}

}
