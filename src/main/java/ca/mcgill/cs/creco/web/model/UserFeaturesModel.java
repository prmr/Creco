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

import java.io.Serializable;

/**
 *
 */
public class UserFeaturesModel implements Serializable
{
	private static final long serialVersionUID = -2225290822890729985L;
	private String aQuery;
	private String aId;
	private String aName;
	private double aValue;
	
	/**
	 * 
	 * @return the search query.
	 */
	public String getQuery()
	{
		return aQuery;
	}
	
	/**
	 * 
	 * @param pQuery search query.
	 */
	public void setQuery(String pQuery)
	{
		aQuery = pQuery;
	}
	
	/**
	 * 
	 * @return a list of ids for the selected features.
	 */
	public String getId()
	{
		return aId;
	}
	/**
	 * 
	 * @param pIds a list of ids for the selected features.
	 */
	public void setId(String pId)
	{
		aId = pId;
	}
	/**
	 * 
	 * @return a list of names of the selected features.
	 */
	public String getName()
	{
		return aName;
	}
	/**
	 * 
	 * @param pNames the names of the selected features.
	 */
	public void setName(String pName)
	{
		aName = pName;
	}
	/**
	 * 
	 * @return list of values for the features.
	 */
	public double getValue()
	{
		return aValue;
	}
	
	/**
	 * 
	 * @param pValues the values of the features.
	 */
	public void setValue(double pValue)
	{
		aValue = pValue;
	}

}

