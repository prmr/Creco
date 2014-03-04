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
import java.util.List;

import javax.annotation.PostConstruct;

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

import ca.mcgill.cs.creco.data.CRData;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;


/**
 * Searches a list of categories with Lucene indexes.
 */
@Component
public class CategorySearch implements ICategorySearch
{
	public static final String CATEGORY_ID = "ID";
	public static final String CATEGORY_NAME = "NAME";
	public static final String FLATTENED_TEXT = "FLATTENED_TEXT";
	
	private static final int MAX_NUM_RESULTS = 10;
	private static final Version VERSION = Version.LUCENE_46;
	private static final Logger LOG = LoggerFactory.getLogger(CategorySearch.class);
	
	private final Directory directory;
	private final Analyzer analyzer;
	
	@Autowired
	private IDataStore aData;
	
	/**
	 * Constructor.
	 */
	public CategorySearch() throws IOException
	{
		directory = new RAMDirectory();
		analyzer = new EnglishAnalyzer(VERSION);
	}
	
	@PostConstruct
	private void buildCategoryIndex() throws IOException
	{
		Analyzer analyzer = new EnglishAnalyzer(VERSION);
		IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(VERSION, analyzer));
		for (Category category : aData.getEquivalenceClasses()) 
		{
			String flattenedText = category.getName();
			LOG.debug("Adding " + category.getName() +", ID: " + category.getId());
			for (Product product : category.getProducts())
			{
				flattenedText += product.getName() + " ";
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
	}
	
	@Override
	public List<Category> queryCategories(String queryString) 
	{
		List<Category> equivalenceClassResults = new ArrayList<Category>();
		try 
		{
			Query query = new QueryParser(VERSION, CATEGORY_NAME, analyzer).parse(queryString);
			//Query query = new FuzzyQuery(new Term(FLATTENED_TEXT, queryString), 1);
			DirectoryReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector results = TopScoreDocCollector.create(MAX_NUM_RESULTS, true);
			
			searcher.search(query, results);
			ScoreDoc[] hits = results.topDocs().scoreDocs;
			
			// If no category name matches are found, search in product names
			if (hits.length == 0)
			{
				Query broaderQuery = new QueryParser(VERSION, FLATTENED_TEXT, analyzer).parse(queryString);
				searcher.search(broaderQuery, results);
				hits = results.topDocs().scoreDocs;
			}
			
			LOG.info("Found " + hits.length + " results for \"" + queryString + "\"");	

			for(int i = 0; i<hits.length; i++) 
			{
			    Document doc = searcher.doc(hits[i].doc);
			    LOG.info(hits[i].score + " - " + CRData.getData().getCategory(doc.get(CATEGORY_ID)).getName());
			    equivalenceClassResults.add(CRData.getData().getCategory(doc.get(CATEGORY_ID)));
			}
		}
		catch (IOException e) 
		{
			LOG.error(e.getMessage());
		}
		catch (ParseException e)
		{
			LOG.error(e.getMessage());
		}
		return equivalenceClassResults;
	}
}
