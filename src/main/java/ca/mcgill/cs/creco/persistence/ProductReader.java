package ca.mcgill.cs.creco.persistence;

import java.io.FileReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONTokener;

public class ProductReader {
	private static String[] productFiles = 
	{
		"product_appliances.json", "product_babiesKids.json", 
		"product_cars.json", "product_electronicsComputers.json", "product_food.json", 
		"product_health.json", "product_homeGarden.json", "product_money.json"
	};
	
	private static String[] ignoredProductFiles = 
	{
		"product_babiesKids.json", "product_food.json", "product_money.json"
	};
	
	public static ProductList read(String dataPath) throws IOException {
		
		FileReader reader;
		int i;
	
		// Make an empty CategoryList
		ProductList prodList = new ProductList();
		
		for(i=0; i<ProductReader.productFiles.length; i++) {
			String fname = ProductReader.productFiles[i];
			
			// Show progress
			System.out.println(dataPath + fname);
			
			reader = new FileReader(dataPath + fname);
			JSONTokener tokener = new JSONTokener(reader);
			JSONArray prods = new JSONArray(tokener);
			
			
			int j;
			for(j=0; j<prods.length(); j++) 
			{
				
				Product newProd = new Product(prods.getJSONObject(j));
				String id = newProd.getString("id");
				prodList.put(id, newProd);
				
				// Show progress
				System.out.println(newProd.getString("displayName"));
			}
		}
		
		return prodList;
	}
}
