package ca.mcgill.cs.creco.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.mcgill.cs.creco.data.*;
import ca.mcgill.cs.creco.logic.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;


public class RankedFeaturesProducts {
	private static List<ScoredProduct> main_aProductSearchResult;

	private static List<ScoredAttribute> aRatingList;

	private static List<ScoredAttribute> aSpecList;

	private static List<ScoredProduct> aProductSearchResult;
	
public RankedFeaturesProducts()
{
	
}
	
	public RankedFeaturesProducts(List<ScoredAttribute> pRatingList, List<ScoredAttribute> pSpecList, List<ScoredProduct> pProductSearchResult)
	{
		RankedFeaturesProducts.main_aProductSearchResult=pProductSearchResult;
		RankedFeaturesProducts.aProductSearchResult=pProductSearchResult;
		RankedFeaturesProducts.aSpecList = pSpecList;
		RankedFeaturesProducts.aRatingList = pRatingList;

	}
	
	/**
	 * Feature Sensitive Ranking Algorithm.
	 * @param pFeatureList list of user selected features
	 * @param pCatId category id
	 * @return ranked list of products
	 */
	public List<ScoredProduct> FeatureSensitiveRanking(List <ScoredAttribute> pFeatureList, String pCatId)
	{
		int prodSize = main_aProductSearchResult.size();
		int featSize = pFeatureList.size();
		ScoredProduct[] rankedList = new ScoredProduct[prodSize];
		//rankedList = main_aProductSearchResult;
		Boolean flag = false;
			
		if (pFeatureList.isEmpty())
		{
			return null;
		}
		
		ScoredProduct [][] matrix = new ScoredProduct[featSize][prodSize]; //row is feature, column is product containing this feature.
		
			for(int i = 0 ; i < featSize ; i++) //for each feature
			{
				String fID = pFeatureList.get(i).getAttributeID();
				for(int j = 0 ; j < prodSize ; j++) // for each product in the category
				{
					if(!main_aProductSearchResult.get(j).getEqClassId().equals(pCatId))
					{
						System.out.println("id "+ main_aProductSearchResult.get(j).getEqClassId());
						continue;
					}
								
					if(main_aProductSearchResult.get(j).getProduct().getSpec(fID) != null || main_aProductSearchResult.get(j).getProduct().getRating(fID) != null)
					{
						matrix[i][j] = main_aProductSearchResult.get(j);
					}
					else
					{
						flag = true;
					}							
				}				
			}
			if(matrix.length > 0)
			{
				for(int i =0 ;i < featSize; i++)
				{
	
					System.out.println("*********** feature : " + i +" ********");
					for(int j=0 ; j < matrix[i].length;j++)
					{
						System.out.println("product " + j+" : "+ matrix[i][j].getProduct().getId());								
					}
				}
			}
		return null;		
	}
	
	public List<ScoredProduct> FilterandReturn(List<ScoredAttribute> pSpecList)
	{
		List<ScoredProduct> new_set = new ArrayList<ScoredProduct>();
		for(ScoredProduct p: RankedFeaturesProducts.main_aProductSearchResult)
		{
			int flag=0;
			Product product= p.getProduct();
			Iterable<Attribute> specs = product.getSpecs();
			for(Attribute a:specs)
			{
				for(ScoredAttribute b:pSpecList)
				{
					if(a.getName().equals(b.getAttributeName()))
					{
						if( b.getAttributeMean().isNA() || b.getAttributeMean().isNA() )
						{
							continue;
						}
						if(!(a.getTypedValue().equals(b.getAttributeMean() ))) // TODO Check this for correctness
								{
									flag=1;
								}
					}
				}

			}
			if(flag==0)
			{
				new_set.add(p);
			}
		}
		RankedFeaturesProducts.aProductSearchResult = new_set;
		return(RankedFeaturesProducts.aProductSearchResult);
	}


	public List<ScoredProduct> getaProductSearchResult()
	{
		return aProductSearchResult;
	}
	
	public List<ScoredAttribute> getaRatingList()
	{
		return aRatingList;
	}
	
	public List<ScoredAttribute> getaSpecList()
	{
		return aSpecList;
	}
	
}
