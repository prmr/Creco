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
		Set<String> Brands = new HashSet<String>();
		Set<String> Text_search = new HashSet<String>();
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
								Brands.add(productspace);
							}
						}
						else if (collectedtexttillnow.contains(productspace)
								|| collectedbrandstillnow.contains(productspace))
						{
						}
						else
						{
							collectedtexttillnow.add(productspace);
							int count = 0;
							for (int i = 0; i < productspace.length(); i++)
							{
								if (Character.isDigit(productspace.charAt(i)))
									count++;
							}
							if (count < 2 && !productspace.contains("(") && !productspace.contains(")"))
								Text_search.add(productspace);
						}
					}
				}
			}
		}

		for (String brandname : Brands)
			response = response.concat(brandname + "| " + "Brand" + "| ");

		for (String textname : Text_search)
			response = response.concat(textname + "| " + "Text Search" + "| ");
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
	public List<RankingExplanation> rankProducts(List<ScoredAttribute> pScoredAttributes, String pCategoryID)
	{
		return aProductRanker.rankProducts(pScoredAttributes, aDataStore.getCategory(pCategoryID));
	}


	@Override
	public String sendCurrentFeatureList(String dataSpec, String pCategoryId)
	{
		UserFeatureModel userFMSpec = new Gson().fromJson(dataSpec, UserFeatureModel.class);

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

		List<RankingExplanation> rankedProducts = rankProducts(userScoredFeaturesSpecs, pCategoryId);
		// Converting to View Object
		ArrayList<ProductView> products = new ArrayList<ProductView>();
		for (RankingExplanation scoredProduct : rankedProducts)
		{
//			if(scoredProduct.getaAttribute())
			ArrayList<ExplanationView> explanation = new ArrayList<ExplanationView>();
			
			for(RankExplanationInstance rei : scoredProduct.getaRankingExplanation())
			{
				TypedValue value = rei.getaAttributeValue();
				String attributeName = rei.getaAttribute().getAttributeName();
				explanation.add(new ExplanationView(attributeName, value, rei.getaAttributeRank(),rei.getaAttribute().getAttributeScore()));
			}
			products.add(new ProductView(scoredProduct.getaProduct().getId(), scoredProduct.getaProduct().getName(), scoredProduct.getaProduct().getUrl(),explanation));
			
		}
		String response = "";

		if (rankedProducts.size() > 0)
		{
			// This response is to be process by AJAX in JavaScript
			for (ProductView productView : products)
			{
				List<ExplanationView> expView = productView.getExplanation();
				for(ExplanationView e : expView)
				{
					response = response.concat(e.getName() + "|");
					response = response.concat(e.getValue() + "|");
					response = response.concat(e.getAttrRank() + "|");
					response = response.concat(e.getValueRank()+"||");					
				}		
				response=response.concat("{}");
				response = response.concat(productView.getId() + ",");
				response = response.concat(productView.getName() + ",");
				response = response.concat(productView.getUrl() + ";");
	
			
			}
		}

		return response;
	}

	public List<ScoredAttribute> sortFeatures(List<ScoredAttribute> pUserFeatures)
	{
		ScoredAttribute tmp = null;
		for (int i = 0; i < pUserFeatures.size(); i++)
		{
			for (int j = pUserFeatures.size() - 1; j >= i + 1; j--)
			{
				if (pUserFeatures.get(j).getEntropy() > pUserFeatures.get(j - 1).getEntropy())
				{
					tmp = pUserFeatures.get(j);
					pUserFeatures.set(j, pUserFeatures.get(j - 1));
					pUserFeatures.set(j - 1, tmp);
				}
			}
		}
		return pUserFeatures;
	}

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
	public ArrayList<ProductView> searchRankedFeaturesProducts_POST(String pCategoryId, Model pModel)
	{
		List<Product> prodSearch = aProductSort.returnProductsAlphabetically(pCategoryId);
		ArrayList<ProductView> products = new ArrayList<ProductView>();
		for (Product scoredProduct : prodSearch)
		{
			products.add(new ProductView(scoredProduct.getId(), scoredProduct.getName(), scoredProduct.getUrl()));
		}
		return products;
	}

	@Override
	public ArrayList<FeatureView> updateCurrentFeatureList(String pCategoryId)
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
			f.setSpec(true);
			f.setVisible(true);
			f.setDesc(scoredAttr.get(i).getAttributeDesc());

			specFeatures.add(f);
		}
		return specFeatures;
	}
}
