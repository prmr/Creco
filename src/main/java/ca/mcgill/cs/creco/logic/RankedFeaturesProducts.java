package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.ScoredAttribute.Direction;

public class RankedFeaturesProducts 
{
	private static List<Product> main_aProductSearchResult;
	private static List<ScoredAttribute> aAttrList;
	private static List<Product> aProductSearchResult;

	public RankedFeaturesProducts()
	{
	
	}
	
	public RankedFeaturesProducts(List<ScoredAttribute> pAttrList, List<Product> pProductSearchResult)
	{
		RankedFeaturesProducts.main_aProductSearchResult=pProductSearchResult;
		RankedFeaturesProducts.aAttrList = pAttrList;		
		RankedFeaturesProducts.aProductSearchResult=pProductSearchResult;
		
	}

	/**
	 * @author MariamN
	 * Feature Sensitive Ranking Algorithm.
	 * @param pFeatureList list of user selected features
	 * @param pCat category
	 * @return ranked list of products
	 */
	public List<Product> FeatureSensitiveRanking(List <ScoredAttribute> pFeatureList, Category pCat)
	{
		int prodSize = main_aProductSearchResult.size();
		int featSize = pFeatureList.size();
		double score = 0;
		double[] weight = new double [featSize];			
		double[] prodScore = new double [prodSize];
		Product [] prodSet = new Product [prodSize];
		List<Product> rankedSet = new ArrayList<Product>();
		List<Product> new_RankedSet = new ArrayList<Product>();
		
		AttributeCorrelator aCorrelator = null;
	
		aCorrelator = new AttributeCorrelator(pCat);
				
		//row is feature, column is a product, 1 if the product contains the feature and 0 otherwise.
		int [][] matrix = new int[featSize][prodSize]; 
		
		//initialize the weights with the corresponding feature correlation score
		for(int i = 0; i < weight.length; i++)
		{
			weight[i] = aCorrelator.computeCorrelation(pFeatureList.get(i).getAttributeID());
		}
		
		if (pFeatureList.isEmpty())
		{
			return main_aProductSearchResult;
		}	

		for(int j = featSize-1; j >= 0 ; j--)
		{
				Direction direction = aCorrelator.computeAttributeDirection(pFeatureList.get(j).getAttributeID());
				rankedSet = directionSensitiveProductSort(pFeatureList.get(j).getAttributeID(),main_aProductSearchResult,direction);								
		}		

		for(int i = 0 ; i < featSize ; i++) //for each feature
		{
			String fID = pFeatureList.get(i).getAttributeID();
			for(int j = 0 ; j < prodSize ; j++) // for each product in the category
			{
				Product prod = rankedSet.get(j);		
				if(prod.getAttribute(fID) != null)
				{
					matrix[i][j] = 1;
				}
				else
				{
					matrix[i][j] = 0;
				}						
			}
		}
			
			
		for(int i = 0 ; i < prodSize; i++)
		{
			for(int j = 0; j < featSize; j++)
			{
					double temp = matrix[j][i]*weight[j];
					score = score + temp;
			}
			prodScore[i] = score;
			prodSet[i] = rankedSet.get(i);
			score = 0;
		}
				
		prodSet = sortProducts(prodScore, prodSet);
		for (int i = 0; i<prodScore.length; i++)
		{
			if(prodScore[i] > 0.0)
			{
				new_RankedSet.add(prodSet[i]);
			}					
		}	
				
		return new_RankedSet;					
	}
	
	
	/**
	 * @author MariamN
	 * @param pAttrId Id of the attribute to calculate it's directions
	 * @param pProductList list of weighted ranked products
	 * @param pDirection direction of the attribute
	 * @return list of ranked products based on attribute direction.
	 */
	public List<Product> directionSensitiveProductSort(String pAttrId, List<Product> pProductList, Direction pDirection)
	{
		Product tmpProd = null;
		for(int i = 0 ; i< pProductList.size() ; i++)
		{
			for(int j = (pProductList.size()-1); j >= (i+1); j--)
			{
				if(pDirection.equals(Direction.MORE_IS_BETTER))
				{
					if(pProductList.get(j).getAttribute(pAttrId) != null && pProductList.get(j-1).getAttribute(pAttrId) != null) //TODO EN: I wasn't sure if this check is still needed now that there is no spec/rating ambiguity?
					{
						if(pProductList.get(j).getAttribute(pAttrId).getTypedValue().isNumeric())
						{
							if(pProductList.get(j).getAttribute(pAttrId).getTypedValue().getNumeric() > pProductList.get(j-1).getAttribute(pAttrId).getTypedValue().getNumeric())
							{
								tmpProd = pProductList.get(j);
								pProductList.set(j,pProductList.get(j-1));		
								pProductList.set(j-1,tmpProd);
							}						

						}
					}
				}
				else
				{
					if(pDirection.equals(Direction.LESS_IS_BETTER))
					{
						if(pProductList.get(j).getAttribute(pAttrId) != null) //TODO EN: I wasn't sure if this check is still needed now that there is no spec/rating ambiguity?
						{
							if(pProductList.get(j).getAttribute(pAttrId).getTypedValue().isNumeric())
							{							
								if(pProductList.get(j).getAttribute(pAttrId).getTypedValue().getNumeric() < pProductList.get(j-1).getAttribute(pAttrId).getTypedValue().getNumeric())
								{
									tmpProd = pProductList.get(j);
									pProductList.set(j,pProductList.get(j-1));	
									pProductList.set(j-1,tmpProd);
								}
							}
						}
					}
					
				}
			}
		}
		
		return pProductList;	
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
	public List<Product> FilterandReturn(List<ScoredAttribute> pSpecList)
	{
		List<Product> new_set = new ArrayList<Product>();
		for(Product product: RankedFeaturesProducts.main_aProductSearchResult)
		{
			int flag=0;
			Iterable<Attribute> specs = product.getAttributes();
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
				new_set.add(product);
			}
		}
		RankedFeaturesProducts.aProductSearchResult = new_set;
		return(RankedFeaturesProducts.aProductSearchResult);
	}

	public  List<ScoredAttribute> getaAttrList()
	{
		return aAttrList;
	}

	public  void setaAttrList(List<ScoredAttribute> aAttrList) 
	{
		RankedFeaturesProducts.aAttrList = aAttrList;
	}

	public List<Product> getaProductSearchResult()
	{
		return aProductSearchResult;
	}
}
