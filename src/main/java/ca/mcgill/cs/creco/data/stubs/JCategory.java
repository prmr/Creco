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
package ca.mcgill.cs.creco.data.stubs;

/**
 * Stub for a json object. Do not change the field name
 * because they have to match the json keys.
 * Add getters as required.
 */
public class JCategory
{
	private String url;
	private String productGroupId;
	private String singularName;
	private String pluralName;
	private String imageCanonical;
	private String name;
	private String id;
	private String type;
	private String groupClassification;
	private int testedProductsCount;
	private int ratedProductsCount;
	private int productsCount;
	private String products;
	private int servicesCount;
	private int materialsCount;
	private int testedProductsOnlyCount;
	private JDownLevel downLevel;
	
	public void print()
	{
		print("");
	}
	
	public void print(String prefix)
	{
		System.out.println(prefix + name + "(" + productsCount + ")");
		if( downLevel != null )
		{
			downLevel.print(prefix + "  ");
		}
	}
	
	public static class JDownLevel
	{
		JCategory[] subfranchise;
		JCategory[] supercategory;
		
		public void print(String prefix)
		{
			if( subfranchise != null )
			{
				for( JCategory c : subfranchise )
				{
					c.print(prefix);
				}
			}
			if( supercategory != null )
			{
				for( JCategory c : supercategory )
				{
					c.print(prefix);
				}
			}
		}
	}
}
