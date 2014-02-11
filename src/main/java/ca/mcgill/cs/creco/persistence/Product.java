package ca.mcgill.cs.creco.persistence;

import java.util.ArrayList;
import com.google.gson.*;

public class Product {
	
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
	private String modelOverviewPageUrl;
	private String genericColor;
	
	// fields from json don't map directly onto these variables;
	// they are extracted using Product.refresh()
	private String brandId;
	private String brandName;
	private String categoryId;
	
	//private String vendorApiChannels;
	//groups
	
	private String review;
	private String highs;
	private String lows;
	private String bottomLine;
	private String description;
	private String dontBuyType;
	private String supercategory;
	private String subcategory;
	
	private Double overallScoreMax;
	private Double overallScoreMin; 
	private Double overallScore;
	
	private Boolean isRecommended;
	private Boolean isBestSeller;
	private Boolean isTested;
	private Boolean isBestBuy;
	
	private Rating[] ratings = new Rating[0];
	private Spec[] specs = new Spec[0];
	private Category category;
	private Category catRef;
	private Brand brand;
	
	private Price price;
	
	public static String[] stringFields = 
	{
		"supercategory", "subcategory", "summary", "mpn", "highs", "description",
		"id", "subfranchise", "theCategory", "franchise", "imageLarge", "dontBuyType",
		"imageThumbnail", "displayName", "name", "upc", "rewiew", "bottomLine", "lows",
		"overallScoreDisplayName", "modelOverviewPageUrl", "categoryId", "genericColor",
		"brandId", "brandName"
	};
	
	public static String[] doubleFields = 
	{
		"overallScoreMax", "overallScoreMin", "overallScore"
	};
				
	public static String[] boolFields = 
	{
			"isRecommended", "isBestSeller", "isTested",
			"isBestBuy"
	};
	
	public String getDisplayName()
	{
		return this.displayName;
	}
	
	public Rating[] getRatings()
	{
		return this.ratings;
	}
	
	public Spec[] getSpecs()
	{
		return this.specs;
	}
	
	public Category getCategory() 
	{
		return this.catRef;
	}
	
	public void setCategory(Category cat)
	{
		this.catRef = cat;
	}
	
	public String dump() 
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	public Double getPrice() {
		return this.price.getPrice();
	}
	
	public String getCategoryId() {
		return this.categoryId;
	}
	
	public String getId() 
	{
		return this.id;
	}
	
	public void refresh()
	{
		this.categoryId = this.category.getString("id");
		this.category = null;
		
		if(this.ratings == null)
		{
			this.ratings = new Rating[0];
		}
		
		if(this.specs == null)
		{
			this.specs = new Spec[0];
		}
		
		if(this.brand != null)
		{
			this.brandId = this.brand.getString("id");
			this.brandName = this.brand.getString("name");
		}
		else
		{
			this.brandId = null;
			this.brandName = null;			
		}
	}
	
	public String getString(String key)
	{
		if(key.equals("supercategory")) 
		{
			return this.supercategory;
		} 
		else if(key.equals("summary")) 
		{
			return this.summary;
		} 
		else if(key.equals("mpn")) 
		{
			return this.mpn;
		} 
		else if(key.equals("id")) 
		{
			return this.id;
		} 
		else if(key.equals("subfranchise")) 
		{
			return this.subfranchise;
		} 
		else if(key.equals("theCategory")) 
		{
			return this.theCategory;
		} 
		else if(key.equals("franchise")) 
		{
			return this.franchise;
		} 
		else if(key.equals("imageLarge")) 
		{
			return this.imageLarge;
		} 
		else if(key.equals("imageThumbnail")) 
		{
			return this.imageThumbnail;
		} 
		else if(key.equals("displayName")) 
		{
			return this.displayName;
		} 
		else if(key.equals("name")) 
		{
			return this.name;
		} 
		else if(key.equals("upc")) 
		{
			return this.upc;
		} 
		else if(key.equals("overallScoreDisplayName")) 
		{
			return this.overallScoreDisplayName;
		}
		else if(key.equals("modelOverviewPageUrl")) 
		{
			return this.modelOverviewPageUrl;
		} 
		else
		{
			return null;
		}
		
	}
		
	public Boolean getBool(String key)
	{
		if(key.equals("isRecommended")) 
		{
			return this.isRecommended;
		} 
		else if(key.equals("isBestSeller")) 
		{
			return this.isBestSeller;
		} 
		else if(key.equals("isTested")) 
		{
			return this.isTested;
		} 
		else if(key.equals("isBestBuy")) 
		{
			return this.isBestBuy;
		}
		else 
		{
			return null;
		}
	}
		
	public Double getDouble(String key) {
		if(key.equals("overallScoreMax")) 
		{
			return this.overallScoreMax;
		} 
		else if(key.equals("overallScoreMin")) 
		{
			return this.overallScoreMin;
		} 
		else if(key.equals("overallScore")) 
		{
			return this.overallScore;
		}
		else
		{
			return null;
		}
	}
		
}
