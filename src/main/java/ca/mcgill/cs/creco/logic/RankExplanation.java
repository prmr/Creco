package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;

public class RankExplanation {
	
	private Product aProduct;
	private Category aCategory; 
	private List<RankExplanationInstance> aRankList;
	
	public RankExplanation(Product pProduct,Category pCategory,List<ScoredAttribute> pScoredAttributes)
	{
		aProduct = pProduct;
		aCategory = pCategory;
		aRankList = new ArrayList<RankExplanationInstance>();
		
		for(ScoredAttribute sa : pScoredAttributes)
		{
			RankExplanationInstance rei= null;
			try
			{
				rei = new RankExplanationInstance(pProduct, sa);
				aRankList.add(rei);
			}
			catch(IllegalArgumentException iae)
			{
				System.out.println("No ranking for Attribute: " + sa.getAttributeID()+
						" in Product: " + pProduct.getId());
			}
			
		}
		
	}

	public Product getaProduct() 
	{
		return aProduct;
	}

	public Category getaCategory() 
	{
		return aCategory;
	}

	public List<RankExplanationInstance> getaRankList() 
	{
		return aRankList;
	}
	
	

}
