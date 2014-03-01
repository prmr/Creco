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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ca.mcgill.cs.creco.data.json.ProductStub;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class ProductReader 
{
	
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
			ProductStub prodStub = gson.fromJson(reader, ProductStub.class);
			prodList.put(prodStub.id, new Product(prodStub));
			//System.out.println('\t' + prod.getString("displayName"));
		}
		reader.endArray();
		reader.close();
		in.close();
	}
	
}
