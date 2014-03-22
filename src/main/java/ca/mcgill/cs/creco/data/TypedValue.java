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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents an immutable value object from which a type has been inferred.
 * The value is inferred to be of one of the following types is inferred:
 * <ul>
 * <li>NULL: The null type represents null objects.</li>
 * <li>NUMERIC: Including integers and floating-point types.</li>
 * <li>BOOLEAN: Represents any binomial type of value, including true and false and yes/no.</li>
 * <li>NA: Represents a missing value.
 * <li>STRING: Any kind of string.
 * </ul>
 * 
 * The value wrapped by this type can only be accessed by the proper specialized accessor. For example,
 * it is only possible to access a numeric value using asNumeric(). Calling an as... method on
 * a value of the wrong type will raise an exception. For example, calling asString() on a value of type
 * numeric is not possible. To convert numeric values to string, use a formatter or a Double object.
 * 
 * For this reason, calls to obtain a value should always be preceded by calls to get... methods.
 * 
 * Note that the NULL and NA types do not have a corresponding value.
 */
public class TypedValue 
{
	private final Type aType;
	
	private boolean aBooleanValue;
	private String aStringValue;
	private double aNumericValue;
	
	/**
	 * The different types a typed value can take.
	 */
	private enum Type 
	{ NULL, NA, BOOLEAN, NUMERIC, STRING }
	
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
		if(pValue == null)
		{
			aType = Type.NULL;
		}
		else if(pValue instanceof Integer)
		{
			aType = Type.NUMERIC;
			aNumericValue = ((Integer) pValue).doubleValue();
		}
		else if(pValue instanceof Double )
		{
			aType = Type.NUMERIC;
			aNumericValue = ((Double) pValue).doubleValue();
		}
		else if( pValue instanceof Float)
		{
			aType = Type.NUMERIC;
			aNumericValue = ((Float) pValue).doubleValue();
		}
		else if(pValue instanceof Boolean)
		{
			aType = Type.BOOLEAN;
			aBooleanValue = ((Boolean) pValue).booleanValue();
		}
		else if(pValue instanceof String)
		{
			String theString = (String) pValue; 
			// match int with optional '-' and decimal.
			if(theString.matches("-?\\d+"))
			{
				aType = Type.NUMERIC;
				aNumericValue = Double.parseDouble(theString);

			}
			else if(theString.matches("^(n|N)/?(a|A)$"))
			{
				// Matches an not available indicator
				aType = Type.NA;
			}
			//match a number with optional '-' and decimal.
			else if(theString.matches("-?\\d+(\\.\\d+)?"))  
			{
				aType = Type.NUMERIC;
				aNumericValue = Double.parseDouble(theString);
			}
			else if(theString.matches("(y|Y)es"))
			{
				aType = Type.BOOLEAN;
				aBooleanValue = true;
			}
			else if(theString.matches("(n|N)o"))
			{
				aType = Type.BOOLEAN;
				aBooleanValue = false;
			}
			else if(theString.matches("(\\d+)(-)(\\d+)"))
            {
            	aType = Type.NUMERIC;
            	Pattern pattern = Pattern.compile("(\\d+)(-)(\\d+)(\"?)");
                Matcher matcher = pattern.matcher(theString);
            	if(matcher.find())
            	{
            		Double leftNumber = Double.parseDouble(matcher.group(1));
            		Double rightNumber = Double.parseDouble(matcher.group(3));
                	aNumericValue = (leftNumber + rightNumber) / 2;

            	}
            }
			
            else if(theString.matches("(Limited to )(.*)"))
            {
                aType = Type.NUMERIC;
                
                Pattern pattern = Pattern.compile("(Limited to )(.*)(-)(.*)(Lbs?)(\"?)");
                Matcher matcher = pattern.matcher(theString);
                if(matcher.find())
                {
                	Double leftNumber = Double.parseDouble(matcher.group(2));
                	Double rightNumber = Double.parseDouble(matcher.group(4));
                	aNumericValue = (leftNumber + rightNumber)/2;
                }
               
            }
            
            
            
			else
			{
				aType = Type.STRING;
				aStringValue = theString;
			}
		}
		else
		{
			throw new TypedValueException("Cannot infer type from object of class " + pValue.getClass().getName() + ": " + pValue.toString());
		}
	}	
	
	/**
	 * @return True if and only if this object represents
	 * a non-available value.
	 */
	public boolean isNA()
	{
		return aType == Type.NA;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a null value.
	 */
	public boolean isNull()
	{
		return aType == Type.NULL;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a boolean value.
	 */
	public boolean isBoolean()
	{
		return aType == Type.BOOLEAN;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a numeric.
	 */
	public boolean isNumeric()
	{
		return aType == Type.NUMERIC;
	}
	
	/**
	 * @return True if and only if this object represents
	 * a string value.
	 */
	public boolean isString()
	{
		return aType == Type.STRING;
	}
	
	/**
	 * @return The numeric value after type inference.
	 */
	public double getNumeric()
	{
		if( aType == Type.NUMERIC )
		{
			return aNumericValue;
		}
		else
		{
			throw new TypedValueException("Attempting to obtain a numeric value from a non-numeric type");
		}
	}
	/**
	 * @return The boolean value after type inference.
	 */
	public boolean getBoolean()
	{
		if( aType == Type.BOOLEAN )
		{
			return aBooleanValue;
		}
		else
		{
			throw new TypedValueException("Attempting to obtain a boolean value from a non-boolean type");
		}
	}
	/**
	 * @return The nominal value after type inference.
	 */
	public String getString()
	{
		if( aType == Type.STRING )
		{
			return aStringValue;
		}
		else
		{
			throw new TypedValueException("Attempting to obtain a string value from a non-string type");
		}
	}
	
	@Override
	public boolean equals(Object pObject)
	{
		if( pObject == null )
		{ return false; }
		if( pObject == this ) 
		{ return true; }
		if( pObject.getClass() != getClass() ) 
		{ return false; }
		
		TypedValue value = (TypedValue) pObject;
		return (value.aType == aType) && (value.aNumericValue == aNumericValue) && 
			   (value.aBooleanValue == aBooleanValue) && (value.aStringValue == aStringValue);
	}
	
	@Override
	public int hashCode()
	{
		if( aType == Type.NULL && aType == Type.NA )
		{
			return aType.hashCode();
		}
		else if( aType == Type.BOOLEAN)
		{
			return new Boolean(getBoolean()).hashCode();
		}
		else if( aType == Type.NUMERIC)
		{
			return new Double(getNumeric()).hashCode();
		}
		else if( aType == Type.STRING)
		{
			return getString().hashCode();
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * A string representation of this object to be used for debugging purposes.
	 * Do not use this method to compare values: use equals instead.
	 * @return A string representation of this object that includes the type and its value.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String lReturn = aType.toString();
		if( aType == Type.BOOLEAN )
		{
			lReturn += ": " + aBooleanValue;
		}
		else if( aType == Type.NUMERIC )
		{
			lReturn += ": " + aNumericValue;
		}
		else if( aType == Type.STRING )
		{
			lReturn += ": " + aStringValue;
		}
		return lReturn;
	}
}

