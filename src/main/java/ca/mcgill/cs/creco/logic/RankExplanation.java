package ca.mcgill.cs.creco.logic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.subst.Token.Type;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.data.TypedValue;

/**
 * Provides explanation for the ranking of each product.
 * Explanation is based on the value rank of the user selected attributes.
 */
public class RankExplanation
{		
	private static final Logger LOG = LoggerFactory.getLogger(RankExplanation.class);
	private Product aProduct;
	private Category aCategory; 
	private List<RankExplanationInstance> aRankList;
	private String aOneLineDesc;
	
	/**
	 * 
	 * @param pProduct product to provide explanation to.
	 * @param pCategory category that the product belongs to.
	 * @param pScoredAttributes attribute selected by the user.
	 */
	public RankExplanation(Product pProduct, Category pCategory, List<ScoredAttribute> pScoredAttributes)
	{
		aProduct = pProduct;
		aCategory = pCategory;
		aRankList = new ArrayList<RankExplanationInstance>();
		
		for(ScoredAttribute sa : pScoredAttributes)
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
		
		aOneLineDesc = OneLineDescription(pProduct);
		
	}
	
	private String OneLineDescription(Product pProduct)
	{ 
		String description = "";
		description = "The " + pProduct.getName() + " is a " + pProduct.getCategory().getName() + ". The " + pProduct.getCategory().getName();
		for (RankExplanationInstance attr : aRankList)
		{
			TypedValue value = attr.getaAttributeValue();
			if(!value.isNA() && !value.isNull())
			{		
				description = description.concat(", ");
				if(value.isBoolean())
				{
					if(value.getBoolean())
					{
						description = description.concat(" has ");
					}
					else
					{
						description = description.concat(" does not have");
					}
				}
				description = description.concat(attr.getaAttribute().getAttributeName());
				if(value.isNumeric())
				{
					description = description.concat(" is " + Double.toString(value.getNumeric()));
				}
				else if(value.isString())
				{
					description = description.concat(" " + value.getString());
				}
			}
		}
		description = description.concat(".");
		System.out.println(description);
		return description;
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
	 * @return the Category object.
	 */
	public Category getaCategory() 
	{
		return aCategory;
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
