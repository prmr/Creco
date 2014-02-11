package ca.mcgill.cs.creco.persistence;

public class Attribute {
	private String displayName;
	private String description;
	private String attributeId;
	private String filterWidget;
	private String dataPresentationFormat;
	
	private String attributeGroup;
	private String unitName;
	
	// Note the json attribute 'value' varies in data type
	// from one jsonAttribute to the next.  
	// Users of the class have to check
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
	
	public Object getValue()
	{
		return this.value;
	}
	
	public Integer getSortOrder()
	{
		return this.sortOrder;
	}
	public String getString(String key)
	{
		if(key.equals("displayName"))
		{
			return this.displayName;
		}
		else if(key.equals("description"))
		{
			return this.description;
		} 
		else if(key.equals("attributeId"))
		{
			return this.attributeId;
		}
		else if(key.equals("filterWidget"))
		{
			return this.filterWidget;
		}
		else if(key.equals("dataPresentationFormat"))
		{
			return this.dataPresentationFormat;
		}
		else if(key.equals("attributeGroup"))
		{
			return this.attributeGroup;
		}
		else if(key.equals("unitName"))
		{
			return this.unitName;
		}
		else
		{
			return null;
		}							
	}
	
	public Boolean getBool(String key)
	{
		if(key.equals("isForDisplayOnCRO"))
		{
			return this.isForDisplayOnCro;
		}
		else if(key.equals("isCategoryCommonAttribute"))
		{
			return this.isCategoryCommonAttribute;
		}
		else
		{
			return null;
		}
	}

	public Integer getInt(String key)
	{
		if(key.equals("sortOrder"))
		{
			return this.sortOrder;
		}
		else
		{
			return null;
		}
	}
	
	public String getAttributeName()
	{
		return this.displayName;
	}
	
	public String getId() {
		return this.attributeId;
	}
	

	public void setValue(Object val)
	{
		this.value = val;
	}
	
	public void setInt(String key, Integer val)
	{
		if(key.equals("sortOrder"))
		{
			this.sortOrder = val;
		}
	}
	
	public void setDouble(String key, Double val) {
		if(key.equals("value"))
		{
			this.value = val;
		}
	}
	
	public void setBool(String key, Boolean val)
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
	
	public void setString(String key, String val)
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
