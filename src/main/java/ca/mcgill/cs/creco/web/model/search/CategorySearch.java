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
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.CategoryList;
import ca.mcgill.cs.creco.data.Product;


/**
 * Searches a list of categories with Lucene indexes.
 */
public class CategorySearch 
{
	public static final String CATEGORY_ID = "ID";
	public static final String CATEGORY_NAME = "NAME";
	public static final String FLATTENED_TEXT = "FLATTENED_TEXT";
	
	private static final int MAX_NUM_RESULTS = 10;
	private static final Version VERSION = Version.LUCENE_46;
	private static final Logger LOG = LoggerFactory.getLogger(CategorySearch.class);
	
	private final Directory directory;
	private final Analyzer analyzer;
	
	// TEMPORARY
	private CategoryList categoryList;

	/**
	 * Constructor.
	 */
	public CategorySearch(CategoryList categoryList) 
	{
		directory = new RAMDirectory();
		analyzer = new EnglishAnalyzer(VERSION);
		
		this.categoryList = categoryList;
		buildCategoryIndex(categoryList);
	}
	
	/**
	 * Add equivalence classes into the Lucene directory.
	 */
	private void buildCategoryIndex(CategoryList categoryList) 
	{
		try 
		{
			Analyzer analyzer = new EnglishAnalyzer(VERSION);
			IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(VERSION, analyzer));
		
			for (Category category : categoryList.getEqClasses()) {
				String flattenedText = "";
				LOG.debug("Adding " + category.getName());
				for (Product product : category.getProducts())
				{
					// flattenedText += product.getName() + " ";
					//flattenedText += product.getBrandName() + " ";
					//flattenedText += product.getHighs() + " ";
					//flattenedText += product.getLows() + " ";
					//flattenedText += product.getDescription() + " ";
					//flattenedText += product.getReview() + " ";
					//flattenedText += product.getSummary() + " ";
					//flattenedText += product.getBottomLine() + " ";
				}
				Document doc = new Document();
				doc.add(new TextField(CATEGORY_ID, category.getId(), Field.Store.YES));
				doc.add(new TextField(CATEGORY_NAME, category.getName(), Field.Store.YES));
				doc.add(new TextField(FLATTENED_TEXT, flattenedText, Field.Store.YES));	
				writer.addDocument(doc);
			}
			writer.close();
		} catch (IOException e)
		{
			
		}
	}
	
	/**
	 * Query the Lucene directory for matches to the query string.
	 * @param queryString the search string
	 */
	public List<Category> queryCategories(String queryString) 
	{
		List<Category> equivalenceClassResults = new ArrayList<Category>();
		try 
		{
			//Query query = new QueryParser(VERSION, CATEGORY_NAME, analyzer).parse(queryString);
			Query query = new FuzzyQuery(new Term(CATEGORY_NAME, queryString), 1);
			DirectoryReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector results = TopScoreDocCollector.create(MAX_NUM_RESULTS, true);
			
			searcher.search(query, results);
			ScoreDoc[] hits = results.topDocs().scoreDocs;
			LOG.info("Found " + hits.length + " results for \"" + queryString + "\"");	

			for(int i = 0; i<hits.length; i++) 
			{
			    Document doc = searcher.doc(hits[i].doc);
			    LOG.info(hits[i].score + " - " + categoryList.get(doc.get(CATEGORY_ID)).getName());
			    equivalenceClassResults.add(categoryList.get(doc.get(CATEGORY_ID)));
			}
		}
		catch (IOException e) 
		{
			LOG.error(e.getMessage());
		}
		catch (Exception e)
		{
			
		}
		return equivalenceClassResults;
	}
}
