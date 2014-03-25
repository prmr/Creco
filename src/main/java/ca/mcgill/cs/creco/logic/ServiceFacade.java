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
package ca.mcgill.cs.creco.logic;

/**
 * Single point of access for all services of domain 
 * objects. Implements Fowler's Service Layer pattern.
 */
public interface ServiceFacade 
{
	/**
	 * Returns a number of possible completions for the input string.
	 * Completions are based on product categories, product names, or 
	 * product brands. 
	 * @param pInput The input text to complete.
	 * @return A comma-separated string of potential completions.
	 */
	String getCompletions(String pInput);
}
