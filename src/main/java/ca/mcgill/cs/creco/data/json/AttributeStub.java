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
package ca.mcgill.cs.creco.data.json;

/**
 * This class is designed to capture data from the CR database for attributes (ratings and specs) which are 
 * properties of products.  It is a superclass for RatingStub and SpecStub.
 * 
 * Reflectively loaded by the GSON library, do not change the field names.
 * 
 * @author enewe101
 *
 */
public class AttributeStub 
{
	public String displayName;
	public String description;
	public String attributeId;
	public String filterWidget;
	public String dataPresentationFormat;
	public String attributeGroup;
	public String unitName;
	public Object value;
	public Integer sortOrder;
	public Boolean isForDisplayOnCRO;
	public Boolean isCategoryCommonAttribute;
}
