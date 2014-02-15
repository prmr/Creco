package ca.mcgill.cs.creco.data.stubs;

/**
 * This class is designed to capture data from the CR database for attributes (ratings and specs) which are 
 * properties of products.  It is a superclass for RatingStub and SpecStub.
 * 
 * @author enewe101
 *
 */
public class AttributeStub {
	public String displayName;
	public String description;
	public String attributeId;
	public String filterWidget;
	public String dataPresentationFormat;
	public String attributeGroup;
	public String unitName;
	public Object value;
	public Integer sortOrder;
	public Boolean isForDisplayOnCRO;
	public Boolean isCategoryCommonAttribute;
}
