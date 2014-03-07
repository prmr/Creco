package ca.mcgill.cs.creco.server;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.creco.data.*;
import ca.mcgill.cs.creco.logic.AttributeValue;
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
						if((b.getAttributeMean().toString()).equals("NA"))
						{
							continue;
						}
						if(!(String.valueOf(a.getValue()).equals(b.getAttributeMean().toString())))
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
