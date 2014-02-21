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
import java.util.Hashtable;
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

import ca.mcgill.cs.creco.data.CRData;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.CategoryList;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.ProductList;
import ca.mcgill.cs.creco.web.model.ProductVO;

/**
 * Searches a list of products with Lucene indexes.
 */
public class ProductSearch {
	public static final String NAME = "name";
	public static final String ID = "id";

	private static final Version VERSION = Version.LUCENE_46;
	private static final int MAX_NUM_RESULTS = 20;
	private static final Logger LOG = LoggerFactory
			.getLogger(ProductSearch.class);

	private final Directory directory;
	private final Analyzer analyzer;

	private CategoryList categoryList;
	private ProductList productList;

	private List<String> allProductsOfUserEq = new ArrayList<String>();
	

	/**
	 * Constructor.
	 * 
	 * @throws IOException
	 */
	public ProductSearch() throws IOException {
		directory = new RAMDirectory();
		analyzer = new EnglishAnalyzer(VERSION);
		CRData crData = CRData.getData();

		categoryList = crData.getCategoryList();
		productList = crData.getProductList();
		buildProductIndexByCategory(categoryList);
	}

	private void buildProductIndexByCategory(CategoryList categoryList) {
		try {

			Analyzer analyzer = new EnglishAnalyzer(VERSION);
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(VERSION, analyzer));

			for (Category category : categoryList.getEqClasses()) {

				for (Product product : category.getProducts()) {

					LOG.debug("Adding category : " + category.getName()+" id : "+category.getId()+" product : "+product.getName());
					Document doc = new Document();
					doc.add(new TextField(ID, product.getId(), Field.Store.YES));
					doc.add(new TextField(NAME, product.getName(),
							Field.Store.YES));
					writer.addDocument(doc);

				}
			}
			writer.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}

	}

	public void query(String queryString, Category eqClass) 
	{
		ProductList scoredResults = new ProductList();
	
			
		System.out.println(eqClass.getName());
		for (Product product : eqClass.getProducts())
			{
				allProductsOfUserEq.add(product.getId());
			}
		
			
			
		
		
		try 
		{
			Query query = new QueryParser(VERSION, NAME, analyzer).parse(queryString);
			DirectoryReader reader = DirectoryReader.open(directory);
			
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector results = TopScoreDocCollector.create(MAX_NUM_RESULTS, true);
			
			searcher.search(query, results);
			ScoreDoc[] hits = results.topDocs().scoreDocs;
			
			
			for(String productsOfUserEq :allProductsOfUserEq){
			for(int i=0; i<hits.length; i++){
				
			    Document doc = searcher.doc(hits[i].doc);
				  
				if(doc.get(ID) == productsOfUserEq){
					 LOG.info((i+1)+hits[i].score+ ". " + doc.get(NAME));
					 System.out.println((i+1)+hits[i].score+ ". " + doc.get(NAME));
				}
				
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
//		SearchResult searchResult = new SearchResult(scoredResults,category);
//		return searchResult;
	}

	public static void main(String[] args) throws IOException {

		ProductSearch p = new ProductSearch();
		
		
		Category c = p.categoryList.get("28693");
		
		p.query("Grand Tech", c);
		System.out.println("done");
	}

}
