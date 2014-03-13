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
 * Represents an immutable value object from which a type has been inferred.
 */
public class TypedValue 
{
	private static final String TOKEN_NA = "NA";
	
	private final Type aType;
	private Object aValue;
	
	private boolean aBooleanValue;
	private String aNominalValue;
	private double aNumericValue;
	
	/**
	 * The different types a typed value can take.
	 */
	public enum Type 
	{ NULL, INTEGER, DOUBLE, BOOLEAN, STRING, NA, UNKNOWN }
	
	/**
	 * Creates a value of type "NA", meaning that the object
	 * will represent an unavailable value. getType() will return 
	 * Type.NA, and getValue() will return "NA".
	 */
	public TypedValue()
	{
		this("NA");
	}
	
	/**
	 * Creates a new value object an infers its type.
	 * @param pValue The value.
	 */
	public TypedValue(Object pValue)
	{
		aValue = pValue;
		
		if(pValue == null)
		{
			aType = Type.NULL;
		}
		else if(pValue instanceof Integer)
		{
			aType = Type.INTEGER;
			aNumericValue = ((Integer) pValue).doubleValue();
		}
		else if(pValue instanceof Double )
		{
			aType = Type.DOUBLE;
			aNumericValue = (Double) pValue;
		}
		else if( pValue instanceof Float)
		{
			aType = Type.DOUBLE;
			aNumericValue = ((Float) pValue).doubleValue();
		}
		else if(pValue instanceof Boolean)
		{
			aType = Type.BOOLEAN;
			aBooleanValue = (Boolean) pValue;
		}
		else if(pValue instanceof String)
		{
			String theString = (String) pValue; 
			// match int with optional '-' and decimal.
			if(theString.matches("-?\\d+"))
			{
				aType = Type.INTEGER;
				aValue = Double.parseDouble(theString);
				aNumericValue = Double.parseDouble(theString);

			}
			else if(theString.matches("^(n|N)/?(a|A)$"))
			{
				// Matches an not available indicator
				aType = Type.NA;
				aValue = TOKEN_NA;
				aNominalValue = TOKEN_NA;
				
			}
			//match a number with optional '-' and decimal.
			else if(theString.matches("-?\\d+(\\.\\d+)?"))  
			{
				aType = Type.DOUBLE;
				aNumericValue = Double.parseDouble(theString);
				aValue = Double.parseDouble(theString);
				aNumericValue = Double.parseDouble(theString);
			}
			else if(theString.matches("(y|Y)es"))
			{
				aType = Type.BOOLEAN;
				aBooleanValue = true;
				aValue = true;
				aBooleanValue = true;
			}
			else if(theString.matches("(n|N)o"))
			{
				aType = Type.BOOLEAN;
				aBooleanValue = false;
				aValue = false;
				aBooleanValue = false;
			}
			else
			{
				aType = Type.STRING;
				aNominalValue = theString;
				aValue = theString;
				aNominalValue = theString;
			}
		}
		else
		{
			aType = Type.UNKNOWN;
		}
	}	
	
	/**
	 * @return True if and only if this object represents
	 * a value of type Type.NA.
	 */
	public boolean isNA()
	{
		return aType == Type.NA;
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
	
	@Override
	@Deprecated
	public String toString()
	{
		if(aType == Type.BOOLEAN)
		{
			return aBooleanValue +"";
		}
		if(aType == Type.DOUBLE || aType == Type.INTEGER)
		{
			return aNumericValue +"";
		}
		return aNominalValue;
	}

}

