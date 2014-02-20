package ca.mcgill.cs.creco.data;

import java.io.IOException;

public class CRData 
{
	private static CRData instance = null;
	
	private CategoryList catList;
	private ProductList prodList;
	
	private double JACCARD_THRESHHOLD = 0.8;
	private static String[] productFileNames = 
	{
		"appliances.json", "electronicsComputers.json",
		"cars.json", "health.json", "homeGarden.json", 
		"food.json", "babiesKids.json", "money.json"
	};
	private static String categoryFileName = "category.json";

	private CRData() throws IOException
	{
		String dataPath = DataPath.get();
			
		// Build the CategoryList
		catList = CategoryReader.read(dataPath, CRData.categoryFileName, JACCARD_THRESHHOLD);
		catList.eliminateSingletons();
		
		// Build the products list
		ProductList prodList = ProductReader.read(dataPath, this.getProductFileNames());
		
		// Put links from products to categories and vice-versa
		catList.associateProducts(prodList);

		// Roll up useful pre-processed statistics and find equivalence classes
		catList.refresh();
		catList.findEquivalenceClasses();
	}
	
	private CRData(String[] productFileNames, String categoryFileName) throws IOException
	{
		String dataPath = DataPath.get();
		
		// Build the CategoryList
		catList = CategoryReader.read(dataPath, categoryFileName, JACCARD_THRESHHOLD);
		catList.eliminateSingletons();
		
		// Build the products list
		ProductList prodList = ProductReader.read(dataPath, productFileNames);
		
		// Put links from products to categories and vice-versa
		catList.associateProducts(prodList);
		
		// Roll up useful pre-processed statistics and find equivalence classes
		catList.refresh();
		catList.findEquivalenceClasses();		
	}
	
	public static CRData getData() throws IOException
	{
		if (instance == null)
		{
			instance = new CRData(CRData.productFileNames, CRData.categoryFileName);
		}
		return instance;
	}
	
	public static CRData setupWithFileNames(String[] productFileNames, String categoryFileName) throws IOException 
	{
		if (instance == null)
		{
			instance = new CRData(productFileNames, categoryFileName);
		} else
		{
			throw new IOException("CR Database was already initialized. Use CRData.getData() instead.");
		}
		return instance;
		
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
