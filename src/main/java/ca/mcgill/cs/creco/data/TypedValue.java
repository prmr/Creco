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

import java.util.List;

/**
 * Represents an immutable value object from which a type has been inferred.
 * Presently stores min and max or possible string values if necessary.
 * Should probably be handled somewhere else.
 */
public class TypedValue 
{
	private final Type aType;
	private Object aValue;
	private double aNumericValue;
	private String aNominalValue;
	private boolean aBooleanValue;
	private final Object aOriginalValue;
	
//	private double aMin;
//	private double aMax;
//	private List<String> aDict;
	
	/**
	 * The different types a typed value can take.
	 */
	public enum Type 
	{ NULL, INTEGER, DOUBLE, BOOLEAN, STRING, UNKNOWN }
	
//	/**
//	 * Creates a new value object an infers its type. Also stores min and max
//	 * values found in data
//	 * @param pValue The value.
//	 * @param pMin minimum value the attribute takes in data
//	 * @param pMax maximum value the attribute takes in data
//	 */
//	public TypedValue(Object pValue, double pMin, double pMax)
//	{
//		this(pValue);
//		aMin = pMin;
//		aMax = pMax;
//		
//	}
//	/**
//	 * Creates a new value object an infers its type. Also stores 
//	 * possible values for this item
//	 * @param pValue The value.
//	 * @param pDict list of values found in data
//	 */
//	public TypedValue(Object pValue, List<String> pDict)
//	{
//		this(pValue);
//		aDict = pDict;		
//	}
	/**
	 * Creates a new value object an infers its type.
	 * @param pValue The value.
	 */
	public TypedValue(Object pValue)
	{
		aOriginalValue = pValue;
		aValue = pValue;
		
		if(pValue == null)
		{
			aType = Type.NULL;
		}
		else if(pValue instanceof Integer)
		{
			aType = Type.INTEGER;
		}
		else if(pValue instanceof Double || pValue instanceof Float)
		{
			aType = Type.DOUBLE;
		}
		else if(pValue instanceof Boolean)
		{
			aType = Type.BOOLEAN;
		}
		else if(pValue instanceof String)
		{
			String theString = (String) pValue; 
			// match int with optional '-' and decimal.
			if(theString.matches("-?\\d+"))
			{
				aType = Type.INTEGER;
				aNumericValue = Integer.parseInt(theString);
				aValue = Integer.parseInt(theString);
			}
			//match a number with optional '-' and decimal.
			else if(theString.matches("-?\\d+(\\.\\d+)?"))  
			{
				aType = Type.DOUBLE;
				aNumericValue = Double.parseDouble(theString);
				aValue = Double.parseDouble(theString);
			}
			else if(theString.matches("(y|Y)es"))
			{
				aType = Type.BOOLEAN;
				aBooleanValue = true;
				aValue = true;
			}
			else if(theString.matches("(n|N)o"))
			{
				aType = Type.BOOLEAN;
				aBooleanValue = false;
				aValue = false;
			}
			else
			{
				aType = Type.STRING;
				aNominalValue = theString;
				aValue = theString;
			}
		}
		else
		{
			aType = Type.UNKNOWN;
		}
	}	
	
	/**
	 * @return The inferred type of this value.
	 */
	public Type getType() 
	{
		return aType;
	}
	
	/**
	 * @Deprecated use method for specific type instead.
	 * @return The value after type inference.
	 */
	public Object getValue()
	{
		return aValue;
	}
	/**
	 * @return The numeric value after type inference.
	 */
	public double getNumericValue()
	{
		return aNumericValue;
	}
	/**
	 * @return The boolean value after type inference.
	 */
	public boolean getBooleanValue()
	{
		return aBooleanValue;
	}
	/**
	 * @return The nominal value after type inference.
	 */
	public String getNominalValue()
	{
		return aNominalValue;
	}
	
	/**
	 * @return The value before type inference.
	 */
	public Object getOriginalValue()
	{
		return aOriginalValue;
	}
	
//	/**
//	 * @return minimum value recorded in data
//	 */
//	public double getMin() 
//	{
//		return aMin;
//	}
//	/**
//	 * @return maximum value recorded in data
//	 */
//	public double getMax() 
//	{
//		return aMax;
//	}
//	/**
//	 * @return list of string values recorded in data
//	 */
//	public List<String> getDict() 
//	{
//		return aDict;
//	}	
//	
	
}

