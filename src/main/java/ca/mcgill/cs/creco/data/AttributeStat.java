package ca.mcgill.cs.creco.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AttributeStat {
	private int count;
	private Object valueMin;
	private Object valueMax;
	private HashSet<String> valueEnum;
	private Attribute attribute;

	public String getName()
	{
		return this.attribute.getName();
	}
	
	public String getDescription()
	{
		return this.attribute.getDescription();               
	}
	
	public String getFilterWidget()
	{
		return this.attribute.getFilterWidget();              
	}
	
	public String getDataPresentationFormat()
	{
		return this.attribute.getDataPresentationFormat();    
	}
	
	public String getAttributeGroup()
	{
		return this.attribute.getAttributeGroup();            
	}
	
	public String getUnitName()
	{
		return this.attribute.getUnitName();
	}
	
	public int getSortOrder()
	{
		return this.attribute.getSortOrder();
	}
	
	public boolean getIsForDisplayOnCRO()
	{
		return this.attribute.getIsForDisplayOnCRO();
	}
	
	public boolean getIsCategoryCommonAttribute()
	{
		return this.attribute.getIsCategoryCommonAttribute();
	}
	
	public String getId() {
		return this.attribute.getId();
	}
	
	public void increment(int add)
	{
		this.count += add;
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public AttributeStat(Attribute attribute) 
	{
		this.attribute = attribute;
		this.valueEnum = new HashSet<String>();
		this.count = 0;
	}

	public AttributeStat(AttributeStat attribute) 
	{
		this.attribute = attribute.getAttribute();
		this.valueEnum = new HashSet<String>();
		this.count = attribute.getCount();
	}

	public Object getValueMax()
	{
		return this.valueMax;
	}
	
	public Object getValueMin()
	{
		return this.valueMin;
	}
	
	public Set<String> getValueEnum()
	{
		if(this.valueEnum != null)
		{
			return Collections.unmodifiableSet(this.valueEnum);
		}
		return null;
	}
	
	public Attribute getAttribute()
	{
		return this.attribute;
	}
	
	void updateRange(Object value)
	{
	
		if(value instanceof Integer || value instanceof Float || value instanceof Double) {
			if(this.valueMin == null || (Double) value < (Double) this.valueMin)
			{
				this.valueMin = value;
			}
			if(this.valueMax == null || (Double) value > (Double) this.valueMax)
			{
				this.valueMax = value;
			}
		}
		else if(value instanceof String)
		{
			this.valueEnum.add((String) value);
		}
	}
	
}