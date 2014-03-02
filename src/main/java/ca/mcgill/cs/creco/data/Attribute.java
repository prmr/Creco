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

import ca.mcgill.cs.creco.data.json.AttributeStub;

/**
 * Represents an attribute of a product.
 */
public class Attribute 
{
	private String displayName;
	private String description;
	private String attributeId;
	private String filterWidget;
	private String dataPresentationFormat;
	private String attributeGroup;
	private String unitName;
	private TypedVal typedValue;
	private Integer sortOrder;
	private Boolean isForDisplayOnCRO;
	private Boolean isCategoryCommonAttribute;
	
	Attribute(AttributeStub attStub)
	{
		this.displayName = attStub.displayName;
		this.description = attStub.description;
		this.attributeId = attStub.attributeId;
		this.filterWidget = attStub.filterWidget;
		this.dataPresentationFormat = attStub.dataPresentationFormat;
		this.attributeGroup = attStub.attributeGroup; 
		this.unitName = attStub.unitName; 
		this.sortOrder = attStub.sortOrder;  
		this.isForDisplayOnCRO = attStub.isForDisplayOnCRO; 
		this.isCategoryCommonAttribute = attStub.isCategoryCommonAttribute;

		this.typedValue = new TypedVal(attStub.value);
	}

	/**
	 * @return The display name of the attribute.
	 */
	public String getName() 
	{ return displayName; }

	/**
	 * @return The description of the attribute.
	 */
	public String getDescription() 
	{ return description; }

	/**
	 * @return The id of this attribute.
	 */
	public String getId() 
	{ return attributeId; }

	/**
	 * @return The filter widget.
	 */
	public String getFilterWidget() 
	{ return filterWidget; }

	public String getDataPresentationFormat() { return dataPresentationFormat; }

	public String getAttributeGroup() { return attributeGroup; }

	public String getUnitName() { return unitName; }

	public Object getValue() { return this.typedValue.getValue(); }
	
	public TypedVal getTypedValue() { return this.typedValue; }

	public Object getOriginalValue() { return this.typedValue.getOriginalValue(); }
	
	public String getType() { return this.typedValue.getType(); }

	public Integer getSortOrder() { return sortOrder; }

	public Boolean getIsForDisplayOnCRO() { return isForDisplayOnCRO; }

	public Boolean getIsCategoryCommonAttribute() { return isCategoryCommonAttribute; }	
}
