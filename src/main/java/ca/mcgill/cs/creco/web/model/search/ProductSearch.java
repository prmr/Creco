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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import ca.mcgill.cs.creco.web.model.ProductVO;

/**
 * Searches a list of products with Lucene indexes.
 */
public class ProductSearch 
{
	public static final String NAME = "name";
	public static final String ID = "id";
	
	private static final Version VERSION = Version.LUCENE_46;
	private static final int MAX_NUM_RESULTS = 10;
	private static final Logger LOG = LoggerFactory.getLogger(ProductSearch.class);
	
	private final Directory directory;
	private final Analyzer analyzer;

	/**
	 * Constructor.
	 */
	public ProductSearch() 
	{
		directory = new RAMDirectory();
		analyzer = new EnglishAnalyzer(VERSION);
	}
	
	/**
	 * Add products to the lucene search directory, with indexes on all the product's fields.
	 * @param products list of products to store with lucene indices
	 */
	public void addProducts(List<ProductVO> products) 
	{
		try 
		{
			Analyzer analyzer = new EnglishAnalyzer(VERSION);
			IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(VERSION, analyzer));
		for (ProductVO product : products) 
		{
			Document doc = new Document();
			doc.add(new TextField(ID, product.getId(), Field.Store.YES));	
			doc.add(new TextField(NAME, product.getName(), Field.Store.YES));
			writer.addDocument(doc);
		}
		writer.close();
		}
		catch (IOException e) 
		{
			LOG.error(e.getMessage());
		}
	}
	
	/**
	 * Query the Lucene directory for matches to the query string.
	 * @param queryString the search string
	 */
	public SearchResult queryProducts(String queryString) 
	{
		List<ProductVO> scoredResults = new ArrayList<ProductVO>();
		try 
		{
			Query query = new QueryParser(VERSION, NAME, analyzer).parse(queryString);
			DirectoryReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector results = TopScoreDocCollector.create(MAX_NUM_RESULTS, true);
			
			searcher.search(query, results);
			ScoreDoc[] hits = results.topDocs().scoreDocs;
			LOG.info("Found " + hits.length + " results.");	

			for(int i = 0; i<hits.length; i++) 
			{
			    Document doc = searcher.doc(hits[i].doc);
			    LOG.info((i + 1) + ". " + doc.get(NAME));
			    
			    ProductVO scoredProduct = new ProductVO();
			    scoredProduct.setId(doc.get(ID));
			    scoredProduct.setName(doc.get(NAME));
			    scoredResults.add(scoredProduct);
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
		SearchResult searchResult = new SearchResult(scoredResults);
		return searchResult;
	}
}
