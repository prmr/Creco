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
import java.util.ArrayList;

/**
 *
 */
public class UserFeatureModel implements Serializable
{
	private static final long serialVersionUID = -2225290822890729985L;
	private String aQuery;
	private ArrayList<String> aIds;
	private ArrayList<String> aNames;
	private ArrayList<String> aValues;
	
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
	public ArrayList<String> getIds()
	{
		return aIds;
	}
	/**
	 * 
	 * @param pIds a list of ids for the selected features.
	 */
	public void setIds(ArrayList<String> pIds)
	{
		aIds = pIds;
	}
	/**
	 * 
	 * @return a list of names of the selected features.
	 */
	public ArrayList<String> getNames()
	{
		return aNames;
	}
	/**
	 * 
	 * @param pNames the names of the selected features.
	 */
	public void setNames(ArrayList<String> pNames)
	{
		aNames = pNames;
	}
	/**
	 * 
	 * @return list of values for the features.
	 */
	public ArrayList<String> getValues()
	{
		return aValues;
	}
	
	/**
	 * 
	 * @param pValues the values of the features.
	 */
	public void setValues(ArrayList<String> pValues)
	{
		aValues = pValues;
	}

}

