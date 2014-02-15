package ca.mcgill.cs.creco.data;

import ca.mcgill.cs.creco.data.stubs.AttributeStub;

public class Attribute {
	private String displayName;
	private String description;
	private String attributeId;
	private String filterWidget;
	private String dataPresentationFormat;
	private String attributeGroup;
	private String unitName;	
	private Object value;
	private Integer sortOrder;
	private Boolean isForDisplayOnCRO;
	private Boolean isCategoryCommonAttribute;
	
	Attribute(AttributeStub attStub)
	{
		this.displayName = attStub.displayName;
		this.description = attStub.description;
		this.attributeId = attStub.attributeId;
		this.filterWidget = attStub.filterWidget;
		this.dataPresentationFormat = attStub.dataPresentationFormat;
		this.attributeGroup = attStub.attributeGroup; 
		this.unitName = attStub.unitName; 
		this.value = attStub.value;      
		this.sortOrder = attStub.sortOrder;  
		this.isForDisplayOnCRO = attStub.isForDisplayOnCRO; 
		this.isCategoryCommonAttribute = attStub.isCategoryCommonAttribute;
	}

	public String getName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public String getId() {
		return this.getAttributeId();
	}

	public String getFilterWidget() {
		return filterWidget;
	}

	public String getDataPresentationFormat() {
		return dataPresentationFormat;
	}

	public String getAttributeGroup() {
		return attributeGroup;
	}

	public String getUnitName() {
		return unitName;
	}

	public Object getValue() {
		return value;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public Boolean getIsForDisplayOnCRO() {
		return isForDisplayOnCRO;
	}

	public Boolean getIsCategoryCommonAttribute() {
		return isCategoryCommonAttribute;
	}	
	
}
