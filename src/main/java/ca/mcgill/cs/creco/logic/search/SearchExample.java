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

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import ca.mcgill.cs.creco.data.CRData;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.CategoryList;
import ca.mcgill.cs.creco.server.SearchService;

public class SearchExample {
	public static void productSearchExample() throws IOException
	{
		CRData crData = CRData.getData();
		CategoryList categoryList = crData.getCategoryList();
		SearchService search = new SearchService();

		Scanner scanner = new Scanner (System.in);
		String productName= "";
		String eqClassId ="";
		while(!productName.equals("exit"))
		{
			System.out.print("Enter your search query (or 'exit'): ");  
			productName = scanner.nextLine();
			System.out.print("Enter the equivalence class id : ");  
			eqClassId= scanner.nextLine();
			Category category = categoryList.get(eqClassId);
			//  A sample query
			//queryProducts("Avalanche 21-014","33118"); 

			// Just used to debug, this re-initializes the index
			if (productName.equals("refresh")){
				search = new SearchService();
			}
			
			List<ScoredProduct> scoredProducts = search.searchProducts(category, productName);
			
			for(ScoredProduct scoredProduct : scoredProducts){
				
				System.out.println("Lucene Score : "+scoredProduct.getLuceneScore()+"\tProduct Name : "+scoredProduct.getProduct().getName()
						+"\tEquivalence Class Id : "+scoredProduct.getEqClassId());
			}
		}
		scanner.close();

	}
	
	public static void categorySearchExample() throws Exception
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
	
	public static void main(String[] args)
	{
		// categorySearchExample();
		try
		{
			productSearchExample();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

}