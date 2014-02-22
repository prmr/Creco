package ca.mcgill.cs.creco.server;

import java.util.List;

import ca.mcgill.cs.creco.logic.model.ScoredAttribute;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;


public class RankedFeaturesProducts {
	private final List<ScoredAttribute> aRatingList;
	private final List<ScoredAttribute> aSpecList;
	private final List<ScoredProduct> aProductSearchResult;
	
	public RankedFeaturesProducts(List<ScoredAttribute> pRatingList, List<ScoredAttribute> pSpecList, List<ScoredProduct> pProductSearchResult)
	{
		this.aSpecList = pSpecList;
		this.aRatingList = pRatingList;
		this.aProductSearchResult = pProductSearchResult;
	}
}
