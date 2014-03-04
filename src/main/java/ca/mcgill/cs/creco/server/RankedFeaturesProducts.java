package ca.mcgill.cs.creco.server;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.creco.data.*;
import ca.mcgill.cs.creco.logic.AttributeValue;
import ca.mcgill.cs.creco.logic.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;


public class RankedFeaturesProducts {
	private final List<ScoredAttribute> aRatingList;
	private final List<ScoredAttribute> aSpecList;
	private final List<ScoredProduct> aProductSearchResult;
	
	public RankedFeaturesProducts(List<ScoredAttribute> pRatingList, List<ScoredAttribute> pSpecList, List<ScoredProduct> pProductSearchResult)
	{
		int count =0; int count2=0;
		this.aSpecList = pSpecList;
		this.aRatingList = pRatingList;
	
		List<ScoredProduct> new_set = new ArrayList();
		for(ScoredProduct p: pProductSearchResult)
		{
			count=count+1;
		}
		
		for(ScoredProduct p: pProductSearchResult)
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
						TypedValue abc = a.getTypedValue();
					//	s= attribute.getNominalValue();*/
						String s = b.toString();
						String main = new String();
						if(a.getType().equals("bool"))
						{
							boolean value = (Boolean) a.getValue();
							if(value==true)
								main="true";
							else
								main="false";
						}
						else
							main= String.valueOf(a.getValue());
					//	System.out.println(main + " : "+s);
						if(s.contains(main))
							flag=1;
					}	
				}				
			}
			if(flag==1)
			{
				count2++;
				new_set.add(p);
			//	System.out.println("Nishanth : "+product.getName() );
			}
		}
		if(count2>count/2)
			this.aProductSearchResult = new_set;
		else
			this.aProductSearchResult = pProductSearchResult;
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
