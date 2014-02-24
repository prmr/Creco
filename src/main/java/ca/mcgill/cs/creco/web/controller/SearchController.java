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
package ca.mcgill.cs.creco.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;
import ca.mcgill.cs.creco.server.RankedFeaturesProducts;
import ca.mcgill.cs.creco.server.SearchService;
import ca.mcgill.cs.creco.web.model.EqcListVO;
import ca.mcgill.cs.creco.web.model.EqcVO;
import ca.mcgill.cs.creco.web.model.MainQueryVO;
import ca.mcgill.cs.creco.web.model.ProductListVO;
import ca.mcgill.cs.creco.web.model.ProductVO;


@Controller
@RequestMapping("/")
public class SearchController
{
	private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);
	private SearchService searchService;
	
	@Autowired
	private ProductListVO productList;
	
	@Autowired
	private EqcListVO eqcList;
	
	@Autowired
	private MainQueryVO mainQuery;

	@ModelAttribute("mainQuery")
	private MainQueryVO getMainQuery() {
		return new MainQueryVO();
	}

	@ModelAttribute("eqcList")
	private EqcListVO getEqcList() {
		return eqcList;
	}
	
	@ModelAttribute("productList")
	private ProductListVO getProductList() {
		return productList;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String init(Model model){
		try
		{
			searchService = new SearchService();
			System.out.println("INIT");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return "/index";
	}
	
	@RequestMapping(value = "/searchEqClass", method = RequestMethod.POST)
	public String searchEqClass(@ModelAttribute("mainQuery") MainQueryVO pMainQuery, BindingResult result, RedirectAttributes redirectAttrs) {
		mainQuery = pMainQuery;
		List<Category> categoryList = searchService.searchCategories(mainQuery.getQuery());		
		
		//Converting
		ArrayList<EqcVO> eqcs = new ArrayList<EqcVO>();		
		for (Category cat: categoryList) {
			EqcVO eqc = new EqcVO();
			eqc.setId(cat.getId());
			eqc.setName(cat.getName());
			eqc.setCount(cat.getCount());
			eqcs.add(eqc);
		}
		eqcList.setEqcs(eqcs);
		return "/eqclass";
	}
			
	@RequestMapping(value = "/searchRankedFeaturesProducts", method = RequestMethod.POST)  
	public String searchRankedFeaturesProducts(@ModelAttribute("eqc") EqcVO eqc) {  
	    List<Category> categoryList = searchService.searchCategories(mainQuery.getQuery());
	    Category target = null;
	    for (Category cat: categoryList) {
	    	if (cat.getId().equals(eqc.getId())) {
	    		target = cat;
	    	}
	    }
	    RankedFeaturesProducts rankedProducts = searchService.getRankedFeaturesProducts(target, mainQuery.getQuery());
	    List<ScoredProduct> scoredProducts = rankedProducts.getaProductSearchResult();

	    // Converting
		ArrayList<ProductVO> products = new ArrayList<ProductVO>();		
	    for (ScoredProduct sp: scoredProducts) {
			ProductVO p = new ProductVO();
			p.setName(sp.getProduct().getName());
			p.setId(sp.getProduct().getId());
			products.add(p);
	    }
		productList.setProducts(products);	
		return "/rankedproducts";
	}  
		
}
