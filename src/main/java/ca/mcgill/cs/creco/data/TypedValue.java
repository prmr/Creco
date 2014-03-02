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
 * Represents a value object from which a type has been inferred.
 */
public class TypedValue 
{
	private Type aType;
	private Object aValue;
	private Object aOriginalValue;
	
	/**
	 * The different types a typed value can take.
	 */
	public enum Type 
	{ NULL, INTEGER, DOUBLE, BOOLEAN, STRING, UNKNOWN }
	
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
				aValue = Integer.parseInt(theString);
			}
			//match a number with optional '-' and decimal.
			else if(theString.matches("-?\\d+(\\.\\d+)?"))  
			{
				aType = Type.DOUBLE;
				aValue = Double.parseDouble(theString);
			}
			else if(theString.matches("(y|Y)es"))
			{
				aType = Type.BOOLEAN;
				aValue = true;
			}
			else if(theString.matches("(n|N)o"))
			{
				aType = Type.BOOLEAN;
				aValue = false;
			}
			else
			{
				aType = Type.STRING;
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
	 * @return The value after type inference.
	 */
	public Object getValue()
	{
		return aValue;
	}
	
	/**
	 * @return The value before type inference.
	 */
	public Object getOriginalValue()
	{
		return aOriginalValue;
	}	
}

