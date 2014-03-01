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

public class TypedVal 
{
	private String aType;
	private Object aValue;
	private Object aOriginalValue;
	
	public TypedVal(Object pValue)
	{
		this.aOriginalValue = pValue;
		
		if(pValue == null)
		{
			this.aType = "null";
			this.aValue = null;
		}
		else if(pValue instanceof Integer)
		{
			this.aType = "int";
			this.aValue = pValue;
		}
		else if(pValue instanceof Double)
		{
			this.aType = "double";
			this.aValue = pValue;
		}
		else if(pValue instanceof Float)
		{
			this.aType = "float";
			this.aValue = pValue;
		}
		else if(pValue instanceof Boolean)
		{
			this.aType = "boolean";
			this.aValue = pValue;
		}
		else if(pValue instanceof String)
		{
			String str = (String) pValue; 
			// match int with optional '-' and decimal.
			if(str.matches("-?\\d+"))
			{
				this.aType = "int";
				this.aValue = Integer.parseInt(str);
			}
			//match a number with optional '-' and decimal.
			else if(str.matches("-?\\d+(\\.\\d+)?"))  
			{
				this.aType = "double";
				this.aValue = Double.parseDouble(str);
			}
			else if(str.matches("(y|Y)es"))
			{
				this.aType = "bool";
				this.aValue = true;
			}
			else if(str.matches("(n|N)o"))
			{
				this.aType = "bool";
				this.aValue = false;
			}
			else
			{
				this.aType = "String";
				this.aValue = str;
			}
		}
		else
		{
			this.aType = "unknown";
			this.aValue = pValue;
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

