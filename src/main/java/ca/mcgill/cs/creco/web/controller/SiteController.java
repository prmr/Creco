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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.mcgill.cs.creco.logic.AttributeExtractor;
import ca.mcgill.cs.creco.logic.ServiceFacade;
import ca.mcgill.cs.creco.web.model.UserFeatureModel;

/**
 * Currently this is the only controller for the entire web application. 
 */
@Controller
public class SiteController
{ 
	private static final Logger LOG = LoggerFactory.getLogger(SiteController.class);
		
	private static final String URL_HOME = "/";
	private static final String URL_AUTOCOMPLETE = "/autocomplete";
	private static final String URL_SEARCH_CATEGORIES = "/searchCategories";
	private static final String URL_SHOW_CATEGORIES = "/categories";
	private static final String URL_SEARCH_PRODUCTS = "/searchProducts";
	private static final String URL_SHOW_PRODUCTS = "/products";
	private static final String URL_UPDATE_FEATURES = "/sendFeatures";
	
	@Autowired
	private ServiceFacade aServiceFacade;
	@Autowired
	private AttributeExtractor aAttributeExtractor;

	
	/**
	 * Loads the user model and redirects the browser to the index 
	 * page.
	 * @param pModel The automatically-inserted model.
	 * @return The relative path to the index page.
	 */
	@RequestMapping(value = URL_HOME, method = RequestMethod.GET)
	public String init(Model pModel)
	{
		 UserFeatureModel form = new UserFeatureModel();
		 pModel.addAttribute("myForm", form);
		 return URL_SHOW_CATEGORIES;								
	}
	
	/**
	 * Returns a response body with results for the search auto-complete
	 * box.
	 * 
	 * @param pInput The string typed by the user.
	 * @return The response body containing the completions.
	 */
	@RequestMapping(URL_AUTOCOMPLETE)  
	@ResponseBody
	public String getCompletions(@RequestParam(value = "input", required = true) String pInput)
	{  
		return aServiceFacade.getCompletions(pInput);
	}  
	   
	
	/**
	 * URL to search for categories from a query text.
	 * @param pSearchQuery The search query.
	 * @param pModel The model, containing the list of categories.
	 * @return A redirection to the url to show categories.
	 */
	@RequestMapping(URL_SEARCH_CATEGORIES)
	public String searchCategories(@RequestParam(value = "query", required = true) String pSearchQuery, Model pModel) 
	{
		pModel.addAttribute("categories", aServiceFacade.searchCategories(pSearchQuery));
		pModel.addAttribute("query", pSearchQuery);
		return URL_SHOW_CATEGORIES;
	}
	
	/**
	 * A category is selected and this controller obtains the features
	 * and products to display.		
	 * @param pCategoryId The id of the selected category
	 * @param pModel The model, containing the list of categories.
	 * @return A redirection to the product page
	 */
	@RequestMapping(URL_SEARCH_PRODUCTS)  
	public String searchRankedFeaturesProducts_POST(@RequestParam(value = "id", required = true) String pCategoryId, Model pModel)
	{  	
		pModel.addAttribute("productList", aServiceFacade.searchRankedFeaturesProducts_POST(pCategoryId, pModel));			
		pModel.addAttribute("specFeatureList", aServiceFacade.updateCurrentFeatureList());
		return URL_SHOW_PRODUCTS;
	}
	
	//TODO clean this method up doesn't need to default value anymore
	/**
	 * @author MariamN
	 * @param dataSpec
	 * @param dataRate
	 * @return name of file to redirect the browser to the products page.
	 */
	@RequestMapping(URL_UPDATE_FEATURES)	
	@ResponseBody
	public String sendCurrentFeatureList(@RequestParam String dataSpec)
	{
		
	    	String response = aServiceFacade.sendCurrentFeatureList(dataSpec);	    
		return response;		
	}		
}
