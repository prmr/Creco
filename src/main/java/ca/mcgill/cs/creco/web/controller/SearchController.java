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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.TypedValue;
import ca.mcgill.cs.creco.data.TypedValue.Type;

import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;
import ca.mcgill.cs.creco.data.TypedValue.Type;
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

@Controller
//@RequestMapping("/")
public class SearchController
{
	private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);
	
	
	private List<ScoredAttribute> aScoredRatings; 
	private List<ScoredAttribute> aScoredSpecs; 
	List<ScoredProduct> aScoredProducts;
	
	@Autowired
	private ProductListVO productList;
	
	@Autowired
	private EqcListVO eqcList;
	
	@Autowired
	private MainQueryVO mainQuery;

	@Autowired
	private FeatureListVO userList;
	@Autowired
	private FeatureListVO specFeatureList;

	@Autowired
	private FeatureListVO rateFeatureList;

	@Autowired
	private FeatureListVO featureList;

	@Autowired
	private FeatureVO userSpecFeatures;
	
	@Autowired
	private IDataStore aDataStore;
	
	@Autowired
	private ICategorySearch aCategorySearch;
	
	@Autowired
	private IProductSearch aProductSearch;
	
	@ModelAttribute("mainQuery")
	private MainQueryVO getMainQuery() {
		return new MainQueryVO();
	}
	
	@ModelAttribute("userList")
	private FeatureListVO getUserList() {
		return new FeatureListVO();
	}

	@ModelAttribute("eqcList")
	private EqcListVO getEqcList() {
		return eqcList;
	}
	
	@ModelAttribute("productList")
	private ProductListVO getProductList() {
		return productList;
	}
	@ModelAttribute("featureList")
	private FeatureListVO getFeatureList() {
		return featureList;
	}
	
	@ModelAttribute("specFeatureList")
	private FeatureListVO getSpecFeatureList() {
		return specFeatureList;
	}
	
	@ModelAttribute("userSpecFeatures")
	private FeatureVO getUserSpecFeatures() {
		return userSpecFeatures;
	}
	
	@ModelAttribute("rateFeatureList")
	private FeatureListVO getRateFeatureList() {
		return rateFeatureList;
	}
	
	
	public void setScoredRatings(List<ScoredAttribute> pScoredRatings)
	{

		LOG.debug("in setscoredRatings " + pScoredRatings.toString());
		this.aScoredRatings=pScoredRatings;	

		
	}
	public void setScoredSpecs(List<ScoredAttribute> pScoredSpecs)
	{

		LOG.debug("in setscoredSpecs"+ pScoredSpecs.toString());
		
		this.aScoredSpecs=pScoredSpecs;	
		
	}

	public List<ScoredAttribute> getScoredSpecs()
	{
		return this.aScoredSpecs;
		
	}	
	
	public List<ScoredAttribute> getScoredRatings()
	{
		return this.aScoredRatings;
		
	}	
	

	// Levenshtein Distance used to calculate distance between two strings. 
	public int LevenshteinDistance (String s0, String s1) {
		int len0 = s0.length()+1;
		int len1 = s1.length()+1;
	 
		// the array of distances
		int[] cost = new int[len0];
		int[] newcost = new int[len0];
	 
		// initial cost of skipping prefix in String s0
		for(int i=0;i<len0;i++) cost[i]=i;
	 
		// dynamicaly computing the array of distances
	 
		// transformation cost for each letter in s1
		for(int j=1;j<len1;j++) {
	 
			// initial cost of skipping prefix in String s1
			newcost[0]=j-1;
	 
			// transformation cost for each letter in s0
			for(int i=1;i<len0;i++) {
	 
				// matching current letters in both strings
				int match = (s0.charAt(i-1)==s1.charAt(j-1))?0:1;
	 
				// computing cost for each transformation
				int cost_replace = cost[i-1]+match;
				int cost_insert  = cost[i]+1;
				int cost_delete  = newcost[i-1]+1;
	 
				// keep minimum cost
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete),cost_replace );
			}
	 
			// swap cost/newcost arrays
			int[] swap=cost; cost=newcost; newcost=swap;
		}
	 
		// the distance is the cost for transforming all letters in both strings
		return cost[len0-1];
	}
	
	
	
	public String returnfournearest(String abcd)
	{
		 String ajax_return=new String();
		   int min1=200;
		   int min2=200;
		   int min3=200;
		   int min4=200;
		   Category min1_s=null;
		   Category min2_s=null;
		   Category min3_s=null;
		   Category min4_s=null;
		 
		   for (Category category123 : aDataStore.getEquivalenceClasses()) 
			{
			   if(category123.getCount()==0)
				   continue; 
			   int value;
			   if(category123.getName().toLowerCase().contains(abcd.toLowerCase()))
				   value=0;
			   else
		  value= LevenshteinDistance(abcd.toLowerCase(),category123.getName().toLowerCase());
		  if(value<=min1)
		  {
			 min4=min3;
			 min3=min2;
			 min2=min1;
			 min1=value;
			 min4_s=min3_s;
			 min3_s=min2_s;
			 min2_s=min1_s;
			 min1_s=category123;
		  }
		  else if(value<=min2)
		  {
			  min4=min3;
				 min3=min2;
				 min2=value;
				 min4_s=min3_s;
				 min3_s=min2_s;
				 min2_s=category123;
		  }
		  else if(value<=min3)
		  {
			  min4=min3;
				 min3=value;
				 min4_s=min3_s;
				 min3_s=category123;
		  }
		  else if(value<=min4)
		  {
			  min4=value;
			  min4_s=category123;
			}
			}
		   ajax_return = "";

		ajax_return=ajax_return.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
		ajax_return=ajax_return.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min1_s.getName()+"></input>");
		   ajax_return=ajax_return.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min1_s.getName()+"></input>");
		   ajax_return=ajax_return.concat("<input type=\"submit\" value=\""+min1_s.getName()+"\"></input>") ;
		   ajax_return=ajax_return.concat("	<span class=\"badge\">"+min1_s.getCount()+"</span>");
		   ajax_return=ajax_return.concat( "</form>");
		ajax_return=ajax_return.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
		ajax_return=ajax_return.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min2_s.getName()+"></input>");
		   ajax_return=ajax_return.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min2_s.getName()+"></input>");
		   ajax_return=ajax_return.concat("<input type=\"submit\" value=\""+min2_s.getName()+"\"></input>") ;
		   ajax_return=ajax_return.concat("	<span class=\"badge\">"+min2_s.getCount()+"</span>");
		   ajax_return=ajax_return.concat( "</form>");
		ajax_return=ajax_return.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
		ajax_return=ajax_return.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min3_s.getName()+"></input>");
		   ajax_return=ajax_return.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min3_s.getName()+"></input>");
		   ajax_return=ajax_return.concat("<input type=\"submit\" value=\""+min3_s.getName()+"\"></input>") ;
		   ajax_return=ajax_return.concat("	<span class=\"badge\">"+min3_s.getCount()+"</span>");
		   ajax_return=ajax_return.concat( "</form>");
		ajax_return=ajax_return.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
		ajax_return=ajax_return.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min4_s.getName()+"></input>");
		   ajax_return=ajax_return.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+min4_s.getName()+"></input>");
		   ajax_return=ajax_return.concat("<input type=\"submit\" value=\""+min4_s.getName()+"\"></input>") ;
		   ajax_return=ajax_return.concat("	<span class=\"badge\">"+min4_s.getCount()+"</span>");
		   ajax_return=ajax_return.concat( "</form>");
		   return (ajax_return);  
	}
	
	
	
	   @RequestMapping(value="/ajax", method=RequestMethod.POST )  
 @ResponseBody  
 public String createSmartphone(@RequestBody String typedString) {  
		   List<Category> categoryList = aCategorySearch.queryCategories(typedString);		

		   int count=0;
		   String ajax_code=new String();
			ArrayList<EqcVO> eqcs = new ArrayList<EqcVO>();		
			for (Category cat: categoryList) {
				if(cat.getCount()==0)
					continue; 
				count++;
				EqcVO eqc = new EqcVO();
				   ajax_code=ajax_code.concat("<form action=\"/searchRankedFeaturesProducts\" method=\"POST\">");
				   String s = new String("<input id=\"id\" name=\"id\" type=\"hidden\" value="+cat.getName()+"></input>");
				   s = s.concat("<input id=\"id\" name=\"id\" type=\"hidden\" value="+cat.getName()+"></input>");
				   s = s.concat("<input type=\"submit\" value=\""+cat.getName()+"\"></input>" );
				   s = s.concat("	<span class=\"badge\">"+cat.getCount()+"</span>");
				   s = s.concat("</form>");
				   ajax_code=ajax_code.concat(s);
				eqc.setId(cat.getId());
				eqc.setName(cat.getName());
				eqc.setCount(cat.getCount());
				eqcs.add(eqc);
			}
			eqcList.setEqcs(eqcs);
			if(count==0)
				return(returnfournearest(typedString));
			else		
	  return(ajax_code);
 }  
	   
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String init(Model model)
	{
		 UserFeatureModel form = new UserFeatureModel();
		 model.addAttribute("myForm", form);
		 specFeatureList = new FeatureListVO();
		 rateFeatureList = new FeatureListVO();
		 productList = new ProductListVO ();
		 return "/index";								
	}
	
	@RequestMapping(value = "/experiment", method = RequestMethod.GET)
	public String init2(Model model)
	{
		 UserFeatureModel form = new UserFeatureModel();
		 model.addAttribute("myForm", form);

		return "/experiment";
	}
	
	@RequestMapping(value = "/searchEqClass", method = RequestMethod.POST)
	public String searchEqClass(@ModelAttribute("mainQuery") MainQueryVO pMainQuery, BindingResult result, RedirectAttributes redirectAttrs) {
		mainQuery = pMainQuery;
		List<Category> categoryList = aCategorySearch.queryCategories(mainQuery.getQuery());	
		
		
		//Nishanth code 
		String main_string = "";
		for (Category category123 : aDataStore.getEquivalenceClasses()) 
		{
		   if(category123.getCount()==0)
			   continue;
		 main_string=main_string.concat("\"");
		 main_string=main_string.concat(category123.getName());
		 main_string=main_string.concat("\"");
		 main_string=main_string.concat(",");
		 main_string=main_string.concat("\n");
		}

		LOG.debug(main_string);

		// Nishanth code
		//Converting
		ArrayList<EqcVO> eqcs = new ArrayList<EqcVO>();		
		for (Category cat: categoryList) {
			if(cat.getCount()==0)
				continue;
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
	    List<Category> categoryList = aCategorySearch.queryCategories(mainQuery.getQuery());
	    Category target = null;
	    for (Category cat: categoryList) {
	    	if (cat.getId().equals(eqc.getId())) {
	    		target = cat;
	    	}
	    }
	    
		List<ScoredProduct> prodSearch = aProductSearch.queryProductsReturnAll(mainQuery.getQuery(), target.getId());
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
		productList.setProducts(products);	
		return getCurrentFeatureList();
	}
	
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

			f.setDesc(aScoredSpecs.get(i).getaAttributeDesc());


			TypedValue val = aScoredSpecs.get(i).getAttributeMean();			

			if(val.getType() == Type.BOOLEAN && val!=null)
			{	
				f.setType("Bool");								
				values.add(val.getBooleanValue()+"");
				f.setValue((ArrayList<String>) values);
			}
			else
			{

				if((val.getType() == Type.DOUBLE || val.getType() == Type.INTEGER ) && val!=null)

					{
						f.setType("Numeric");
						f.setMinValue(aScoredSpecs.get(i).getMin().getNumericValue());
						f.setMaxValue(aScoredSpecs.get(i).getMax().getNumericValue());										
						values.add(val.getNumericValue()+"");
						f.setValue((ArrayList<String>)values);	
												
					}
				else
				{
					if(val.getType() == Type.STRING  && val!=null)
					{
						if(val.getNominalValue().equalsIgnoreCase("true") || val.getNominalValue().equalsIgnoreCase("false")) 
						{

								f.setType("Bool");
						}
						else 
						{
							
								f.setType("Nominal");
						}

							if(val.getNominalValue().equals("N/A"))
							{

								values.add("N/A");
							}
							else
							{
								values= aScoredSpecs.get(i).getDict();								

							}
							f.setValue((ArrayList<String>)values);										
						}
					}
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

			LOG.debug("***********Rate Name ********* "+ f.getName());

			f.setDesc(aScoredRatings.get(i).getaAttributeDesc());
			f.setRate(true);
			f.setSpec(false);			
			f.setVisible(true);


			TypedValue val = aScoredRatings.get(i).getAttributeMean();	

			if(val.getType() == Type.BOOLEAN && val!=null)
			{
				f.setType("Bool");								
				values.add(val.getBooleanValue()+"");
				f.setValue((ArrayList<String>)values);

			}
			else
			{
				if((val.getType() == Type.DOUBLE || val.getType() == Type.INTEGER ) && val!=null)
				{
					f.setType("Numeric");
					f.setMinValue(aScoredRatings.get(i).getMin().getNumericValue());
					f.setMaxValue(aScoredRatings.get(i).getMax().getNumericValue());					
					values.add(val.getNumericValue()+"");
					f.setValue((ArrayList<String>)values);										
				}
				else
				{
					if(val.getType() == Type.STRING && val!=null)
					{
						if(val.getNominalValue().equalsIgnoreCase("true") || val.getNominalValue().equalsIgnoreCase("false")) 
						{
							f.setType("Bool");
						} 
						else 
						{
							f.setType("Nominal");
						}

						if(val.getNominalValue().equals("N/A"))
						{
							values.add("N/A");
						}
						else
						{
							values=aScoredRatings.get(i).getDict();					
						}
						f.setValue((ArrayList<String>)values);										
					}
				}
			}
			rateFeatures.add(f);								
		}
		rateFeatureList.setFeatures(rateFeatures);
	
		specFeatureList.setFeatures(specFeatures);	

		return "/rankedproducts";
						
	}
	
	@RequestMapping(value="/popupFeature", method = RequestMethod.GET)
	public String getPopUp() 
	{
		return "/popupFeature";
	}
	
	@RequestMapping(value="/searchRankedFeaturesProducts", method = RequestMethod.GET)
	public String getRankedProducts() 
	{
		return "/rankedproducts";
	}
	@RequestMapping(value="/rankedproducts", method = RequestMethod.GET)
	public String getRankedProducts2() 
	{
		return "/rankedproducts";
	}
	
	/**
	 * */
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
				sa.setAttributeMean(av);
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
				sa.setAttributeMean(av);
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
	
		RankedFeaturesProducts Products = new RankedFeaturesProducts();
		List<ScoredProduct> productsToDisplay = Products.FilterandReturn(userScoredFeaturesSpecs);

			// Converting
			ArrayList<ProductVO> products = new ArrayList<ProductVO>();		
		    for (ScoredProduct sp: productsToDisplay)
		    {
				ProductVO p = new ProductVO();
				p.setName(sp.getProduct().getName());
				p.setId(sp.getProduct().getId());
				products.add(p);
			 }
			productList.setProducts(products);	
			LOG.debug(" new products "+ products.toString());
			for(ProductVO sa : products)
			{
				LOG.debug("new "+ sa.getName());			
			}

			return "/rankedproducts";	
		
	}	

	/**
	 * 
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
				LOG.debug("old mean value is " + pFeatureList.get(i).getAttributeMean());
				return temp;

			}
		}
		return null;
	}
}
