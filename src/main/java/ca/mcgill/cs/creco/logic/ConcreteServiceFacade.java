/**
 * Copyright 2014 McGill University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;
import ca.mcgill.cs.creco.logic.search.ICategorySearch;
import ca.mcgill.cs.creco.logic.search.ProductSort;
import ca.mcgill.cs.creco.web.model.ExplanationView;
import ca.mcgill.cs.creco.web.model.FeatureView;
import ca.mcgill.cs.creco.web.model.ProductView;
import ca.mcgill.cs.creco.web.model.UserData;
import ca.mcgill.cs.creco.web.model.UserFeaturesModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
/**
 * Default implementation of the service layer.
 */
@Component
public class ConcreteServiceFacade implements ServiceFacade
{
	private static final int MIN_NUMBER_OF_TYPED_LETTERS = 2;
	private static final int NUMBER_OF_FEATURES_TO_DISPLAY = 10;

	@Autowired
	private IDataStore aDataStore;

	@Autowired
	private ICategorySearch aCategorySearch;

	@Autowired
	private ProductRanker aProductRanker;

	@Autowired
	private AttributeExtractor aAttributeExtractor;

	@Autowired
	private ProductSort aProductSort;

	@Override
	public String getCompletions(String pInput)
	{
		if (pInput.length() <= MIN_NUMBER_OF_TYPED_LETTERS)
		{
			return "";
		}
		String[] result_set = new String[100];
		int pointer=0;
		String temp = new String("");
		int index=0;
		JSONArray response = new JSONArray();
		JSONObject obj= new JSONObject();

		for (Category category : aDataStore.getCategories())
		{
			if (category.getNumberOfProducts() == 0)
			{
				continue;
			}
			if (category.getName().toLowerCase().contains(pInput.toLowerCase()))
			{
				result_set[pointer++]=category.getName().toLowerCase();
				result_set[pointer++]="Category";
			}
		}
		Set<String> collectedbrandstillnow = new HashSet<String>();
		Set<String> collectedtexttillnow = new HashSet<String>();
		Set<String> brands = new HashSet<String>();
		Set<String> textSearch = new HashSet<String>();
		for (Product productname : aDataStore.getProducts())
		{
			if (productname.getName().toLowerCase().contains(pInput.toLowerCase()))
			{
				for (String productspace : productname.getName().toLowerCase().split(" "))
				{
					if (productspace.contains(pInput.toLowerCase()))
					{
						if (productspace.equals(productname.getBrandName().toLowerCase()))
						{
							if (collectedbrandstillnow.contains(productspace))
							{

							}
							else
							{
								collectedbrandstillnow.add(productspace);
								brands.add(productspace);
							}
						}
						else if (collectedtexttillnow.contains(productspace) || 
								collectedbrandstillnow.contains(productspace))
						{
						}
						else
						{
							collectedtexttillnow.add(productspace);
							int count = 0;
							for (int i = 0; i < productspace.length(); i++)
							{
								if (Character.isDigit(productspace.charAt(i)))
								{
									count++;
								}
							}
							if (count < 2 && !productspace.contains("(") && !productspace.contains(")"))
							{
								textSearch.add(productspace);
							}								
						}
					}
				}
			}
		}

		for (String brandname : brands)
		{
			if(Arrays.asList(result_set).contains(brandname))
			{
				index= Arrays.asList(result_set).indexOf(brandname);
				result_set[index+1]= result_set[index+1].concat("|Brand");
				temp = result_set[0];
				result_set[0]=result_set[index];
				result_set[index]=temp;
				temp = result_set[1];
				result_set[1]=result_set[index+1];
				result_set[index+1]=temp;
			}
			else
			{
				result_set[pointer++]=brandname;
				result_set[pointer++]="Brand";
			}
		}
		for (String textname : textSearch)
		{
			if(Arrays.asList(result_set).contains(textname))
			{
				index= Arrays.asList(result_set).indexOf(textname);
				result_set[index+1]= result_set[index+1].concat("|Product");
				temp = result_set[0];
				result_set[0]=result_set[index];
				result_set[index]=temp;
				temp = result_set[1];
				result_set[1]=result_set[index+1];
				result_set[index+1]=temp;
			}
			else
			{
				result_set[pointer++]=textname;
				result_set[pointer++]="Product";
			}
		}
		
		
		for(index=0;index<result_set.length;index=index+2)
		{
			obj.put("name", result_set[index]);
			obj.put("type", result_set[index+1]);
			response.add(obj);
			obj= new JSONObject();
		}
		return response.toJSONString();
	}

	@Override
	public Collection<Category> searchCategories(String pQuery)
	{
		return aCategorySearch.queryCategories(pQuery);
	}

	@Override
	public Category getCategory(String pId)
	{
		return aDataStore.getCategory(pId);
	}

	@Override
	public List<RankExplanation> rankProducts(List<UserScoredAttribute> pUserScoredAttributes, String pCategoryID)
	{
		return aProductRanker.rankProducts(pUserScoredAttributes, aDataStore.getCategory(pCategoryID));
	}


	@Override
	public String sendCurrentFeatureList(String pUserFeatureList, String pCategoryId)
	{
		UserData userFMSpec = new Gson().fromJson(pUserFeatureList, UserData.class);

		List<UserScoredAttribute> userScoredFeaturesSpecs = new ArrayList<UserScoredAttribute>();

		for(UserFeaturesModel userFeature: userFMSpec.getUserFeatures())
		{			
			String tempId = userFeature.getId();
			ScoredAttribute sa = locateFeatureScoredAttribute(
					aAttributeExtractor.getAttributesForCategory(pCategoryId), tempId);
			if (sa != null)
			{
				UserScoredAttribute usa = new UserScoredAttribute(sa,userFeature.getValue());
				userScoredFeaturesSpecs.add(usa);
			}
			
		}
		Collections.sort(userScoredFeaturesSpecs, UserScoredAttribute.SORT_BY_USER_SCORE);

		List<RankExplanation> rankedProducts = rankProducts(userScoredFeaturesSpecs, pCategoryId);

		ArrayList<ProductView> products = new ArrayList<ProductView>();
		for (RankExplanation productRankingExplanation : rankedProducts)
		{
			ArrayList<ExplanationView> explanation = new ArrayList<ExplanationView>();
			
			for(RankExplanationInstance rei : productRankingExplanation.getaRankList())
			{
				TypedValue value = rei.getaAttributeValue();				
				String attributeName = rei.getaAttribute().getAttributeName();
				explanation.add(new ExplanationView(attributeName, value, rei.getaAttributeRank(), rei.getaAttribute().getUserScore()));
			}
			products.add(new ProductView(productRankingExplanation.getaProduct().getId(), productRankingExplanation.getaProduct().getName(),
					productRankingExplanation.getaProduct().getUrl(), explanation, productRankingExplanation.getaProduct().getImage()));			
		}		
		
		String response = "";
		if (rankedProducts.size() > 0)
		{
			response = createExplainedProductsResponse(products);
			
		}		
		
		return response;
	}

	/**
	 * 
	 * @param pProducts list of products to display.
	 * @return Create JSON response object representing the products and their explanations.
	 */
	private String createExplainedProductsResponse(ArrayList<ProductView> pProducts)
	{
		JsonArray jResponse = new JsonArray();
		
		for (ProductView productView : pProducts)
		{		
			List<ExplanationView> expView = productView.getExplanation();
			
			JsonObject jProductObj = new JsonObject();
			JsonArray jExplanationArray = new JsonArray();
			
			jProductObj.addProperty("productID", productView.getId());
			jProductObj.addProperty("productName", productView.getName());
			jProductObj.addProperty("productURL", productView.getUrl());
			jProductObj.addProperty("productIMAGE", productView.getImage());		
			
			for(ExplanationView e : expView)
			{
				JsonObject jExplanationElem = new JsonObject();
						
				TypedValue tValue = e.getValue();
				
				jExplanationElem.addProperty("name", e.getName());
				jExplanationElem.addProperty("productsNum", pProducts.size());
				jExplanationElem.addProperty("boolean", e.getIsBoolean());
				jExplanationElem.addProperty("rank", e.getValueRank());
				jExplanationElem.addProperty("userScore", e.getAttrScore());
							
				if(e.getValueRank() == -1)
				{
					jExplanationElem.addProperty("isExplained", "-1");
				}
				else
				{
					jExplanationElem.addProperty("isExplained", "1");
					
					if(e.getIsBoolean())
					{
						if(tValue.getBoolean())
						{
							jExplanationElem.addProperty("boolValue", "True");

						}
						else
						{
							jExplanationElem.addProperty("boolValue", "False");
						}
					}
				}

				jExplanationArray.add(jExplanationElem);										
			}						
			jProductObj.add("explanation", jExplanationArray);		
			jResponse.add(jProductObj);
		}
		return jResponse.toString();
	}
	
	/***
	 * 
	 * @param pUserFeatures list of scored attributes selected by the user.
	 * @return list of sorted scored attributes according to attribute correlation.
	 */
	public List<ScoredAttribute> sortFeatures(List<ScoredAttribute> pUserFeatures)
	{
		ScoredAttribute tmp = null;
		for (int i = 0; i < pUserFeatures.size(); i++)
		{
			for (int j = pUserFeatures.size() - 1; j >= i + 1; j--)
			{
				if (pUserFeatures.get(j).getCorrelation() > pUserFeatures.get(j - 1).getCorrelation())
				{
					tmp = pUserFeatures.get(j);
					pUserFeatures.set(j, pUserFeatures.get(j - 1));
					pUserFeatures.set(j - 1, tmp);
				}
			}
		}
		return pUserFeatures;
	}

	/***
	 * 
	 * @param pFeatureList list of all scored attributes.
	 * @param pName name of the attribute to find.
	 * @return scored attribute matching the parameter name
	 */
	public ScoredAttribute locateFeatureScoredAttribute(List<ScoredAttribute> pFeatureList, String pID)
	{
		for (int i = 0; i < pFeatureList.size(); i++)
		{
			ScoredAttribute temp = pFeatureList.get(i);
			if (temp.getAttributeID().equals(pID))
			{
				return temp;

			}
		}
		return null;
	}

	@Override
	public ArrayList<ProductView> searchRankedFeaturesProducts(String pCategoryId, Model pModel)
	{
		List<Product> prodSearch = aProductSort.returnTopProducts(pCategoryId);
		ArrayList<ProductView> products = new ArrayList<ProductView>();
		ArrayList<ExplanationView> emptyExplanation = new ArrayList<ExplanationView>();
		for (Product scoredProduct : prodSearch)
		{
			products.add(new ProductView(scoredProduct.getId(), scoredProduct.getName(), 
					scoredProduct.getUrl(), emptyExplanation , scoredProduct.getImage()));
		}
		return products;
	}

	@Override
	public ArrayList<FeatureView> createFeatureList(String pCategoryId)
	{
		ArrayList<FeatureView> specFeatures = new ArrayList<FeatureView>();
		List<ScoredAttribute> scoredAttr = aAttributeExtractor.getAttributesForCategory(pCategoryId);
		// Display top 10 scored attributes
		for (int i = 0; i < scoredAttr.size(); i++)
		{
			if (i > NUMBER_OF_FEATURES_TO_DISPLAY)
			{
				break;
			}
			FeatureView f = new FeatureView();
			f.setId(scoredAttr.get(i).getAttributeID());
			f.setName(scoredAttr.get(i).getAttributeName());
			f.setVisible(true);
			f.setDesc(scoredAttr.get(i).getAttributeDesc());
			
			if(scoredAttr.get(i).getAttributeID().equals("254"))
			{
				f.setScore(100);				
			}
			else
			{
				f.setScore(0);
			}
			specFeatures.add(f);
		}
		return specFeatures;
	}
	
	@Override
	@Deprecated
	public JSONObject createJSONforallattributes(String pCategoryId)
	{
		JSONObject obj = new JSONObject();
		
		ArrayList<FeatureView> specFeatures = createFeatureList(pCategoryId);
	
		JSONArray userFeatures = new JSONArray();
		JSONArray list_ids = new JSONArray();
		JSONArray list_values = new JSONArray();
		
		for(FeatureView temporary : specFeatures)
		{
			UserFeaturesModel userModel= new UserFeaturesModel();
			userModel.setId(temporary.getId());
			userModel.setName(temporary.getName());
			userModel.setValue((int)temporary.getScore());
			userFeatures.add(userModel.toString());			
		}
		
		obj.put("userFeatures",userFeatures.toJSONString());
//		obj.put("aIds", list_ids);
	//	obj.put("aValues",list_values);
		return (obj);
	}
	
}
