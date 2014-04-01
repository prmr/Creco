/**
 * Copyright 2014 McGill University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.mcgill.cs.creco.data;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import ca.mcgill.cs.creco.data.json.*;

/**
 * File which creates the dead_links
 * Can be accessed with the dead_links.json
 * Should be run when dead_links in the Database needs to be revisited
 * A check of how many products are present is performed at t 
 * @param <CategoryStub>
 * @param <PriceStub>
 * @param <BrandStub>
 */
public class CRDeadlinks<CategoryStub, PriceStub, BrandStub> 
{
	public static String write_to_file = new String(""); 
	private static final String DEFAULT_CATEGORY_FILENAME = "category.json";
	
	private static final String[] DEFAULT_PRODUCT_FILENAMES = 
		{
			"appliances.json", "electronicsComputers.json",
			"cars.json", "health.json", "homeGarden.json", 
			"food.json", "babiesKids.json", "money.json"
		};
	
	
	public CRDeadlinks() throws IOException, InterruptedException
	{
		this(DEFAULT_PRODUCT_FILENAMES, DEFAULT_CATEGORY_FILENAME);
	}
	
	private CRDeadlinks(String[] pProductFileNames, String pCategoryFileName) throws IOException, InterruptedException
	{
		
		write_to_file=write_to_file.concat("[");
		for(String fileName : pProductFileNames)
		{
			System.out.println("Reading file "+fileName);
			readFile(DataPath.get() + fileName);

		}
		write_to_file = write_to_file.substring(0, write_to_file.length() - 1);
		write_to_file=write_to_file.concat("]");
		System.out.println(write_to_file);
		write_to_file();
		tryreadingthejson();
	}
	
	private void write_to_file() throws IOException
	{
		 FileWriter fileWriter = null;
		File  newTextFile = new File(DataPath.get()+"dead_links.json");
		 fileWriter = new FileWriter(newTextFile);
         fileWriter.write(write_to_file);
         fileWriter.close();
	}
	
	private void tryreadingthejson() throws FileNotFoundException, IOException
	{
		InputStream in = new FileInputStream(DataPath.get()+"dead_links.json");
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		reader.beginArray();
		while(reader.hasNext()) 
		{
		
			
		}

		reader.endArray();
		reader.close();
		in.close();
	}
	
	private static void readFile(String filePath) throws IOException, InterruptedException
	{
		InputStream in = new FileInputStream(filePath);
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		reader.beginArray();
		while(reader.hasNext()) 
		{
	
			ProductStub prodStub = new Gson().fromJson(reader, ProductStub.class);
		write_to_file=write_to_file.concat("{\"product_id\":"+prodStub.id+",");
		String urltext = prodStub.modelOverviewPageUrl;
		// Write 404 error if no URL exists
		if(urltext==null)
			 {
				 write_to_file=write_to_file.concat("\"state\":"+"404"+"},");
				 continue;
			 }
		
		Thread.sleep(200);
		URL url = new URL(urltext);
		// Attempt a connection and see the resulting response code it returns
		int responseCode = ((HttpURLConnection) url.openConnection()).getResponseCode();
		write_to_file=write_to_file.concat("\"state\":"+responseCode+"},");
		}
		reader.endArray();
		reader.close();
		in.close();
	}
	
	static class responsecode
	{
		public int product_id;
		public int state;
	}
	// Enew class
	static class ProductStub 
	{
		public String summary;
		public String mpn;
		public String id;
		public String subfranchise;
		public String theCategory;
		public String franchise;
		public String imageLarge;
		public String imageThumbnail;
		public String displayName;
		public String name;
		public String upc;
		public String overallScoreDisplayName;
		public String modelOverviewPageUrl;
		public String genericColor;	
		public String review;
		public String highs;
		public String lows;
		public String bottomLine;
		public String description;
		public String dontBuyType;
		public String supercategory;
		public String subcategory;
		public Double overallScoreMax;
		public Double overallScoreMin; 
		public Double overallScore;
		public Boolean isRecommended;
		public Boolean isBestSeller;
		public Boolean isTested;
		public Boolean isBestBuy;
		public RatingStub[] ratings;
	}


	

}
