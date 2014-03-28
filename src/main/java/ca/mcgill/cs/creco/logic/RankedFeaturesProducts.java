package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.ScoredAttribute.Direction;

/**
 * Class to rank the products according to the user selected features.
 */
public class RankedFeaturesProducts 
{
	private static List<Product> aProductSearchResult;
	private static List<ScoredAttribute> aAttrList;

	/**
	 * Empty constructor.
	 */
	public RankedFeaturesProducts()
	{
	
	}
	
	/**
	 * 
	 * @param pAttrList list of scored attributes
	 * @param pProductSearchResult list of products
	 */
	public RankedFeaturesProducts(List<ScoredAttribute> pAttrList, List<Product> pProductSearchResult)
	{
		RankedFeaturesProducts.aProductSearchResult = pProductSearchResult;
		RankedFeaturesProducts.aAttrList = pAttrList;		
	}

	/**
	 * @author MariamN
	 * Feature Sensitive Ranking Algorithm.
	 * @param pFeatList list of user selected features
	 * @param pCat category
	 * @return ranked list of products
	 */
	public List<Product> FeatureSensitiveRanking(List<ScoredAttribute> pFeatList, Category pCat)
	{
		int prodSize = aProductSearchResult.size();
		int featSize = pFeatList.size();
		double score = 0;
		double[] weight = new double [featSize];			
		double[] prodScore = new double [prodSize];
		Product [] prodSet = new Product [prodSize];
		List<Product> rankedSet = new ArrayList<Product>();
		
		//row is feature, column is a product, 1 if the product contains the feature and 0 otherwise.
		int [][] matrix = new int[featSize][prodSize]; 
		
		//initialize the weights with the corresponding feature correlation score
		for(int i = 0; i < weight.length; i++)
		{
			weight[i] = pFeatList.get(i).getCorrelation();		
		}
		
		if (pFeatList.isEmpty())
		{
			return aProductSearchResult;
		}	

		for(int i = 0 ; i < featSize ; i++) //for each feature
		{
			String fID = pFeatList.get(i).getAttributeID();
			for(int j = 0 ; j < prodSize ; j++) // for each product in the category
			{
				Product prod = aProductSearchResult.get(j);
				if(prod.getAttribute(fID) != null)
				{
					if(pFeatList.get(i).isNumeric())
					{
						Direction direction = pFeatList.get(i).getDirection();		
						if(direction.equals(Direction.LESS_IS_BETTER))
						{
							matrix[i][j] = -1;						
						}
						else
						{
							matrix[i][j] = 1;
						}				

					}
					else
					{
						matrix[i][j] = 1;
					}
				}
				else
				{
					matrix[i][j] = 0;
				}						
			}
		}
			
			
		for(int i = 0 ; i < prodSize; i++)
		{
			Product p = aProductSearchResult.get(i);
			for(int j = 0; j < featSize; j++)
			{
					String fID = pFeatList.get(j).getAttributeID();
					if(p.getAttribute(fID) != null)
					{
						if(!p.getAttribute(fID).getTypedValue().isNull() && p.getAttribute(fID).getTypedValue().isNumeric())
						{
							double temp = matrix[j][i]*weight[j]*p.getAttribute(fID).getTypedValue().getNumeric();
							score = score + temp;
						}
						else
						{
							double temp = matrix[j][i]*weight[j];
							score = score + temp;						
						}
					}
			}			
			prodScore[i] = score;
			prodSet[i] = aProductSearchResult.get(i);
			score = 0;
		}
		
		prodSet = sortProducts(prodScore, prodSet);
		for (int i = 0; i<prodScore.length; i++)
		{
			if(prodScore[i] > 0.0)
			{
				rankedSet.add(prodSet[i]);
			}					
		}
		
		if(rankedSet.isEmpty())
		{
			return aProductSearchResult;
		}
		
		return rankedSet;					
	}
		
	
	/**
	 * @author MariamN
	 * @param pWeights attribute correlated weights
	 * @param pProducts list of the products
	 * @return sorted product list based on weights
	 */
	public Product[]  sortProducts(double[] pWeights, Product[] pProducts)
	{
		int len = pWeights.length;
		double tmp = 0;
		Product tmpProd = null;
		
		for(int i = 0; i<len; i++)
		{
			for(int j = len-1; j >= i+1; j--)
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
	

	/**
	 * 
	 * @return list of scored attributes
	 */
	public  List<ScoredAttribute> getaAttrList()
	{
		return aAttrList;
	}

	/**
	 * 
	 * @param pAAttrList attribute list
	 */
	public  void setaAttrList(List<ScoredAttribute> pAAttrList)
	{
		RankedFeaturesProducts.aAttrList = pAAttrList;
	}
	
	/**
	 * 
	 * @return list of products
	 */
	public List<Product> getaProductSearchResult()
	{
		return aProductSearchResult;
	}
	/**
	 * 
	 * @param pProductSearch list of products
	 */
	public void setaProductSearchResult(List<Product> pProductSearch)
	{
		 aProductSearchResult = pProductSearch;
	}
	
	
}
