package ca.mcgill.cs.creco.persistence;

import org.json.JSONObject;

public class Attribute {
	private String displayName;
	private String description;
	private String attributeId;
	private String filterWidget;
	private String dataPresentationFormat;
	
	private String attributeGroup;
	private String unitName;
	
	// Note the json attribute 'value' varies in data type
	// from one jsonAttribute to the next.  Users of the class
	// have to check
	private Object value;
	
	private Integer sortOrder;
	
	private Boolean isForDisplayOnCro;
	private Boolean isCategoryCommonAttribute;
	
	private static String[] stringFields = 
	{
		"displayName","description","attributeId","filterWidget",
		"dataPresentationFormat", "attributeGroup", "unitName"
	};
	
	private static String[] boolFields = 
	{
		"isForDisplayOnCro", "isCategoryCommonAttribute"
	};
	
	
	public Attribute(JSONObject jsonAttribute) {
		
		int i;
		
		// Set all the string fields
		for(i=0; i<Attribute.stringFields.length; i++)
		{
			String key = Attribute.stringFields[i];
			if(jsonAttribute.has(key))
			{
				this.setString(key, jsonAttribute.getString(key));
			}
			else
			{
				this.setString(key, null);
			}
		}
		
		// Set all the boolean fields
		for(i=0; i<Attribute.boolFields.length; i++)
		{
			String key = Attribute.boolFields[i];
			if(jsonAttribute.has(key))
			{
				this.setBool(key, jsonAttribute.getBoolean(key));
			}
			else 
			{
				this.setBool(key, null);
			}
		}
		
		if(jsonAttribute.has("value")) 
		{
			this.setValue(jsonAttribute.get("value"));
		}
		else
		{
			this.setValue(null);
		}
		
		if(jsonAttribute.has("sortOrder")) 
		{
			this.setInteger("sortOrder", jsonAttribute.getInt("sortOrder"));
		}
		else 
		{
			this.setInteger("sortOrder", null);
		}
	}
	
	private void setValue(Object val)
	{
		this.value = val;
	}
	
	private void setInteger(String key, Integer val)
	{
		if(key.equals("sortOrder"))
		{
			this.sortOrder = val;
		}
	}
	
	private void setDouble(String key, Double val) {
		if(key.equals("value"))
		{
			this.value = val;
		}
	}
	
	private void setBool(String key, Boolean val)
	{
		if(key.equals("isForDisplayOnCro"))
		{
			this.isForDisplayOnCro = val;
		}
		else if(key.equals("isCategoryCommonAttribute"))
		{
			this.isCategoryCommonAttribute = val;
		}
	}
	
	private void setString(String key, String val)
	{
		if(key.equals("displayName"))
		{
			this.displayName = val;
		}
		else if(key.equals("description"))
		{
			this.description = val;
		}
		else if(key.equals("attributeId"))
		{
			this.attributeId = val;
		}
		else if(key.equals("filterWidget"))
		{
			this.filterWidget = val;
		}
		else if(key.equals("dataPresentationFormat"))
		{
			this.dataPresentationFormat = val;	
		}
	}
	
}
