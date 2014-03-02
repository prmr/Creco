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

import java.io.IOException;

/**
 * Loads the product data from its persisted representation.
 */
public interface IDataLoadingService 
{
	/**
	 * @param pCollector The object collecting the loaded categories.
	 * @throws IOException If there is any error retrieving the data.
	 */
	void loadCategories(IDataCollector pCollector) throws IOException;
	
	/**
	 * @param pCollector The object collecting the loaded products.
	 * @throws IOException If there is any error retrieving the data.
	 */
	void loadProducts(IDataCollector pCollector) throws IOException;
}
