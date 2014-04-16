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
import java.util.Collection;
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
import ca.mcgill.cs.creco.logic.search.IProductSearch;
import ca.mcgill.cs.creco.web.model.ExplanationView;
import ca.mcgill.cs.creco.web.model.FeatureView;
import ca.mcgill.cs.creco.web.model.ProductView;
import ca.mcgill.cs.creco.web.model.UserFeatureModel;

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
	private IProductSearch aProductSort;

	@Override
	public String getCompletions(String pInput)
	{
		if (pInput.length() <= MIN_NUMBER_OF_TYPED_LETTERS)
		{
			return "";
		}

		String response = "";

		for (Category category : aDataStore.getCategories())
		{
			if (category.getNumberOfProducts() == 0)
			{
				continue;
			}
			if (category.getName().toLowerCase().contains(pInput.toLowerCase()))
			{
				response = response.concat(category.getName() + "| " + "Category" + "| ");
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
			response = response.concat(brandname + "| " + "Brand" + "| ");
		}
		for (String textname : textSearch)
		{
			response = response.concat(textname + "| " + "Text Search" + "| ");
		}
		return response;
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
	public List<RankExplanation> rankProducts(List<ScoredAttribute> pScoredAttributes, String pCategoryID)
	{
		return aProductRanker.rankProducts(pScoredAttributes, aDataStore.getCategory(pCategoryID));
	}


	@Override
	public String sendCurrentFeatureList(String pUserFeatureList, String pCategoryId)
	{
		UserFeatureModel userFMSpec = new Gson().fromJson(pUserFeatureList, UserFeatureModel.class);

		List<ScoredAttribute> userScoredFeaturesSpecs = new ArrayList<ScoredAttribute>();

		for (int i = 0; i < userFMSpec.getNames().size(); i++)
		{
			String tempName = userFMSpec.getNames().get(i);
			ScoredAttribute sa = locateFeatureScoredAttribute(
					aAttributeExtractor.getAttributesForCategory(pCategoryId), tempName);
			if (sa != null)
			{
				userScoredFeaturesSpecs.add(sa);
			}

		}

		userScoredFeaturesSpecs = sortFeatures(userScoredFeaturesSpecs);

		List<RankExplanation> rankedProducts = rankProducts(userScoredFeaturesSpecs, pCategoryId);

		ArrayList<ProductView> products = new ArrayList<ProductView>();
		for (RankExplanation productRankingExplanation : rankedProducts)
		{
			ArrayList<ExplanationView> explanation = new ArrayList<ExplanationView>();
			
			for(RankExplanationInstance rei : productRankingExplanation.getaRankList())
			{
				TypedValue value = rei.getaAttributeValue();				
				String attributeName = rei.getaAttribute().getAttributeName();
				explanation.add(new ExplanationView(attributeName, value, rei.getaAttributeRank(), rei.getaAttribute().getCorrelation()));
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
	public ScoredAttribute locateFeatureScoredAttribute(List<ScoredAttribute> pFeatureList, String pName)
	{
		for (int i = 0; i < pFeatureList.size(); i++)
		{
			ScoredAttribute temp = pFeatureList.get(i);
			if (temp.getAttributeName().equals(pName))
			{
				return temp;

			}
		}
		return null;
	}

	@Override
	public ArrayList<ProductView> searchRankedFeaturesProducts(String pCategoryId, Model pModel)
	{
		List<Product> prodSearch = aProductSort.returnProductsAlphabetically(pCategoryId);
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

			specFeatures.add(f);
		}
		return specFeatures;
	}
	
	@Override
	public JSONObject createJSONforallattributes(String pCategoryId)
	{
		JSONObject obj = new JSONObject();
		ArrayList<FeatureView> specFeatures =createFeatureList(pCategoryId);
		JSONArray list_name = new JSONArray();
		JSONArray list_values = new JSONArray();
		for(FeatureView temporary : specFeatures)
		{
				list_name.add(temporary.getName());
				list_values.add(null);
		}
		obj.put("aNames",list_name);
		obj.put("aValues",list_values);
		return (obj);
	}
	
}
