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

public class TypedValue 
{
	private String aType;
	private Object aValue;
	private Object aOriginalValue;
	
	public TypedValue(Object pValue)
	{
		aOriginalValue = pValue;
		
		if(pValue == null)
		{
			aType = "null";
			aValue = null;
		}
		else if(pValue instanceof Integer)
		{
			aType = "int";
			aValue = pValue;
		}
		else if(pValue instanceof Double)
		{
			aType = "double";
			aValue = pValue;
		}
		else if(pValue instanceof Float)
		{
			aType = "float";
			aValue = pValue;
		}
		else if(pValue instanceof Boolean)
		{
			aType = "boolean";
			aValue = pValue;
		}
		else if(pValue instanceof String)
		{
			String str = (String) pValue; 
			// match int with optional '-' and decimal.
			if(str.matches("-?\\d+"))
			{
				aType = "int";
				aValue = Integer.parseInt(str);
			}
			//match a number with optional '-' and decimal.
			else if(str.matches("-?\\d+(\\.\\d+)?"))  
			{
				aType = "double";
				aValue = Double.parseDouble(str);
			}
			else if(str.matches("(y|Y)es"))
			{
				aType = "bool";
				aValue = true;
			}
			else if(str.matches("(n|N)o"))
			{
				aType = "bool";
				aValue = false;
			}
			else
			{
				aType = "String";
				aValue = str;
			}
		}
		else
		{
			aType = "unknown";
			aValue = pValue;
		}
	}	
	
	String getType() 
	{
		return this.aType;
	}
	
	Object getValue()
	{
		return this.aValue;
	}
	
	Object getOriginalValue()
	{
		return this.aOriginalValue;
	}	
}

