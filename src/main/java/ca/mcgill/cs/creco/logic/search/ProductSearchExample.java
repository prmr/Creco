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
public class ProductSearchExample{
	
	public static void main(String[] args) throws IOException{
		
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
			//  A sample query
			//queryProducts("Avalanche 21-014","33118"); 

			// Just used to debug, this re-initializes the index
			if (productName.equals("refresh")){
				search = new SearchService();
			}
			
			ProductSearchResults pro=search.searchProducts(productName,eqClassId);
			List<ProductSearchResult> products =pro.getfinalScoredProducts();
			
			for(int i=0;i<products.size();i++){
				
				System.out.println("Lucene Score : "+products.get(i).getLuceneScore()+"\tProduct Name : "+products.get(i).getProduct().getName()
						+"\tEquivalence Class Id : "+products.get(i).getEqClassId());
			}
		}
		scanner.close();

	}
	
}