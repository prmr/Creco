package ca.mcgill.cs.creco.data;

public class AttributeStat {
	private String displayName;
	private String description;
	private String attributeId;
	private String filterWidget;
	private String dataPresentationFormat;
	
	private String attributeGroup;
	private String unitName;
		
	private Integer sortOrder;
	
	private Boolean isForDisplayOnCro;
	private Boolean isCategoryCommonAttribute;
	
	private int count;

	private static String[] stringFields = 
	{
		"displayName","description","attributeId","filterWidget",
		"dataPresentationFormat", "attributeGroup", "unitName"
	};
	
	private static String[] boolFields = 
	{
		"isForDisplayOnCro", "isCategoryCommonAttribute"
	};
	
	public String getAttributeName()
	{
		return this.displayName;
	}
	
	public String getId() {
		return this.attributeId;
	}
	
	public void increment(int add)
	{
		this.count += add;
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public AttributeStat(Attribute attribute) {
		
		// Set all the string fields
		for(String key : AttributeStat.stringFields)
		{
			this.setString(key, attribute.getString(key));
		}
		for(String key : AttributeStat.boolFields)
		{
			this.setBool(key, attribute.getBool(key));
		}
		this.setInt("sortOrder", attribute.getInt("sortOrder"));
		this.count = 0;
	}
	
	public AttributeStat(AttributeStat attribute) 
	{
		// Set all the string fields
		for(String key : AttributeStat.stringFields)
		{
			this.setString(key, attribute.getString(key));
		}
		for(String key : AttributeStat.boolFields)
		{
			this.setBool(key, attribute.getBool(key));
		}
		this.setInt("sortOrder", attribute.getInt("sortOrder"));
		this.count = attribute.getCount();
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
		
	private void setInt(String key, Integer val)
	{
		if(key.equals("sortOrder"))
		{
			this.sortOrder = val;
		}
	}
	
	public void setSortOrder(int sortOrder)
	{
		this.sortOrder = sortOrder;
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