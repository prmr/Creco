package ca.mcgill.cs.creco.server;

import java.util.List;

import ca.mcgill.cs.creco.web.model.search.ProductSearchResult;
import ca.mcgill.cs.creco.logic.model.ScoredAttribute;


public class RankedFeaturesProducts {
	private final List<ScoredAttribute> aRatingList;
	private final List<ScoredAttribute> aSpecList;
	private final ProductSearchResult aProductSearchResult;
	
	public RankedFeaturesProducts(List<ScoredAttribute> pRatingList, List<ScoredAttribute> pSpecList, ProductSearchResult pProductSearchResult)
	{
		this.aSpecList = pSpecList;
		this.aRatingList = pRatingList;
		this.aProductSearchResult = pProductSearchResult;
	}
}
