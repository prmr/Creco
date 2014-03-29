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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;
import ca.mcgill.cs.creco.logic.AttributeExtractor;
import ca.mcgill.cs.creco.logic.RankedFeaturesProducts;
import ca.mcgill.cs.creco.logic.ScoredAttribute;
import ca.mcgill.cs.creco.logic.ServiceFacade;
import ca.mcgill.cs.creco.logic.search.IProductSearch;
import ca.mcgill.cs.creco.web.model.FeatureListVO;
import ca.mcgill.cs.creco.web.model.FeatureVO;
import ca.mcgill.cs.creco.web.model.ProductListView;
import ca.mcgill.cs.creco.web.model.ProductView;
import ca.mcgill.cs.creco.web.model.UserFeatureModel;

import com.google.gson.Gson;

/**
 * Currently this is the only controller for the entire web application. 
 */
@Controller
public class SiteController
{ 
	private static final Logger LOG = LoggerFactory.getLogger(SiteController.class);
	
	private static final int NUMBER_OF_FEATURES_TO_DISPLAY = 10;
	
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
	
	private List<ScoredAttribute> aScoredAttr; 
	
	private Category aCategory;
	
	@Autowired
	private ProductListView aProductList;
	
	@Autowired
	private FeatureListVO aSpecFeatureList;

	@Autowired
	private IProductSearch aProductSort;
	
	// ***** Model Attributes *****
	
	@ModelAttribute("productList")
	private ProductListView getProductList() 
	{
		return aProductList;
	}
	
	@ModelAttribute("specFeatureList")
	private FeatureListVO getSpecFeatureList() 
	{
		return aSpecFeatureList;
	}
	
	// ***** URL Mappings *****
	
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
		 aSpecFeatureList = new FeatureListVO();
		 aProductList = new ProductListView();
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

		aCategory = aServiceFacade.getCategory(pCategoryId);
		List<Product> prodSearch = aProductSort.returnProductsAlphabetically(pCategoryId);
        List<ScoredAttribute> attrList = aAttributeExtractor.getAttributesForCategory(aCategory.getId());

		RankedFeaturesProducts rankedProducts = new RankedFeaturesProducts(attrList, prodSearch);
		List<Product> aScoredProducts = rankedProducts.getaProductSearchResult();
	    
	    aScoredAttr = rankedProducts.getaAttrList();
	    	   
	    // Converting
		ArrayList<ProductView> products = new ArrayList<ProductView>();		
	    for (Product scoredProduct: aScoredProducts) 
	    {
			products.add(new ProductView(scoredProduct.getId(), scoredProduct.getName(), scoredProduct.getUrl()));
	    }
		aProductList.setProducts(products);	
		
		updateCurrentFeatureList();
		
		return URL_SHOW_PRODUCTS;
	}
	
	private void updateCurrentFeatureList()
	{		
		ArrayList<FeatureVO> specFeatures = new ArrayList<FeatureVO>();	
		List<String> values;

		//Display top 10 scored attributes
		for (int i = 0 ; i < aScoredAttr.size() ; i++)
		{

			if(i > NUMBER_OF_FEATURES_TO_DISPLAY)
			{
				break;
			}

			values = new ArrayList<String>();
			FeatureVO f = new FeatureVO();
			f.setId(aScoredAttr.get(i).getAttributeID());
			f.setName(aScoredAttr.get(i).getAttributeName());
			f.setSpec(true);
			f.setVisible(true);


			f.setDesc(aScoredAttr.get(i).getAttributeDesc());
			TypedValue val = aScoredAttr.get(i).getAttributeDefault();		


			if( val.isBoolean() )
			{	
				f.setType("Bool");								
				values.add(val.getBoolean()+"");
				f.setValue((ArrayList<String>) values);
			}
			else if( val.isNumeric() )
			{				
				f.setType("Numeric");
				f.setMinValue(aScoredAttr.get(i).getMin().getNumeric());
				f.setMaxValue(aScoredAttr.get(i).getMax().getNumeric());										
				values.add(val.getNumeric()+"");
				f.setValue((ArrayList<String>)values);	
			}
			else if( val.isString() || val.isNA() )
			{				
				f.setType("Nominal");
				if(val.isNA())
				{
					values.add("N/A");
				}
				else
				{
					//comment to change possibly
					List<TypedValue> tvs = aScoredAttr.get(i).getDict();	
					for(TypedValue tv :tvs)
					{
						values.add(tv.getString());
					}					
				}
				f.setValue((ArrayList<String>)values);										
			}
			specFeatures.add(f);	
		}		
		aSpecFeatureList.setFeatures(specFeatures);	
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
		UserFeatureModel userFMSpec = new Gson().fromJson(dataSpec, UserFeatureModel.class);

		List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();
			
		for(int i = 0 ; i < userFMSpec.getNames().size() ; i++)
		{
			String tempName = userFMSpec.getNames().get(i);
			ScoredAttribute sa = locateFeatureScoredAttribute(aScoredAttr, tempName);
			if ( sa != null)
			{
				userScoredFeaturesSpecs.add(sa);				
			}			

		}
		
		RankedFeaturesProducts tempProducts = new RankedFeaturesProducts();	
		userScoredFeaturesSpecs = sortFeatures(userScoredFeaturesSpecs);
		
		List<Product> productsToDisplay  = tempProducts.FeatureSensitiveRanking(userScoredFeaturesSpecs, aCategory);

		// Converting to View Object
		ArrayList<ProductView> products = new ArrayList<ProductView>();
	    for (Product scoredProduct: productsToDisplay)
	    {
			products.add(new ProductView(scoredProduct.getId(), scoredProduct.getName(), scoredProduct.getUrl()));
		 }
	    if (productsToDisplay.size() > 0) 
	    {
			aProductList.setProducts(products);	
	    }

	    // This response is to be process by AJAX in JavaScript
	    String response = "";
	    for (ProductView productView : aProductList.getProducts()) 
	    {
	    	response = response.concat(productView.getId() + ",");
	    }
	    
		return response;		
	}	

	
	/**
	 * @author MariamN
	 * Sort user selected features based on Entropy
	 * @param pUserFeatures user selected features
	 * @return list of sorted features
	 */
	public List<ScoredAttribute> sortFeatures(List<ScoredAttribute> pUserFeatures)
	{
		ScoredAttribute tmp = null;
		
		for(int i = 0; i<pUserFeatures.size(); i++)
		{
			for(int j = pUserFeatures.size()-1; j >= i+1; j--)
			{				
				if(pUserFeatures.get(j).getEntropy() > pUserFeatures.get(j-1).getEntropy())
				{
					tmp = pUserFeatures.get(j);			       
					pUserFeatures.set(j, pUserFeatures.get(j-1));
					pUserFeatures.set(j-1, tmp);
				}
			}
		}
		return pUserFeatures;		
	}
	/**
	 * @author MariamN
	 * @param pFeatureList : feature list, either specs or ratings
	 * @param pName   : Name of feature to locate
	 * @return ScoredAttribute matched object
	 */
	public ScoredAttribute locateFeatureScoredAttribute(List<ScoredAttribute> pFeatureList, String pName)
	{
		for (int i = 0 ; i< pFeatureList.size() ; i++)
		{
			ScoredAttribute temp = pFeatureList.get(i);
			if(temp.getAttributeName().equals(pName))
			{
				return temp;

			}
		}
		return null;
	}
}
