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
package ca.mcgill.cs.creco.logic.search;

import java.util.Scanner;

public class SearchExample {
	public static void main1(String[] args) throws Exception
	{
		SearchService search = new SearchService();

		Scanner scanner = new Scanner (System.in);
		String userinput= "";
		while(!userinput.equals("exit"))
		{
			System.out.print("Enter your search query (or 'exit'): ");  
			userinput = scanner.nextLine();

			// Just used to debug, this re-initializes the index
			if (userinput.equals("refresh")){
				search = new SearchService();
			}

			search.searchCategories(userinput);
		}
		scanner.close();
	}

}