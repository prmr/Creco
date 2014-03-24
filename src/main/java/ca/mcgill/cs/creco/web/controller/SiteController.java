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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;
//import ca.mcgill.cs.creco.data.Product;
//import ca.mcgill.cs.creco.data.TypedValue;
//import ca.mcgill.cs.creco.data.TypedValue.Type;
import ca.mcgill.cs.creco.logic.AttributeExtractor;
import ca.mcgill.cs.creco.logic.RankedFeaturesProducts;
import ca.mcgill.cs.creco.logic.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.ICategorySearch;
import ca.mcgill.cs.creco.logic.search.IProductSearch;
import ca.mcgill.cs.creco.web.model.EqcListVO;
import ca.mcgill.cs.creco.web.model.EqcVO;
import ca.mcgill.cs.creco.web.model.FeatureListVO;
import ca.mcgill.cs.creco.web.model.FeatureVO;
import ca.mcgill.cs.creco.web.model.MainQueryVO;
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
	private static final int MIN_NUMBER_OF_TYPED_LETTERS = 3;

	private static final Logger LOG = LoggerFactory.getLogger(SiteController.class);
	
	private static final String URL_HOME = "/";
	private static final String URL_INDEX = "/index";
	private static final String URL_AUTOCOMPLETE = "/autocomplete";
	
	private List<ScoredAttribute> aScoredAttr; 
	
	private List<Product> aScoredProducts;
	private Category aCategory;
	
	@Autowired
	private ProductListView aProductList;
	
	@Autowired
	private EqcListVO aEqcList;
	
	@Autowired
	private MainQueryVO aMainQuery;

	@Autowired
	private FeatureListVO aUserList;

	@Autowired
	private FeatureListVO aSpecFeatureList;

	@Autowired
	private FeatureListVO aRateFeatureList;

	@Autowired
	private FeatureListVO aFeatureList;

	@Autowired
	private FeatureVO aUserSpecFeatures;
	
	@Autowired
	private IDataStore aDataStore;
	
	@Autowired
	private ICategorySearch aCategorySearch;
	
	@Autowired
	private IProductSearch aProductSort;
	
	// ***** Model Attributes *****
	
	@ModelAttribute("mainQuery")
	private MainQueryVO getMainQuery() 
	{
		return new MainQueryVO();
	}
	
	@ModelAttribute("userList")
	private FeatureListVO getUserList() 
	{
		return new FeatureListVO();
	}

	@ModelAttribute("eqcList")
	private EqcListVO getEqcList() 
	{
		return aEqcList;
	}
	
	@ModelAttribute("productList")
	private ProductListView getProductList() 
	{
		return aProductList;
	}
	@ModelAttribute("featureList")
	private FeatureListVO getFeatureList() 
	{
		return aFeatureList;
	}
	
	@ModelAttribute("specFeatureList")
	private FeatureListVO getSpecFeatureList() 
	{
		return aSpecFeatureList;
	}
	
	@ModelAttribute("userSpecFeatures")
	private FeatureVO getUserSpecFeatures()
	{
		return aUserSpecFeatures;
	}
	
	@ModelAttribute("rateFeatureList")
	private FeatureListVO getRateFeatureList()
	{
		return aRateFeatureList;
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
		 aRateFeatureList = new FeatureListVO();
		 aProductList = new ProductListView();
		 return URL_INDEX;								
	}
	
	/**
	 * Returns a response body with results for the search autocomplete
	 * box.
	 * 
	 * @param pStringTyped The string typed by the user.
	 * @return The response body containing the completions.
	 */
	@RequestMapping(value = URL_AUTOCOMPLETE, method = RequestMethod.POST )  
	@ResponseBody  
	public String createSmartphone(@RequestBody String pStringTyped)
	{  
		if(pStringTyped.length() < MIN_NUMBER_OF_TYPED_LETTERS)
		{
			return "";
		}

		String response = "";

		for(Category category : aDataStore.getCategories()) 
		{
			if(category.getNumberOfProducts() == 0)
			{
				continue;
			}
			if(category.getName().toLowerCase().contains(pStringTyped.toLowerCase()))
			{
				response = response.concat(category.getName() + ",");
			}
		}
		
		for(Product product : aDataStore.getProducts()) 
		{
			if(product.getName().toLowerCase().contains(pStringTyped.toLowerCase()))
			{
				response = response.concat(product.getName()+ ",");
			}
		}

		return response;
	  }  
	   
	
	/**
	 * 
	 * @param pMainQuery
	 * @param result
	 * @param redirectAttrs
	 * @return string to redirect browser to eqclass.html
	 */
	@RequestMapping(value = "/searchEqClass", method = RequestMethod.POST)
	public String searchEqClass(@ModelAttribute("mainQuery") MainQueryVO pMainQuery, BindingResult result, RedirectAttributes redirectAttrs) 
	{
		aMainQuery = pMainQuery;
		List<Category> categoryList = aCategorySearch.queryCategories(aMainQuery.getQuery());	
				
		//Nishanth code 
		String mainString = "";
		for (Category category123 : aDataStore.getCategories()) 
		{
		   if(category123.getNumberOfProducts() == 0)
		   {
			   continue;
		   }
		 mainString = mainString.concat("\"");
		 mainString = mainString.concat(category123.getName());
		 mainString = mainString.concat("\"");
		 mainString = mainString.concat(",");
		 mainString = mainString.concat("\n");
		}

		LOG.debug(mainString);

		// Nishanth code
		//Converting
		ArrayList<EqcVO> eqcs = new ArrayList<EqcVO>();		
		for (Category cat: categoryList)
		{
			if(cat.getNumberOfProducts() == 0)
			{
				continue;
			}
			EqcVO eqc = new EqcVO();
			eqc.setId(cat.getId());
			eqc.setName(cat.getName());
			eqc.setCount(cat.getNumberOfProducts());
			eqcs.add(eqc);
		}
		aEqcList.setEqcs(eqcs);
		return "/eqclass";
	}
	/**
	 * 		
	 * @param eqc
	 * @return
	 */
	@RequestMapping(value = "/searchRankedFeaturesProducts", method = RequestMethod.POST)  
	public String searchRankedFeaturesProducts(@ModelAttribute("eqc") EqcVO eqc)
	{  
	    List<Category> categoryList = aCategorySearch.queryCategories(aMainQuery.getQuery());
	    Category target = null;
	    for (Category cat: categoryList)
	    {
	    	if (cat.getId().equals(eqc.getId()))
	    	{
	    		target = cat;
	    	}
	    }
	    
		List<Product> prodSearch = aProductSort.returnProductsAlphabetically(target.getId());
		AttributeExtractor ae = new AttributeExtractor(target);
		
		List<ScoredAttribute> attrList = ae.getScoredAttributeList();
		aCategory = ae.getCategory();
		RankedFeaturesProducts rankedProducts = new RankedFeaturesProducts(attrList, prodSearch);
	    aScoredProducts = rankedProducts.getaProductSearchResult();
	    
	    aScoredAttr = rankedProducts.getaAttrList();
	    	   
	    // Converting
		ArrayList<ProductView> products = new ArrayList<ProductView>();		
	    for (Product scoredProduct: aScoredProducts) 
	    {
			products.add(new ProductView(scoredProduct.getId(), scoredProduct.getName(), scoredProduct.getUrl()));
	    }
		aProductList.setProducts(products);	
		return getCurrentFeatureList();
	}
	/**
	 * @author MariamN
	 * @return string to redirect the browser to feature selection page
	 */
	public String getCurrentFeatureList()
	{		
		LOG.debug("Get Current Features");

		ArrayList<FeatureVO> specFeatures = new ArrayList<FeatureVO>();	
		List<String> values;

		//Display top 10 scored attributes
		int featureNumToDisplay = 10;
		for (int i = 0 ; i < aScoredAttr.size() ; i++)
		{

			if(i>featureNumToDisplay)
			{
				break;
			}

			values = new ArrayList<String>();
			FeatureVO f = new FeatureVO();
			f.setId(aScoredAttr.get(i).getAttributeID());
			f.setName(aScoredAttr.get(i).getAttributeName());
			f.setSpec(true);
			f.setRate(false);			
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

		return "/rankedproducts";
						
	}
	/**
	 * 
	 * @return name of file to redirect the browser to popupFeature.html
	 */
	@RequestMapping(value = "/popupFeature", method = RequestMethod.GET)
	public String getPopUp() 
	{
		return "/popupFeature";
	}
	
	/**
	 * 
	 * @return name of file to redirect the browser to rankedproducts.html
	 */
	@RequestMapping(value = "/searchRankedFeaturesProducts", method = RequestMethod.GET)
	public String getRankedProducts() 
	{
		return "/rankedproducts";
	}
	
	/**
	 * 
	 * @return name of file to redirect the browser to rankedproducts.html
	 */
	@RequestMapping(value = "/rankedproducts", method = RequestMethod.GET)
	public String getRankedProducts2() 
	{
		return "/rankedproducts";
	}
	
	/**
	 * @author MariamN
	 * @param dataSpec
	 * @param dataRate
	 * @return name of file to redirect the browser to rankedproducts.html
	 */
	@RequestMapping(value = "/sendFeatures", method = RequestMethod.POST)	
	public String sendCurrentFeatureList(@RequestParam String dataSpec, @RequestParam String dataRate)
	{

		LOG.debug(" spec data is  " + dataSpec);
		LOG.debug(" rate data is  " + dataRate);

		Gson gson = new Gson();
		UserFeatureModel userFMSpec = gson.fromJson(dataSpec, UserFeatureModel.class);

		List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();
			
		for(int i = 0 ; i < userFMSpec.getNames().size() ; i++)
		{
			String tempName = userFMSpec.getNames().get(i);
			ScoredAttribute sa = locateFeatureScoredAttribute(aScoredAttr, tempName);
			if ( sa != null)
			{
				TypedValue av = new TypedValue(userFMSpec.getValues().get(i));				
				sa.setAttributeDefault(av);
				userScoredFeaturesSpecs.add(sa);				
			}			

		}
		
		for(Product sa : aScoredProducts)
		{
			LOG.debug(sa.toString());					
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
	    if (productsToDisplay.size() > 0) {
			aProductList.setProducts(products);	
	    }
		return "/rankedproducts";		
	}	

	
	/**
	 * @author MariamN
	 * Sort user selected features based on Entropy
	 * @param pUserFeatures user selected features
	 * @return list of sorted features
	 */
	public List<ScoredAttribute> sortFeatures(List<ScoredAttribute> pUserFeatures)
	{
		int len = pUserFeatures.size();
		ScoredAttribute tmp = null;
		
		for(int i = 0; i<len; i++)
		{
			for(int j = (len-1); j >= (i+1); j--)
			{				
				if(pUserFeatures.get(j).getEntropy() > pUserFeatures.get(j-1).getEntropy())
				{
					tmp = pUserFeatures.get(j);			       
					pUserFeatures.set(j,pUserFeatures.get(j-1));
					pUserFeatures.set(j-1,tmp);
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
				LOG.debug("old mean value is " + pFeatureList.get(i).getAttributeDefault());
				return temp;

			}
		}
		return null;
	}
}
