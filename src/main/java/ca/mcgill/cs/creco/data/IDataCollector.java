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

/**
 * Any object that can accept Category and Product objects.
 */
public interface IDataCollector 
{
	/**
	 * Adds a category to the collector.
	 * @param pCategory The category to add.
	 */
	void addCategory(Category pCategory);
	
	/**
	 * Adds a product to the collector.
	 * @param pProduct The product to add.
	 */
	void addProduct(Product pProduct);
}
