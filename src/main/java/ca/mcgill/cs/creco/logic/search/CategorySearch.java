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

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Category2;
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
	
	private final Directory aDirectory;
	private final Analyzer aAnalyzer;
	
	private IDataStore aDataStore;
	
	/**
	 * Constructor.
	 * @param pDataStore The database whose categories will be in the search index. 
	 * @throws IOException If an exception is thrown during the creation of the product index.
	 */
	@Autowired
	public CategorySearch(IDataStore pDataStore) throws IOException
	{
		aDirectory = new RAMDirectory();
		aAnalyzer = new EnglishAnalyzer(VERSION);
		aDataStore = pDataStore;

		buildCategoryIndex();
	}
	
	private void buildCategoryIndex() throws IOException
	{
		IndexWriter writer = new IndexWriter(aDirectory, new IndexWriterConfig(VERSION, aAnalyzer));
		for (Category2 category : aDataStore.getCategories()) 
		{
			String flattenedText = category.getName();
			for (Product product : category.getProducts())
			{
				flattenedText += product.getName() + " ";
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
	public List<Category2> queryCategories(String pQueryString) 
	{
		List<Category2> equivalenceClassResults = new ArrayList<Category2>();
		try 
		{
			DirectoryReader reader = DirectoryReader.open(aDirectory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector results = TopScoreDocCollector.create(MAX_NUM_RESULTS, true);
			
			// Search category names
			Query categoryNameQuery = new QueryParser(VERSION, CATEGORY_NAME, aAnalyzer).parse(pQueryString);
			searcher.search(categoryNameQuery, results);
			
			// Search flattened text (only product names for now)
			Query flattenedTextQuery = new QueryParser(VERSION, FLATTENED_TEXT, aAnalyzer).parse(pQueryString);
			searcher.search(flattenedTextQuery, results);

			for(ScoreDoc scoredResult : results.topDocs().scoreDocs) 
			{
			    Document doc = searcher.doc(scoredResult.doc);
			    Category2 resultCategory = aDataStore.getCategory2(doc.get(CATEGORY_ID));

			    if (!equivalenceClassResults.contains(resultCategory))
			    {
			    	equivalenceClassResults.add(resultCategory);
			    }
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
