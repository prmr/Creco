package ca.mcgill.cs.creco.persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class ProductReader {
	private static String[] productFiles = 
	{
		"product_appliances.json", "product_electronicsComputers.json",
		"product_cars.json", "product_health.json", "product_homeGarden.json", 
		//"product_food.json", "product_babiesKids.json", "product_money.json"
	};
	
	public static ProductList read(String dataPath) throws IOException {

		// Make an empty ProducList
		ProductList prodList = new ProductList();
		
		for(String fname : ProductReader.productFiles)
		{
			String filePath = dataPath + fname; 
			System.out.println(filePath);
			ProductReader.readFile(filePath, prodList);
		}
		System.out.println("Found " + prodList.size() + " products.");
		return prodList;
	}
	
	private static void readFile(String filePath, ProductList prodList) throws IOException
	{
		InputStream in = new FileInputStream(filePath);
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		Gson gson = new Gson();
		
		reader.beginArray();
		while(reader.hasNext()) 
		{
			Product prod = gson.fromJson(reader, Product.class);
			prod.refresh();
			prodList.put(prod.getId(), prod);
			//System.out.println('\t' + prod.getString("displayName"));
		}
		reader.endArray();
		reader.close();
		in.close();
	}
	
}
