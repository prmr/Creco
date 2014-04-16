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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.mcgill.cs.creco.logic.ServiceFacade;
import ca.mcgill.cs.creco.web.model.UserFeatureModel;

/**
 * Currently this is the only controller for the entire web application. 
 */
@Controller
public class SiteController
{ 
	private static final String URL_HOME = "/";
	private static final String URL_AUTOCOMPLETE = "/autocomplete";
	private static final String URL_SEARCH_CATEGORIES = "/searchCategories";
	private static final String URL_SHOW_CATEGORIES = "/categories";
	private static final String URL_SEARCH_PRODUCTS = "/searchProducts";
	private static final String URL_SHOW_PRODUCTS = "/products";
	private static final String URL_UPDATE_FEATURES = "/sendFeatures";
	
	@Autowired
	private ServiceFacade aServiceFacade;

	
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
	public String searchRankedFeaturesProductsPOST(@RequestParam(value = "id", required = true) String pCategoryId, Model pModel)
	{  	
		pModel.addAttribute("productList", aServiceFacade.searchRankedFeaturesProducts(pCategoryId, pModel));			
		pModel.addAttribute("specFeatureList", aServiceFacade.createFeatureList(pCategoryId));
		pModel.addAttribute("currentCategoryId", pCategoryId);
		return URL_SHOW_PRODUCTS;
	}
	
	/**
	 * @author MariamN
	 * @param pUserFeatureList list of features selected by the user.
	 * @param pCategoryId id of the category the user searched.
	 * @return response contains list of products and explanation matching the user selected features.
	 */
	@RequestMapping(URL_UPDATE_FEATURES)	
	@ResponseBody
	public String sendCurrentFeatureList(@RequestParam String pUserFeatureList, @RequestParam String pCategoryId)
	{
		
		String response = aServiceFacade.sendCurrentFeatureList(pUserFeatureList, pCategoryId);	 
		

		String response_all = aServiceFacade.sendCurrentFeatureList(aServiceFacade.createJSONforallattributes(pCategoryId).toJSONString(), pCategoryId);

		
		return response.concat("|||").concat(response_all);		
	}		
}
