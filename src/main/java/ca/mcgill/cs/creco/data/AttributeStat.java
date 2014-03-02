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

import ca.mcgill.cs.creco.data.TypedValue.Type;

/**
 * Represents statistics about an product attribute.
 */
public class AttributeStat 
{
	private int aCount = 0;
	private Number aValueMin;
	private Number aValueMax;
	private HashSet<String> aValueEnum = new HashSet<String>();
	private Attribute aAttribute;

	/**
	 * Creates an empty statistics object for this attribute.
	 * @param pAttribute The attribute about which statistics are collected.
	 */
	public AttributeStat(Attribute pAttribute) 
	{
		aAttribute = pAttribute;
	}

	/**
	 * Copy constructor.
	 * @param pStats The stats to copy.
	 */
	public AttributeStat(AttributeStat pStats) 
	{
		aAttribute = pStats.getAttribute();
		aValueEnum.addAll(pStats.getValueEnum());
		aValueMax = pStats.aValueMax;
		aValueMin = pStats.aValueMin;
		aCount = pStats.getCount();
	}
	
	/**
	 * Update this stat object to take into account the 
	 * values in pStat.
	 * @param pStatistics The statistics to integrate into this object.
	 */
	public void update(AttributeStat pStatistics)
	{
		increment(pStatistics.getCount());
		aValueEnum.addAll(pStatistics.getValueEnum());
		updateRange(pStatistics.getValueMax());
		updateRange(pStatistics.getValueMin());
	}

	/**
	 * Update this statistics object to take into account
	 * the values from a single attribute.
	 * @param pAttribute The attribute to consider.
	 */
	public void update(Attribute pAttribute)
	{
		increment(1);
		updateRange(pAttribute.getTypedValue());
	}
	
	/**
	 * @return The attribute described by this statistics object.
	 */
	public Attribute getAttribute()
	{
		return aAttribute;
	}

	/**
	 * Increment the number of attributes described by this statistics
	 * by pAmount.
	 * @param pAmount The amount to increment by.
	 */
	public void increment(int pAmount)
	{
		aCount += pAmount;
	}
	
	/**
	 * @return The number of attributes described by this statistics.
	 */
	public int getCount()
	{
		return aCount;
	}

	/**
	 * @return The maximal value for this statistics.
	 */
	public Object getValueMax()
	{
		return aValueMax;
	}
	
	/**
	 * @return The minimal value for this statistics.
	 */
	public Object getValueMin()
	{
		return aValueMin;
	}
	
	/**
	 * @return The enumerated values for this statistics.
	 */
	public Set<String> getValueEnum()
	{
		if(aValueEnum != null)
		{
			return Collections.unmodifiableSet(aValueEnum);
		}
		return null;
	}
	
	private void updateRange(TypedValue pTypedValue)
	{
		TypedValue.Type type = pTypedValue.getType();
		Object value = pTypedValue.getValue();
		
		if(type.equals(Type.INTEGER) || type.equals(Type.DOUBLE)) 
		{
			Number number = (Number) value;
			if(aValueMin == null || number.doubleValue() < aValueMin.doubleValue())
			{
				aValueMin = number;
			}
			
			if(aValueMax == null || number.doubleValue() > aValueMax.doubleValue())
			{
				aValueMax = number;
			}
		}
		else if(type.equals(Type.STRING) || type.equals(Type.BOOLEAN))
		{
			aValueEnum.add((String) ""+value);
		}		
	}
	
	void updateRange(Object pValue)
	{
		TypedValue typedValue = new TypedValue(pValue);
		updateRange(typedValue);
	}
	
}