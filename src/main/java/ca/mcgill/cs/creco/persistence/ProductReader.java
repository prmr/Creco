package ca.mcgill.cs.creco.persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class ProductReader {
	
	public static ProductList read(String dataPath, String[] productFiles) throws IOException {

		// Make an empty ProducList
		ProductList prodList = new ProductList();
		
		System.out.println("Reading product files...");
		for(String fname : productFiles)
		{
			String filePath = dataPath + fname; 
			System.out.println(filePath);
			ProductReader.readFile(filePath, prodList);
		}
		System.out.println("Found " + prodList.size() + " products.\n");
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
