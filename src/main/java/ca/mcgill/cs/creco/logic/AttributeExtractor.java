/*
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
package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;

 /**
 * This class handles the extraction of most relevant attribute from a category.
 */
public class AttributeExtractor
{
	/** Sorting methods for attributes. */
	public static enum SORT_METHOD
	{ ENTROPY, SCORE, CORRELATION }
	
	private static final Logger LOG = LoggerFactory.getLogger(AttributeExtractor.class);
	
	private Category aCategory;
	private ArrayList<ScoredAttribute> aScoredAttributeList;
	private SORT_METHOD aSortMethod;
	
	/** Constructor that takes a category.
	 * @param pCategory the whole space of interesting products
	 */
	public AttributeExtractor(Category pCategory)
	{
		aCategory = pCategory;
		aSortMethod = SORT_METHOD.ENTROPY;
		aScoredAttributeList = new ArrayList<ScoredAttribute>();
		
		HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();
		
		for(Product p : pCategory.getProducts())
		{
			for(Attribute a : p.getAttributes())
			{
				attributes.put(a.getId(), a);
			}
		}
		for(String key : attributes.keySet())
		{
			aScoredAttributeList.add(new ScoredAttribute(attributes.get(key), pCategory));
		}
		sort();
		
	}
	
	/**Constructor that takes a ProductSearchResult and an equivalence class.
	 * @param pProductSearchResult a lucene result
	 * @param pCategory the whole space of interesting products
	 */
	@Deprecated
	public AttributeExtractor(List<Product> pProductSearchResult, Category pCategory)
	{
		this(pCategory);
	}
	/**Constructor that takes a product list and an equivalence class.
	 * @param pProductList subset of interesting products
	 * @param pCategory the whole space of interesting products
	 */
	@Deprecated
	public AttributeExtractor(Set<Product> pProductList, Category pCategory)
	{
		this(pCategory);
	}	

	/**
	 * @return The equivalence class used by the extractor
	 */
	public Category getCategory() 
	{
		return aCategory;
	}


	/**
	 * Use getScoredAttributeLList() instead.
	 * @see getScoredAttributeList()
	 * @return ScoredAttribute List
	 */
	@Deprecated
	public ArrayList<ScoredAttribute> getScoredSpecList() 
	{
		return getScoredAttributeList();
	}
	
	/**
	 * Use getScoredAttributeLList() instead.
	 * @see getScoredAttributeList()
	 * @return ScoredAttribute List
	 */
	@Deprecated
	public ArrayList<ScoredAttribute> getScoredRatingList() 
	{
		return getScoredAttributeList();
	}
	
	/**
	 * Call this method to get the list of scored Attributes ranked from most important
	 * to least important by entropy.
	 * can return null pointers if it doesn't have any attributes to work with.
	 * @return list of scored attributes ranked from most important
	 * to least important (by entropy).
	 */
	public ArrayList<ScoredAttribute> getScoredAttributeList() 
	{
		sort();
		return aScoredAttributeList;
	}
	
	public void setSortMethod(SORT_METHOD pSortMethod)
	{
		aSortMethod = pSortMethod;
	}
	
	public void sort()
	{
		if (aSortMethod == SORT_METHOD.CORRELATION)
		{
			Collections.sort(aScoredAttributeList, ScoredAttribute.SORT_BY_CORRELATION);

		}
		else if (aSortMethod == SORT_METHOD.SCORE)
		{
			Collections.sort(aScoredAttributeList, ScoredAttribute.SORT_BY_SCORE);
		}
		else
		{
			Collections.sort(aScoredAttributeList, ScoredAttribute.SORT_BY_ENTROPY);
		}
		
	}

}
