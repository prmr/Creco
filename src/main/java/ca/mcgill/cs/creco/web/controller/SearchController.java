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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.logic.model.AttributeValue;
import ca.mcgill.cs.creco.logic.model.ScoredAttribute;
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
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String init(Model model){
		try
		{
			searchService = new SearchService();
			System.out.println("INIT");
			 UserFeatureModel form = new UserFeatureModel();
			 model.addAttribute("myForm", form);
			 
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
