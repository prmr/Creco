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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.mcgill.cs.creco.data.CategoryBuilder;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;

/**
 * Searches a list of products ordered by equivalence classes with Lucene indexes.
 */
@Component
public class ProductSearch implements IProductSearch
{
	public static final String NAME = "name";
	public static final String ID = "id";
	
	private static final Version VERSION = Version.LUCENE_46;
	private static final int MAX_NUM_RESULTS = 1000;
	private static final Logger LOG = LoggerFactory.getLogger(ProductSearch.class);
	
	private final Directory aDirectory;
	private final Analyzer aAnalyzer;

	private IDataStore aDataStore;
	

	/**
	 * Constructor.
	 * @param pDataStore The database whose products will be in the search index. 
	 * @throws IOException If an exception is thrown during the creation of the product index.
	 */
	@Autowired
	public ProductSearch(IDataStore pDataStore) throws IOException
	{
		aDirectory = new RAMDirectory();
		aAnalyzer = new EnglishAnalyzer(VERSION);
		aDataStore = pDataStore;
		
		buildProductIndexByCategory();
	}
	
	/**
	 * Add products into the Lucene directory.
	 */
	private void buildProductIndexByCategory() throws IOException
	{
		//TODO: Might be more efficient to have a different index for each equivalence class,
		// instead of searching all products in a single index, and then filtering out the
		// results outside the equivalence class.

		Analyzer analyzer = new EnglishAnalyzer(VERSION);
		IndexWriter writer = new IndexWriter(aDirectory,
				new IndexWriterConfig(VERSION, analyzer));
		for (Category category : aDataStore.getCategories())
		{
			for (Product product : category.getProducts()) 
			{
				Document doc = new Document();
				doc.add(new TextField(ID, product.getId(), Field.Store.YES));
				doc.add(new TextField(NAME, product.getName(), Field.Store.YES));
				writer.addDocument(doc);
				}
		}
		writer.close();
		
	}
	
	/**
	 * Query the Lucene directory for matches to the query string.
	 * @param pQueryString the search string
	 * @param pCategoryID the id of the equivalence class
	 * @return ProductSearchResults an object of ProductSearchResults
	 */
	@Override
	public List<ScoredProduct> queryProducts(String pQueryString, String pCategoryID)
	{
		Category category = aDataStore.getCategory(pCategoryID);
		if (category == null)
		{
			LOG.error("Invalid category ID: " + pCategoryID);
			return null;
		}
		
		Map<String, Product> productsInCategory = new HashMap<String, Product>();
		List<ScoredProduct> scoredProducts = new ArrayList<ScoredProduct>();
		
		for(Product product :category.getProducts())
		{
			productsInCategory.put(product.getId(), product);
		}
		
		try 
		{
			Query query = new QueryParser(VERSION, NAME, aAnalyzer).parse(pQueryString);
			DirectoryReader reader = DirectoryReader.open(aDirectory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector results = TopScoreDocCollector.create(MAX_NUM_RESULTS, true);
			
			searcher.search(query, results);

			for(ScoreDoc scoredResult : results.topDocs().scoreDocs) 
			{
			    Document doc = searcher.doc(scoredResult.doc);
			    if(productsInCategory.containsKey(doc.get(ID)))
			    {
			    	Product product = productsInCategory.get(doc.get(ID));
			    	LOG.info(scoredResult.score + ". " + doc.get(NAME));
			    	scoredProducts.add(new ScoredProduct(product, scoredResult.score, pCategoryID));
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
	
	@Override
	public List<ScoredProduct> queryProductsReturnAll(String pQueryString, String pCategoryId)
	{
		List<ScoredProduct> scoredProducts = queryProducts(pQueryString, pCategoryId);
		List<Product> matchingProducts = new ArrayList<Product>();
		for (ScoredProduct scoredProduct : scoredProducts)
		{
			matchingProducts.add(scoredProduct.getProduct());
		}
		
		// Add remaining products from the category, even if they didn't match the query
		Category category = aDataStore.getCategory(pCategoryId);
		for (Product product : category.getProducts())
		{
			if (!matchingProducts.contains(product))
			{
				ScoredProduct scoredProduct = new ScoredProduct(product, 0, pCategoryId);
				scoredProducts.add(scoredProduct);
			}
		}
		return scoredProducts;
	}

}