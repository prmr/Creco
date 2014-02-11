package ca.mcgill.cs.creco.persistence;

public class Price {
	String displayName;
	String description;
	String attributeId;
	String dataPresentationFormat;
	String filterWidget;
	
	Double value;
	
	Integer sortOrder;

	Boolean isCategoryCommonAttribute;
	Boolean isForDisplayOnCRO;
	
	public static final String[] stringFields = 
	{
		"displayName", "description", "attributeId",
		"dataPresentationFormat", "filterWidget"
	};
	
	public static final String[] doubleFields = {"value"};
	public static final String[] intFields = {"sortOrder"};
	public static final String[] boolFields = {"isCategoryCommonAttribute", "isForDisplayOnCRO"};
	
	public Double getPrice()
	{
		return this.value;
	}
	
	public Boolean getBool(String key)
	{
		if(key.equals("isCategoryCommonAttribue"))
		{
			return this.isCategoryCommonAttribute;
		}
		else if(key.equals("isForDisplayOnCRO"))
		{
			return this.isForDisplayOnCRO;
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
	
	public Double getDouble(String key)
	{
		if(key.equals("value"))
		{
			return this.value;
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
		else if(key.equals("dataPresentationFormat"))
		{
			return this.dataPresentationFormat;
		}
		else if(key.equals("filterWidget"))
		{
			return this.filterWidget;
		}
		else
		{
			return null;
		}
	}
}
