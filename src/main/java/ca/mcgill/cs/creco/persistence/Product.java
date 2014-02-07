package ca.mcgill.cs.creco.persistence;

import org.json.JSONObject;

public class Product {
	
	private String supercategory;
	private String summary;
	private String mpn;
	private String id;
	private String subfranchise;
	private String theCategory;
	private String franchise;
	private String imageLarge;
	private String imageThumbnail;
	private String displayName;
	private String name;
	private String upc;
	private String overallScoreDisplayName;
	private String modelOverviewpageUrl;
	
	private Double overallScoreMax;
	private Double overallScoreMin; 
	private Double overallScore;
	
	private Boolean isRecommended;
	private Boolean isBestSeller;
	private Boolean isTested;
	private Boolean isBestBuy;
	
	private Rating[] ratings;
	private Spec[] specs;
	
	private static String[] stringFields = 
	{
		"supercategory", "summary", "mpn", 
		"id", "subfranchise", "theCategory", "franchise", "imageLarge", 
		"imageThumbnail", "displayName", "name", "upc", 
		"overallScoreDisplayName", "modelOverviewPageUrl",
	};
	
	private static String[] doubleFields = 
	{
		"overallScoreMax", "overallScoreMin", "overallScore"
	};
				
	private static String[] boolFields = 
	{
			"isRecommended", "isBestSeller", "isTested",
			"isBestBuy"
	};
	
	public Product(JSONObject jsonProd) 
	{
		int i;
		
		// Copy all the string fields from the jsonProd to
		// the Product.
		for(i=0; i< Product.stringFields.length; i++)
		{
			String key = Product.stringFields[i];
			if(jsonProd.has(key)) {
				this.setString(key, jsonProd.getString(key));
			} else {
				this.setString(key, null);
			}
		}
		
		// Copy all the Double fields from the jsonProd to the
		// Product
		for(i=0; i< Product.doubleFields.length; i++)
		{
			String key = Product.doubleFields[i];
			if(jsonProd.has(key)) {
				this.setDouble(key, jsonProd.getDouble(key));
			} else {
				this.setDouble(key, null);
			}
		}
		
		// Copy all the Double fields from the jsonProd to the
		// Product
		for(i=0; i< Product.boolFields.length; i++)
		{
			String key = Product.boolFields[i];
			if(jsonProd.has(key)) {
				this.setBool(key, jsonProd.getBoolean(key));
			} else {
				this.setBool(key, null);
			}
		}
		
	}

	private void setString(String key, String val) 
	{
		if(key.equals("supercategory")) {
			this.supercategory = val;
		} else if(key.equals("summary")) {
			this.summary = val;
		} else if(key.equals("mpn")) {
			this.mpn = val;
		} else if(key.equals("id")) {
			this.id = val;
		} else if(key.equals("subfranchise")) {
			this.subfranchise = val;
		} else if(key.equals("theCategory")) {
			this.theCategory = val;
		} else if(key.equals("franchise")) {
			this.franchise = val;
		} else if(key.equals("imageLarge")) {
			this.imageLarge = val;
		} else if(key.equals("imageThumbnail")) {
			this.imageThumbnail = val;
		} else if(key.equals("displayName")) {
			this.displayName = val;
		} else if(key.equals("name")) {
			this.name = val;
		} else if(key.equals("upc")) {
			this.upc = val;
		} else if(key.equals("overallScoreDisplayName")) {
			this.overallScoreDisplayName = val;
		} else if(key.equals("modelOverviewpageUrl")) {
			this.modelOverviewpageUrl = val;
		}
	}
	
	public String getString(String key)
	{
		if(key.equals("supercategory")) {
			return this.supercategory;
		} else if(key.equals("summary")) {
			return this.summary;
		} else if(key.equals("mpn")) {
			return this.mpn;
		} else if(key.equals("id")) {
			return this.id;
		} else if(key.equals("subfranchise")) {
			return this.subfranchise;
		} else if(key.equals("theCategory")) {
			return this.theCategory;
		} else if(key.equals("franchise")) {
			return this.franchise;
		} else if(key.equals("imageLarge")) {
			return this.imageLarge;
		} else if(key.equals("imageThumbnail")) {
			return this.imageThumbnail;
		} else if(key.equals("displayName")) {
			return this.displayName;
		} else if(key.equals("name")) {
			return this.name;
		} else if(key.equals("upc")) {
			return this.upc;
		} else if(key.equals("overallScoreDisplayName")) {
			return this.overallScoreDisplayName;
		} else if(key.equals("modelOverviewpageUrl")) {
			return this.modelOverviewpageUrl;
		} 
		else
		{
			return null;
		}
		
	}
	
	private void setBool(String key, Boolean val)
	{
		if(key.equals("isRecommended")) {
			this.isRecommended = val;
		} else if(key.equals("isBestSeller")) {
			this.isBestSeller = val;
		} else if(key.equals("isTested")) {
			this.isTested = val;
		} else if(key.equals("isBestBuy")) {
			this.isBestBuy = val;
		}
	}
	
	private void setDouble(String key, Double val) {
		if(key.equals("overallScoreMax")) 
		{
			this.overallScoreMax = val;
		} else if(key.equals("overallScoreMin")) 
		{
			this.overallScoreMin = val;
		} else if(key.equals("overallScore")) 
		{
			this.overallScore = val;
		}
	}
		
}
