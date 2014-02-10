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
package ca.mcgill.cs.creco.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import ca.mcgill.cs.creco.data.stubs.JCategory;

import com.google.gson.Gson;

/**
 * Loads the category tree from disk and prints it on the console.
 */
public final class ListCategories
{
	private static final String PATH_CATEGORIES = "categories.json";
	
	private ListCategories(){}
	
	/**
	 * See class description. Rename to 'main' to run. Having two main
	 * methods breaks the SpringBoot build.
	 * @param pArgs Takes no argument.
	 * @throws Exception Throws anything. This is a demonstration method.
	 */
	public static void main1(String[] pArgs) throws Exception
	{
		JCategory[] categories = getGson(DataPath.get() + PATH_CATEGORIES, JCategory[].class);
		
		for( JCategory category : categories )
		{
			category.print();
		}
	}
	
	private static <T> T getGson(String pJsonUri, Class<T> pType) throws FileNotFoundException
	{
		InputStreamReader json = new InputStreamReader(new FileInputStream(pJsonUri));
		T gson = new Gson().fromJson(json, pType);
		return gson;
	}
}
