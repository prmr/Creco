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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ca.mcgill.cs.creco.data.json.RatingStub;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * File which creates the dead_links.
 * Can be accessed with the dead_links.json
 * Should be run when dead_links in the Database needs to be revisited.
 * A check of how many products are present is performed at t.
 * @param <CategoryStub>
 * @param <PriceStub>
 * @param <BrandStub>
 */
public class CRDeadlinks<CategoryStub, PriceStub, BrandStub> 
{
	public static String writeToFile = new String("");
	private static final String DEFAULT_CATEGORY_FILENAME = "category.json";
	private static final int SLEEP = 200;
	private static final String[] DEFAULT_PRODUCT_FILENAMES = 
		{
			"appliances.json", "electronicsComputers.json",
			"cars.json", "health.json", "homeGarden.json", 
			"food.json", "babiesKids.json", "money.json"
		};
	
	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public CRDeadlinks() throws IOException, InterruptedException
	{
		this(DEFAULT_PRODUCT_FILENAMES, DEFAULT_CATEGORY_FILENAME);
	}
	
	private CRDeadlinks(String[] pProductFileNames, String pCategoryFileName) throws IOException, InterruptedException
	{
		
		writeToFile = writeToFile.concat("[");
		for(String fileName : pProductFileNames)
		{
			System.out.println("Reading file "+fileName);
			readFile(DataPath.get() + fileName);

		}
		writeToFile = writeToFile.substring(0, writeToFile.length() - 1);
		writeToFile = writeToFile.concat("]");
		System.out.println(writeToFile);
		writeToFile();
		tryreadingthejson();
	}
	
	private void writeToFile() throws IOException
	{
		 FileWriter fileWriter = null;
		File  newTextFile = new File(DataPath.get()+"dead_links.json");
		 fileWriter = new FileWriter(newTextFile);
         fileWriter.write(writeToFile);
         fileWriter.close();
	}
	
	private void tryreadingthejson() throws IOException
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
	
	private static void readFile(String pFilePath) throws IOException, InterruptedException
	{
		InputStream in = new FileInputStream(pFilePath);
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		reader.beginArray();
		while(reader.hasNext()) 
		{
	
			ProductStub prodStub = new Gson().fromJson(reader, ProductStub.class);
		writeToFile = writeToFile.concat("{\"product_id\":"+prodStub.id+",");
		String urltext = prodStub.modelOverviewPageUrl;
		// Write 404 error if no URL exists
		if(urltext==null)
			 {
				 writeToFile = writeToFile.concat("\"state\":"+"404"+"},");
				 continue;
			 }
		
		Thread.sleep(SLEEP);
		URL url = new URL(urltext);
		// Attempt a connection and see the resulting response code it returns
		int responseCode = ((HttpURLConnection) url.openConnection()).getResponseCode();
		writeToFile = writeToFile.concat("\"state\":"+responseCode+"},");
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
