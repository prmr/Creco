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
public class UserFeatureModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2225290822890729985L;
	private String query;
	private ArrayList<String> ids;
	private ArrayList<String> names;
	private ArrayList<String> values;
	public String getQuery()
	{
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public ArrayList<String> getIds() {
		return ids;
	}
	public void setIds(ArrayList<String> ids) {
		this.ids = ids;
	}
	public ArrayList<String> getNames() {
		return names;
	}
	public void setNames(ArrayList<String> names) {
		this.names = names;
	}
	public ArrayList<String> getValues() {
		return values;
	}
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
	

}

