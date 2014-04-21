package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.creco.data.Product;

/**
 * Provides explanation for the ranking of each product.
 * Explanation is based on the value rank of the user selected attributes.
 */
public class RankExplanation
{		
	private static final Logger LOG = LoggerFactory.getLogger(RankExplanation.class);
	private Product aProduct;
	//private Category aCategory; 
	private List<RankExplanationInstance> aRankList;
	
	/**
	 * 
	 * @param pProduct product to provide explanation to.
	 * @param pCategory category that the product belongs to.
	 * @param pUserScoredAttributes attribute selected by the user.
	 */
	public RankExplanation(Product pProduct, List<UserScoredAttribute> pUserScoredAttributes)
	{
		aProduct = pProduct;
		aRankList = new ArrayList<RankExplanationInstance>();
		
		for(UserScoredAttribute sa : pUserScoredAttributes)
		{
			RankExplanationInstance rei = null;
			try
			{
				rei = new RankExplanationInstance(pProduct, sa);
				aRankList.add(rei);
			}
			catch(IllegalArgumentException iae)
			{
				LOG.info("No ranking for Attribute: " + sa.getAttributeID()+
						" in Product: " + pProduct.getId());
			}
			
		}
		
	}

	/**
	 * 
	 * @return the product object.
	 */
	public Product getaProduct() 
	{
		return aProduct;
	}


	/**
	 * 
	 * @return return a list of explanations associated with a single product.
	 */
	public List<RankExplanationInstance> getaRankList() 
	{
		return aRankList;
	}
	
	

}
