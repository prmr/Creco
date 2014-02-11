package ca.mcgill.cs.creco.persistence;

import java.io.IOException;

public class CRData 
{
	
	private CategoryList catList;
	private ProductList prodList;
	
	private double JACCARD_THRESHHOLD = 0.8;
	private static String[] productFileNames = 
	{
		"product_appliances.json", "product_electronicsComputers.json",
		"product_cars.json", "product_health.json", "product_homeGarden.json", 
		//"product_food.json", "product_babiesKids.json", "product_money.json"
	};

	public CRData(String dataPath) throws IOException
	{
		// Build the CategoryList
		this.catList = CategoryReader.read(dataPath, JACCARD_THRESHHOLD);
		catList.eliminateSingletons();
		
		// Build the products list
		ProductList prodList = ProductReader.read(dataPath, this.getProductFileNames());
		
		// Put links from products to categories and vice-versa
		catList.associateProducts(prodList);
		
		// Roll up useful pre-processed statistics and find equivalence classes
		catList.refresh();
		catList.findEquivalenceClasses();		
	}
	
	public CRData(String dataPath, String[] productFileNames) throws IOException 
	{
		// Build the CategoryList
		this.catList = CategoryReader.read(dataPath, JACCARD_THRESHHOLD);
		catList.eliminateSingletons();
		
		// Build the products list
		ProductList prodList = ProductReader.read(dataPath, productFileNames);
		
		// Put links from products to categories and vice-versa
		catList.associateProducts(prodList);
		
		// Roll up useful pre-processed statistics and find equivalence classes
		catList.refresh();
		catList.findEquivalenceClasses();		
	}
	
	public String[] getProductFileNames()
	{
		return CRData.productFileNames;
	}
	
	public CategoryList getCategoryList()
	{
		return this.catList;
	}
	
	public ProductList getProductList()
	{
		return this.prodList;
	}
	
}
