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
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.logic.AttributeValue;
import ca.mcgill.cs.creco.logic.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;
import ca.mcgill.cs.creco.server.RankedFeaturesProducts;
import ca.mcgill.cs.creco.server.SearchService;
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
	
	@Autowired
	private SearchService searchService;
	
	private List<ScoredAttribute> scoredRatings; 
	private List<ScoredAttribute> scoredSpecs; 
	
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
		System.out.println("in setscoredRatings " + pScoredRatings.toString());
		this.scoredRatings=pScoredRatings;	
		
	}
	public void setScoredSpecs(List<ScoredAttribute> pScoredSpecs)
	{
		System.out.println("in setscoredSpecs"+ pScoredSpecs.toString());
		
		this.scoredSpecs=pScoredSpecs;	
		
	}

	public List<ScoredAttribute> getScoredSpecs()
	{
		return this.scoredSpecs;
		
	}	
	
	public List<ScoredAttribute> getScoredRatings()
	{
		return this.scoredRatings;
		
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
		   List<Category> categoryList = searchService.searchCategories(typedString);		

		   int count=0;
		   String ajax_code=new String();
			ArrayList<EqcVO> eqcs = new ArrayList<EqcVO>();		
			for (Category cat: categoryList) {
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
	    
	    scoredRatings = rankedProducts.getaRatingList();
	    scoredSpecs = rankedProducts.getaSpecList();
	    	   
	    // Converting
		ArrayList<ProductVO> products = new ArrayList<ProductVO>();		
	    for (ScoredProduct sp: scoredProducts) 
	    {
			ProductVO p = new ProductVO();
			p.setName(sp.getProduct().getName());
			p.setId(sp.getProduct().getId());
			products.add(p);
	    }
		productList.setProducts(products);	
		getCurrentFeatureList();
		return "/rankedproducts";
	}
	
	public void getCurrentFeatureList()
	{		
		LOG.debug("Get Current Features");
		
		ArrayList<FeatureVO> specFeatures = new ArrayList<FeatureVO>();	
		ArrayList<FeatureVO> rateFeatures = new ArrayList<FeatureVO>();

		ArrayList<String> values;
	
		//For now will get top 10 scores
		int featureNumToDisplay = 10;
		for (int i = 0 ; i < scoredSpecs.size() ; i++)
		{
		    
			if(i>featureNumToDisplay)
			{
				break;
			}

			values = new ArrayList <String>();
			FeatureVO f = new FeatureVO();
			f.setId(scoredSpecs.get(i).getAttributeID());
			f.setName(scoredSpecs.get(i).getAttributeName());
			f.setSpec(true);
			f.setRate(false);			
			f.setVisible(true);
			
			AttributeValue val = scoredSpecs.get(i).getAttributeMean();			
			
			if(val.isBool() && val!=null)
			{	
				f.setType("Bool");								
				values.add(val.getBoolValue()+"");
				f.setValue(values);
			}
			else
			{
				if(val.isNumeric() && val!=null)
					{
						f.setType("Numeric");					
						values.add(val.getNumericValue()+"");
						f.setValue(values);										
					}
				else
				{
					if(val.isNominal() && val!=null)
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
								values.add("0");
							}
							else
							{
								values.add(""+val.getNominalValue());							
							}
							f.setValue(values);										
						}
					}
				}
				specFeatures.add(f);	
		}		
		
		for (int i = 0; i < scoredRatings.size() ; i++)
		{
			if(i > featureNumToDisplay)
			{
				break;
			}
			values = new ArrayList <String>() ;
			FeatureVO f = new FeatureVO();
			f.setId(scoredRatings.get(i).getAttributeID());
			f.setName(scoredRatings.get(i).getAttributeName());
			f.setRate(true);
			f.setSpec(false);			
			f.setVisible(true);
			
			AttributeValue val = scoredRatings.get(i).getAttributeMean();	
			
			if(val.isBool() && val!=null)
			{
				f.setType("Bool");								
				values.add(val.getBoolValue()+"");
				f.setValue(values);
			}
			else
			{
				if(val.isNumeric() && val!=null)
				{
					f.setType("Numeric");					
					values.add(val.getNumericValue()+"");
					f.setValue(values);										
				}
				else
				{
					if(val.isNominal() && val!=null)
					{
						if(val.getNominalValue().equalsIgnoreCase("true") || val.getNominalValue().equalsIgnoreCase("false")) 
						{
							f.setType("Bool");
						} 
						else 
						{
							f.setType("Nominal");
						}
						
						values.add(""+val.getNominalValue());
						f.setValue(values);										
					}
				}
			}
			rateFeatures.add(f);								
		}
		rateFeatureList.setFeatures(rateFeatures);
		specFeatureList.setFeatures(specFeatures);	
		
		System.out.println("***********Specs ");
		
		printLogs(specFeatureList);
		
		System.out.println("***********Rates ");

		printLogs(rateFeatureList);
						
	}
	
	public void printLogs(FeatureListVO list)
	{
		for(FeatureVO f: list.getFeatures())
		{
			System.out.println("name : " + f.getName());
			//System.out.println("id : " + f.getId());
			//System.out.println("score : " + f.getScore());
			//System.out.println("visibel : " + f.getVisible());
			System.out.println("value : " + f.getValue());						
		}
	}
		
	@RequestMapping(value="/popupFeature", method = RequestMethod.GET)
	public String getPopUp() 
	{
		return "/popupFeature";
	}
	
	@RequestMapping(value="/sendFeatures", method = RequestMethod.POST)
	public String sendCurrentFeatureList(@RequestParam String dataSpec, @RequestParam String dataRate)
	{
		System.out.println(" data is  " + dataSpec);
		
		System.out.println(" data is  " + dataRate);
	
		Gson gson = new Gson();
		UserFeatureModel userFMSpec = gson.fromJson(dataSpec, UserFeatureModel.class);
		UserFeatureModel userFMRate = gson.fromJson(dataRate, UserFeatureModel.class);
		
		List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();
		List<ScoredAttribute> userScoredFeaturesRates = new ArrayList<ScoredAttribute>();
			
		for(int i = 0 ; i < userFMSpec.getNames().size() ; i++)
		{
			String tempName = userFMSpec.getNames().get(i);
			ScoredAttribute sa = locateFeatureScoredAttribute(scoredSpecs, tempName);
			if ( sa != null)
			{
				AttributeValue av = new AttributeValue(userFMSpec.getValues().get(i));				
				sa.setAttributeMean(av);
				userScoredFeaturesSpecs.add(sa);				
			}

			System.out.println(" Name " + sa.getAttributeName());			
			System.out.println(" updated Value " + sa.getAttributeMean());			
			System.out.println(" Score " + sa.getAttributeScore());			

		}
		
		for(int i = 0 ; i < userFMRate.getNames().size() ; i++)
		{
			String tempName = userFMSpec.getNames().get(i);
			ScoredAttribute sa = locateFeatureScoredAttribute(scoredSpecs, tempName);
			if ( sa != null)
			{
				AttributeValue av = new AttributeValue(userFMRate.getValues().get(i));				
				sa.setAttributeMean(av);
				userScoredFeaturesRates.add(sa);				
			}

			System.out.println(" Name " + sa.getAttributeName());			
			System.out.println(" updated Value " + sa.getAttributeMean());			
			System.out.println(" Score " + sa.getAttributeScore());			

		}

		System.out.println(" Done ");
		
		//call Priya sorting function and pass userScoredFeatures as parameter
		
		return "popupFeature";
				
	}	
	
	/**
	 * 
	 * @param pFeatureList : feature list, either specs or ratings
	 * @param pName   : Name of feature to locate
	 * @return ScoredAttribute matching the pName
	 */
	public ScoredAttribute locateFeatureScoredAttribute(List<ScoredAttribute> pFeatureList, String pName)
	{
		System.out.println(" in locate feature " + pName);			
		
		System.out.println(" Feature list is " + pFeatureList.toString());			
		
		for (int i = 0 ; i< pFeatureList.size() ; i++)
		{
			ScoredAttribute temp = pFeatureList.get(i);
			if(temp.getAttributeName().equals(pName))
			{
				System.out.println(" feature located");
				double score = pFeatureList.get(i).getAttributeScore();
				System.out.println("score for" + pName +" is " + score);
				System.out.println("old value is " + pFeatureList.get(i).getAttributeMean());

				
				return pFeatureList.get(i);			
			}
		}
		return null;
	}
}
