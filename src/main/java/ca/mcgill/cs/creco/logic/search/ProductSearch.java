/**
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.creco.data.CRData;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;

/**
 * Searches a list of products ordered by equivalence classes with Lucene indexes.
 */
public class ProductSearch 
{
	public static final String NAME = "name";
	public static final String ID = "id";
	
	private static final Version VERSION = Version.LUCENE_46;
	private static final int MAX_NUM_RESULTS = 1000;
	private static final Logger LOG = LoggerFactory.getLogger(ProductSearch.class);
	
	private final Directory directory;
	private final Analyzer analyzer;

	/**
	 * Constructor.
	 * @throws IOException 
	 */
	public ProductSearch() throws IOException 
	{
		directory = new RAMDirectory();
		analyzer = new EnglishAnalyzer(VERSION);
		buildProductIndexByCategory();
	}
	
	/**
	 * Add products into the Lucene directory.
	 */
	private void buildProductIndexByCategory() 
	{
		//TODO: Might be more efficient to have a different index for each equivalence class,
		// instead of searching all products in a single index, and then filtering out the
		// results outside the equivalence class.
		
		try 
		{
			Analyzer analyzer = new EnglishAnalyzer(VERSION);
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(VERSION, analyzer));

			for (Category category : CRData.getData().getEquivalenceClasses())
			{
				for (Product product : category.getProducts()) 
				{
					// LOG.debug("Adding category : " + category.getName()+" id : "+category.getId()+" product : "+product.getName());
					Document doc = new Document();
					doc.add(new TextField(ID, product.getId(), Field.Store.YES));
					doc.add(new TextField(NAME, product.getName(), Field.Store.YES));
					writer.addDocument(doc);

				}
			}
			writer.close();
		} catch (IOException e) 
		{
			LOG.error(e.getMessage());
		}

	}
	
	/**
	 * Query the Lucene directory for matches to the query string.
	 * @param queryString the search string
	 * @param eqClassID the id of the equivalence class
	 * @return ProductSearchResults an object of ProductSearchResults
	 */
	public List<ScoredProduct> queryProducts(String queryString, String eqClassID) 
	{
		Category c = null;
		
		try{c = CRData.getData().get(eqClassID);} catch(IOException e) {LOG.error(e.getMessage());}
		if (c == null)
		{
			LOG.error("Invalid category ID: " + eqClassID);
			return null;
		}
		
		Map<String, Product> allProductsOfUserEq = new HashMap<String, Product>();
		List<ScoredProduct> scoredProducts = new ArrayList<ScoredProduct>();
		
		for(Product product :c.getProducts())
		{
			allProductsOfUserEq.put(product.getId(), product);
		}
		try 
		{
			Query query = new QueryParser(VERSION, NAME, analyzer).parse(queryString);
			DirectoryReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector results = TopScoreDocCollector.create(MAX_NUM_RESULTS, true);
			
			searcher.search(query, results);
			ScoreDoc[] hits = results.topDocs().scoreDocs;
			//LOG.info("Found " + hits.length + " results.");	

			for(int i = 0; i<hits.length; i++) 
			{
			    Document doc = searcher.doc(hits[i].doc);
			    
			    if(allProductsOfUserEq.containsKey(doc.get(ID)))
			    {
			    	Product p=allProductsOfUserEq.get(doc.get(ID));
			    	LOG.info(hits[i].score + ". " + doc.get(NAME));
			    	scoredProducts.add(new ScoredProduct(p,hits[i].score,eqClassID));
			    }
			   
			}
			
		}
		catch (ParseException e)
		{
			LOG.error(e.getMessage());
		}
		catch (IOException e) 
		{
			LOG.error(e.getMessage());
		}	
		return scoredProducts;
		
	}
	
	/**
	 * Searches all products within an equivalence class. Returns a sorted list of scored products
	 * where the first products match the search query the most. Products within the equivalence
	 * class which do not match the search query at all are still appended to the results with
	 * a score of 0.
	 * @param queryString
	 * @param eqClassID
	 * @return
	 */
	public List<ScoredProduct> queryProductsReturnAll(String queryString, String eqClassID) {
		List<ScoredProduct> scoredProducts = queryProducts(queryString, eqClassID);
		List<Product> matchingProducts = new ArrayList<Product>();
		for (ScoredProduct scoredProduct : scoredProducts)
		{
			matchingProducts.add(scoredProduct.getProduct());
		}
		
		Category category = null;
		
		try{category = CRData.getData().get(eqClassID);} catch(IOException e) {LOG.error(e.getMessage());}
		for (Product product : category.getProducts())
		{
			if (!matchingProducts.contains(product))
			{
				ScoredProduct scoredProduct = new ScoredProduct(product, 0, eqClassID);
				scoredProducts.add(scoredProduct);
			}
		}
		return scoredProducts;
	}

}