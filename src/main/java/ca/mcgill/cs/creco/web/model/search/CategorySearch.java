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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Searches a list of categories with Lucene indexes.
 */
public class CategorySearch 
{
	public static final String NAME = "name";
	public static final String ID = "id";
	
	private static final Version VERSION = Version.LUCENE_46;
	private static final Logger LOG = LoggerFactory.getLogger(ProductSearch.class);
	
	private final Directory aDirectory;
	private final Analyzer aAnalyzer;

	/**
	 * Constructor.
	 */
	public CategorySearch() 
	{
		aDirectory = new RAMDirectory();
		aAnalyzer = new EnglishAnalyzer(VERSION);
	}
	
	/**
	 * Add equivalence classes into the Lucene directory.
	 */
	public void addCategories() 
	{
		// This should take a list of categories and add all "equivalence classes" into documents.
	}
	
	/**
	 * Query the Lucene directory for matches to the query string.
	 * @param pQueryString the search string
	 */
	public void queryCategories(String pQueryString) 
	{
		// This should return the best match in the equivalence classes.
	}
}
