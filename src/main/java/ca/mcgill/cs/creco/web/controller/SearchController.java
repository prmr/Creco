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

//import java.io.IOException;
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
import ca.mcgill.cs.creco.logic.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.ICategorySearch;
import ca.mcgill.cs.creco.logic.search.IProductSearch;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;
import ca.mcgill.cs.creco.util.RankedFeaturesProducts;
import ca.mcgill.cs.creco.web.model.EqcListVO;
import ca.mcgill.cs.creco.web.model.EqcVO;
import ca.mcgill.cs.creco.web.model.FeatureListVO;
import ca.mcgill.cs.creco.web.model.FeatureVO;
import ca.mcgill.cs.creco.web.model.MainQueryVO;
import ca.mcgill.cs.creco.web.model.ProductListVO;
import ca.mcgill.cs.creco.web.model.ProductVO;
import ca.mcgill.cs.creco.web.model.UserFeatureModel;

import com.google.gson.Gson;
//import ca.mcgill.cs.creco.data.Attribute;

@Controller
public class SearchController
{
	private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);
	
	
	private List<ScoredAttribute> aScoredRatings; 
	private List<ScoredAttribute> aScoredSpecs; 
	private List<ScoredProduct> aScoredProducts;
	private String aCategoryID;
	@Autowired
	private ProductListVO aProductList;
	
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
	private IProductSearch aProductSearch;
	
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
	private ProductListVO getProductList() 
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
	
	/**
	 * 
	 * @param pScoredRatings list of scored rating Attributes
	 */
	public void setScoredRatings(List<ScoredAttribute> pScoredRatings)
	{

		LOG.debug("in setscoredRatings " + pScoredRatings.toString());
		this.aScoredRatings = pScoredRatings;	

		
	}
	/**
	 * 
	 * @param pScoredSpecs list of scored spec Attributes
	 */
	public void setScoredSpecs(List<ScoredAttribute> pScoredSpecs)
	{

		LOG.debug("in setscoredSpecs"+ pScoredSpecs.toString());
		
		this.aScoredSpecs = pScoredSpecs;	
		
	}

	/**
	 * 
	 * @return list of scored spec Attributes
	 */
	public List<ScoredAttribute> getScoredSpecs()
	{
		return this.aScoredSpecs;
		
	}	
	
	/**
	 * 
	 * @return list of scored rating Attributes
	 */
	public List<ScoredAttribute> getScoredRatings()
	{
		return this.aScoredRatings;
		
	}	
	
	/**
	 * 
	 * @param pS0
	 * @param pS1
	 * @return the distance between strings
	 */
	// Levenshtein Distance used to calculate distance between two strings. 
	public int LevenshteinDistance (String pS0, String pS1)
	{
		int len0 = pS0.length()+1;
		int len1 = pS1.length()+1;
	 
		// the array of distances
		int[] cost = new int[len0];
		int[] newcost = new int[len0];
	 
		// initial cost of skipping prefix in String s0
		for(int i = 0; i<len0; i++) 
			{
				cost[i] = i;
			}
	 
		// dynamicaly computing the array of distances
	 
		// transformation cost for each letter in s1
		for(int j = 1; j<len1; j++) 
		{
	 
			// initial cost of skipping prefix in String s1
			newcost[0] = j-1;
	 
			// transformation cost for each letter in s0
			for(int i = 1; i<len0; i++)
			{
	 
				// matching current letters in both strings
				int match = (pS0.charAt(i-1)==pS1.charAt(j-1)) ? 0 : 1;
	 
				// computing cost for each transformation
				int costReplace = cost[i-1] + match;
				int costInsert  = cost[i] + 1 ;
				int costDelete  = newcost[i-1] + 1;
	 
				// keep minimum cost
				newcost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
			}
	 
			// swap cost/newcost arrays
			int[] swap = cost;
			cost = newcost;
			newcost = swap;
		}
	 
		// the distance is the cost for transforming all letters in both strings
		return cost[len0-1];
	}
	
	
	/**
	 * 
	 * @param abcd
	 * @return
	 */
	public String returnfournearest(String abcd)
	{
		 String ajaxReturn = new String();
		   int min1 = 200;
		   int min2 = 200;
		   int min3 = 200;
		   int min4 = 200;
		   Category min1S = null;
		   Category min2S = null;
		   Category min3S = null;
		   Category min4S = null;
		 
		   for (Category category123 : aDataStore.getCategories()) 
			{
			   if(category123.getNumberOfProducts() == 0)
			   {
				   continue; 
			   
			   }
			   int value;
			   if(category123.getName().toLowerCase().contains(abcd.toLowerCase()))
			   {
				   value = 0;
			   }
			   else
			   {
				   value = LevenshteinDistance(abcd.toLowerCase(), category123.getName().toLowerCase());
			   }

		  if(value<=min1)
		  {
			 min4 = min3;
			 min3 = min2;
			 min2 = min1;
			 min1 = value;
			 min4S = min3S;
			 min3S = min2S;
			 min2S = min1S;
			 min1S = category123;
		  }
		  else if(value<=min2)
		  {
			  	 min4 = min3;
				 min3 = min2;
				 min2 = value;
				 min4S = min3S;
				 min3S = min2S;
				 min2S = category123;
		  }
		  else if(value<=min3)
		  {
			  	min4 = min3;
				 min3 = value;
				 min4S = min3S;
				 min3S = category123;
		  }
		  else if(value<=min4)
		  {
			  min4 = value;
			  min4S = category123;
			}
			}
		   ajaxReturn = "";

		ajaxReturn = ajaxReturn.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
		ajaxReturn = ajaxReturn.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min1S.getName()+"></input>");
		   ajaxReturn = ajaxReturn.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min1S.getName()+"></input>");
		   ajaxReturn = ajaxReturn.concat("<input type=\"submit\" value=\""+min1S.getName()+"\"></input>") ;
		   ajaxReturn = ajaxReturn.concat("	<span class=\"badge\">"+min1S.getNumberOfProducts()+"</span>");
		   ajaxReturn = ajaxReturn.concat( "</form>");
		ajaxReturn = ajaxReturn.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
		ajaxReturn = ajaxReturn.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min2S.getName()+"></input>");
		   ajaxReturn = ajaxReturn.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min2S.getName()+"></input>");
		   ajaxReturn = ajaxReturn.concat("<input type=\"submit\" value=\""+min2S.getName()+"\"></input>") ;
		   ajaxReturn = ajaxReturn.concat("	<span class=\"badge\">"+min2S.getNumberOfProducts()+"</span>");
		   ajaxReturn = ajaxReturn.concat( "</form>");
		ajaxReturn = ajaxReturn.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
		ajaxReturn = ajaxReturn.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min3S.getName()+"></input>");
		   ajaxReturn = ajaxReturn.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min3S.getName()+"></input>");
		   ajaxReturn = ajaxReturn.concat("<input type=\"submit\" value=\""+min3S.getName()+"\"></input>") ;
		   ajaxReturn = ajaxReturn.concat("	<span class=\"badge\">"+min3S.getNumberOfProducts()+"</span>");
		   ajaxReturn = ajaxReturn.concat( "</form>");
		ajaxReturn = ajaxReturn.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
		ajaxReturn = ajaxReturn.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min4S.getName()+"></input>");
		   ajaxReturn = ajaxReturn.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min4S.getName()+"></input>");
		   ajaxReturn = ajaxReturn.concat("<input type=\"submit\" value=\""+min4S.getName()+"\"></input>") ;
		   ajaxReturn = ajaxReturn.concat("	<span class=\"badge\">"+min4S.getNumberOfProducts()+"</span>");
		   ajaxReturn = ajaxReturn.concat( "</form>");
		   return ajaxReturn;
	}
	
	
	/**
	 * 
	 * @param typedString
	 * @return 
	 */
	   @RequestMapping(value = "/ajax", method = RequestMethod.POST )  
 @ResponseBody  
 public String createSmartphone(@RequestBody String typedString)
	   {  
		
		   if(typedString.length()<3)
			   return "";
		   String return_this = new String("");

		   for (Category category123 : aDataStore.getCategories()) 
			{
			   if(category123.getNumberOfProducts() == 0)
				   continue;
			   if(category123.getName().toLowerCase().contains(typedString.toLowerCase()))
				   	return_this =return_this.concat(category123.getName()+",");

			}
		   for (Product category1234 : aDataStore.getProducts()) 
			{
			   if(category1234.getName().toLowerCase().contains(typedString.toLowerCase()))
				   	return_this =return_this.concat(category1234.getName()+",");

			}

		   return return_this;
 }  
	   /**
	    * 
	    * @param model
	    * @return string to redirect browser to index.html
	    */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String init(Model model)
	{
		 UserFeatureModel form = new UserFeatureModel();
		 model.addAttribute("myForm", form);
		 aSpecFeatureList = new FeatureListVO();
		 aRateFeatureList = new FeatureListVO();
		 aProductList = new ProductListVO();
		 return "/index";								
	}
	
	/**
	 * 
	 * @param model
	 * @return string to redirect browser to experiment.html
	 */
	@RequestMapping(value = "/experiment", method = RequestMethod.GET)
	public String init2(Model model)
	{
		 UserFeatureModel form = new UserFeatureModel();
		 model.addAttribute("myForm", form);

		return "/experiment";
	}
	/**
	 * 
	 * @param pMainQuery
	 * @param result
	 * @param redirectAttrs
	 * @return string to redirect browser to eqclass.html
	 */
	@RequestMapping(value = "/searchEqClass", method = RequestMethod.POST)
	public String searchEqClass(@ModelAttribute("mainQuery") MainQueryVO pMainQuery, BindingResult result, RedirectAttributes redirectAttrs) {
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
	    		aCategoryID = eqc.getId();
	    	}
	    }
	    
		List<ScoredProduct> prodSearch = aProductSearch.returnProductsAlphabetically(aMainQuery.getQuery(), target.getId());
		AttributeExtractor ae = new AttributeExtractor(prodSearch, target);
		List<ScoredAttribute> ratingList = ae.getScoredRatingList();
		List<ScoredAttribute> specList = ae.getScoredSpecList();
		
	    RankedFeaturesProducts rankedProducts = new RankedFeaturesProducts(ratingList, specList, prodSearch);
	    aScoredProducts = rankedProducts.getaProductSearchResult();
	    
	    aScoredRatings = rankedProducts.getaRatingList();
	    aScoredSpecs = rankedProducts.getaSpecList();
	    	   
	    // Converting
		ArrayList<ProductVO> products = new ArrayList<ProductVO>();		
	    for (ScoredProduct sp: aScoredProducts) 
	    {
			ProductVO p = new ProductVO();
			p.setName(sp.getProduct().getName());
			p.setId(sp.getProduct().getId());
			products.add(p);
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
		ArrayList<FeatureVO> rateFeatures = new ArrayList<FeatureVO>();
		List<String> values;

		//For now will get top 10 scores
		int featureNumToDisplay = 10;
		for (int i = 0 ; i < aScoredSpecs.size() ; i++)
		{

			if(i>featureNumToDisplay)
			{
				break;
			}

			values = new ArrayList<String>();
			FeatureVO f = new FeatureVO();
			f.setId(aScoredSpecs.get(i).getAttributeID());
			f.setName(aScoredSpecs.get(i).getAttributeName());
			f.setSpec(true);
			f.setRate(false);			
			f.setVisible(true);


			f.setDesc(aScoredSpecs.get(i).getAttributeDesc());
			TypedValue val = aScoredSpecs.get(i).getAttributeDefault();		


			if( val.isBoolean() )
			{	
				f.setType("Bool");								
				values.add(val.getBoolean()+"");
				f.setValue((ArrayList<String>) values);
			}
			else if( val.isNumeric() )
			{				
				f.setType("Numeric");
				f.setMinValue(aScoredSpecs.get(i).getMin().getNumeric());
				f.setMaxValue(aScoredSpecs.get(i).getMax().getNumeric());										
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
					List<TypedValue> tvs = aScoredSpecs.get(i).getDict();	
					for(TypedValue tv :tvs)
					{
						values.add(tv.getString());
					}					
				}
				f.setValue((ArrayList<String>)values);										
			}
			specFeatures.add(f);	
		}		

		for (int i = 0; i < aScoredRatings.size() ; i++)
		{
			if(i > featureNumToDisplay)
			{
				break;
			}
			values = new ArrayList <String>() ;
			FeatureVO f = new FeatureVO();

			f.setId(aScoredRatings.get(i).getAttributeID());
			f.setName(aScoredRatings.get(i).getAttributeName());

			f.setDesc(aScoredRatings.get(i).getAttributeDesc());
			f.setRate(true);
			f.setSpec(false);			
			f.setVisible(true);


			TypedValue val = aScoredRatings.get(i).getAttributeDefault();	

			if( val !=null && val.isBoolean() )
			{
				f.setType("Bool");								
				values.add(val.getBoolean()+"");
				f.setValue((ArrayList<String>)values);
			}
			else if( val.isNumeric() )
			{
				f.setType("Numeric");
				f.setMinValue(aScoredRatings.get(i).getMin().getNumeric());
				f.setMaxValue(aScoredRatings.get(i).getMax().getNumeric());					
				values.add(val.getNumeric()+"");
				f.setValue((ArrayList<String>)values);										
			}
			else if( val.isString() || val.isNA())
			{
				f.setType("Nominal");
				if(val.isNA())
				{
					values.add("N/A");
				}
				else
				{
					//comment to change possibly
					List<TypedValue> tvs = aScoredSpecs.get(i).getDict();	
					for(TypedValue tv :tvs)
					{
						values.add(tv.getString());
					}					
				}
				f.setValue((ArrayList<String>)values);										
			}
			rateFeatures.add(f);								
		}
		aRateFeatureList.setFeatures(rateFeatures);
	
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

		LOG.debug(" data is  " + dataSpec);
		LOG.debug(" data is  " + dataRate);

		Gson gson = new Gson();
		UserFeatureModel userFMSpec = gson.fromJson(dataSpec, UserFeatureModel.class);
		UserFeatureModel userFMRate = gson.fromJson(dataRate, UserFeatureModel.class);

		List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();
		List<ScoredAttribute> userScoredFeaturesRates = new ArrayList<ScoredAttribute>();
			
		for(int i = 0 ; i < userFMSpec.getNames().size() ; i++)
		{
			String tempName = userFMSpec.getNames().get(i);
			ScoredAttribute sa = locateFeatureScoredAttribute(aScoredSpecs, tempName);
			if ( sa != null)
			{
				TypedValue av = new TypedValue(userFMSpec.getValues().get(i));				
				sa.setAttributeDefault(av);
				userScoredFeaturesSpecs.add(sa);				
			}			

		}
		
		for(int i = 0 ; i < userFMRate.getNames().size() ; i++)
		{
			String tempName = userFMRate.getNames().get(i);
			ScoredAttribute sa = locateFeatureScoredAttribute(aScoredSpecs, tempName);			
			if ( sa != null)
			{
				TypedValue av = new TypedValue(userFMRate.getValues().get(i));				
				sa.setAttributeDefault(av);
				userScoredFeaturesRates.add(sa);				

			}
		}

		LOG.debug(" Done ");

		LOG.debug(" specs " + aScoredSpecs.toString());
		LOG.debug(" old products "+ aScoredProducts.toString());
		
		for(ScoredProduct sa : aScoredProducts)
		{
			LOG.debug(sa.toString());					
		}
	
		RankedFeaturesProducts tempProducts = new RankedFeaturesProducts();
		//List<ScoredProduct> productsToDisplay = tempProducts.FilterandReturn(userScoredFeaturesSpecs);
		
		List<ScoredProduct> productsToDisplay  = tempProducts.FeatureSensitiveRanking(userScoredFeaturesSpecs, aCategoryID);

			// Converting
			ArrayList<ProductVO> products = new ArrayList<ProductVO>();		
		    for (ScoredProduct sp: productsToDisplay)
		    {
				ProductVO p = new ProductVO();
				p.setName(sp.getProduct().getName());
				p.setId(sp.getProduct().getId());
				products.add(p);
			 }
			aProductList.setProducts(products);	
			LOG.debug(" new products "+ products.toString());
			for(ProductVO sa : products)
			{
				LOG.debug("new "+ sa.getName());			
			}

			return "/rankedproducts";	
		
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
