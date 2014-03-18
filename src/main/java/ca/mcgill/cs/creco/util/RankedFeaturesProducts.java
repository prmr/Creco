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
	 * @author MariamN
	 * Feature Sensitive Ranking Algorithm.
	 * @param pFeatureList list of user selected features
	 * @param pCatId category id
	 * @return ranked list of products
	 */
	public List<ScoredProduct> FeatureSensitiveRanking(List <ScoredAttribute> pFeatureList, String pCatId)
	{
		int prodSize = main_aProductSearchResult.size();
		int featSize = pFeatureList.size();
		double score = 0;
		double[] weight = new double [featSize];			
		double[] prodScore = new double [prodSize];
		ScoredProduct [] prodSet = new ScoredProduct [prodSize];
		
		List<ScoredProduct> rankedSet = new ArrayList<ScoredProduct>();
		
		//row is feature, column is a product, 1 if the product contains the feature and 0 otherwise.
		int [][] matrix = new int[featSize][prodSize]; 
		
		//initialize the weights with 0.33
		for(int i=0; i < weight.length;i++)
		{
			weight[i]= 0.33;
		}

		if (pFeatureList.isEmpty())
		{
			return null;
		}	

			for(int i = 0 ; i < featSize ; i++) //for each feature
			{
				String fID = pFeatureList.get(i).getAttributeID();
				TypedValue val = pFeatureList.get(i).getAttributeDefault();
				
				for(int j = 0 ; j < prodSize ; j++) // for each product in the category
				{
					if(!main_aProductSearchResult.get(j).getEqClassId().equals(pCatId))
					{
//						System.out.println("id "+ main_aProductSearchResult.get(j).getEqClassId());
						continue;
					}

					if(main_aProductSearchResult.get(j).getProduct().getSpec(fID) != null || main_aProductSearchResult.get(j).getProduct().getRating(fID) != null) //Check if feature exist in the product specs/rates
					{
						if(main_aProductSearchResult.get(j).getProduct().getSpec(fID).getTypedValue().equals(val)) //check if the values are equal
						{
							matrix[i][j] = 1;
						}
						else
						{
							matrix[i][j] = 0;
						}
					}
				}				
			}
			
			if(matrix.length > 0)
			{
				for(int i = 0 ; i < prodSize; i++)
				{
//					System.out.println("*********** product : " + i +" ********");
					for(int j=0 ; j < featSize;j++)
					{
						double temp = matrix[j][i]*weight[j];
						score = score + temp;
//						System.out.println("feature " + j+" : "+ matrix[j][i]);//.getProduct().getId());								
					}
//					System.out.println("Score for Product "+ i+" "+main_aProductSearchResult.get(i).getProduct().getName()+" is "+score);
					prodScore[i] = score;
					prodSet[i] = main_aProductSearchResult.get(i);
					score = 0;
				}
				
				prodSet = sortProducts(prodScore,prodSet);
				
				for (int i = 0; i<prodScore.length; i++)
				{
					if(prodScore[i] > 0.0)
					{
						//rankedSet.add(main_aProductSearchResult.get(i));
						rankedSet.add(prodSet[i]);
					}
					
				}
				return rankedSet;
			}
		return null;		
	}
	
	public ScoredProduct[]  sortProducts(double[] pWeights, ScoredProduct[] pProducts)
	{
		int len = pWeights.length;
		double tmp = 0;
		ScoredProduct tmpProd = null;
		
		for(int i = 0; i<len; i++)
		{
			for(int j = (len-1); j >= (i+1); j--)
			{
				if(pWeights[j] > pWeights[j-1])
				{
					tmp = pWeights[j];
			        tmpProd = pProducts[j];
			        
			        pWeights[j] = pWeights[j-1];
			        pProducts[j] =  pProducts[j-1];
			        
			        pWeights[j-1] = tmp;
			        pProducts[j-1] = tmpProd;
				}
			}
		}

		return pProducts;		
	}
	
	@Deprecated	
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
						if( b.getAttributeDefault().isNA() || b.getAttributeDefault().isNA() )
						{
							continue;
						}
						if(!(a.getTypedValue().equals(b.getAttributeDefault() ))) // TODO Check this for correctness
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
