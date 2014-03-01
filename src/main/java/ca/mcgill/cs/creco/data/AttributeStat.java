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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AttributeStat 
{
	private int count;
	private Number valueMin;
	private Number valueMax;
	private HashSet<String> valueEnum;
	private Attribute attribute;

	public AttributeStat(Attribute attribute) 
	{
		this.attribute = attribute;
		this.valueEnum = new HashSet<String>();
		this.count = 0;
	}

	public AttributeStat(AttributeStat attribute) 
	{
		this.attribute = attribute.getAttribute();
		this.valueEnum = new HashSet<String>();
		this.valueEnum.addAll(attribute.getValueEnum());
		this.valueMax = attribute.valueMax;
		this.valueMin = attribute.valueMin;
		this.count = attribute.getCount();
	}
	
	public void update(AttributeStat attribute)
	{
		this.increment(attribute.getCount());
		this.valueEnum.addAll(attribute.getValueEnum());
		this.updateRange(attribute.getValueMax());
		this.updateRange(attribute.getValueMin());
	}

	public void update(Attribute attribute)
	{
		this.increment(1);
		this.updateRange(attribute.getTypedValue());
	}

	public String getName()
	{
		return this.attribute.getName();
	}
	
	public String getDescription()
	{
		return this.attribute.getDescription();               
	}
	
	public String getFilterWidget()
	{
		return this.attribute.getFilterWidget();              
	}
	
	public String getDataPresentationFormat()
	{
		return this.attribute.getDataPresentationFormat();    
	}
	
	public String getAttributeGroup()
	{
		return this.attribute.getAttributeGroup();            
	}
	
	public String getUnitName()
	{
		return this.attribute.getUnitName();
	}
	
	public int getSortOrder()
	{
		return this.attribute.getSortOrder();
	}
	
	public boolean getIsForDisplayOnCRO()
	{
		return this.attribute.getIsForDisplayOnCRO();
	}
	
	public boolean getIsCategoryCommonAttribute()
	{
		return this.attribute.getIsCategoryCommonAttribute();
	}
	
	public String getId() {
		return this.attribute.getId();
	}
	
	public void increment(int add)
	{
		this.count += add;
	}
	
	public int getCount()
	{
		return this.count;
	}

	public Object getValueMax()
	{
		return this.valueMax;
	}
	
	public Object getValueMin()
	{
		return this.valueMin;
	}
	
	public Set<String> getValueEnum()
	{
		if(this.valueEnum != null)
		{
			return Collections.unmodifiableSet(this.valueEnum);
		}
		return null;
	}
	
	public Attribute getAttribute()
	{
		return this.attribute;
	}
	
	void updateRange(TypedVal typedValue)
	{
		String type = typedValue.getType();
		Object value = typedValue.getValue();
		if(type.equals("int") || type.equals("float") || type.equals("double")) {
			Number number = (Number) value;
			if(this.valueMin == null || number.doubleValue() < this.valueMin.doubleValue())
			{
				this.valueMin = number;
			}
			if(this.valueMax == null || number.doubleValue() > this.valueMax.doubleValue())
			{
				this.valueMax = number;
			}
		}
		else if(type.equals("String") || type.equals("boolean"))
		{
			this.valueEnum.add((String) ""+value);
		}		
	}
	
	void updateRange(Object value)
	{
		TypedVal typedValue = new TypedVal(value);
		this.updateRange(typedValue);
	}
	
}