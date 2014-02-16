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
package ca.mcgill.cs.creco.web.model.search;

import java.util.Scanner;


import ca.mcgill.cs.creco.data.CRData;
import ca.mcgill.cs.creco.data.CategoryList;
import ca.mcgill.cs.creco.web.model.search.CategorySearch;

public class SearchExample {
	public static void main(String[] args) throws Exception
	{

		// Build the CRData as a Java Object
		CRData crData = new CRData();



		// ====================================
		// That's it.  Now you can use the data
		// let's look at some basic examples:
		// ====================================

		CategoryList catList = crData.getCategoryList();
		catList.dumpTree();
		CategorySearch catSearch = new CategorySearch(catList);

		Scanner scanner = new Scanner (System.in);
		String userinput= "";
		while(!userinput.equals("exit"))
		{
			System.out.print("Enter your search query (or 'exit'): ");  
			userinput = scanner.nextLine();

			catSearch.queryCategories(userinput);
		}
		scanner.close();
	}

}