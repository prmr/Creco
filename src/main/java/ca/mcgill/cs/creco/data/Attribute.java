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

/**
 * Represents an immutable attribute of a product.
 */
public final class Attribute 
{
	/**
	 * Obtained from the original data.
	 */
	private enum AttributeType 
	{ RATING, SPECIFICATION, PRICE }
	
	private final String aDisplayName;
	private final String aDescription;
	private final String aAttributeId;
	private final TypedValue aTypedValue;
	
	private AttributeType aAttributeType;
	
	/**
	 * Creates a new Attribute.  You must use one of the factory methods below (buildSpecification,
	 * buildRating, buildPrice), as applicable.
	 * 
	 * @param pId The id of the attribute.
	 * @param pName The display name.
	 * @param pDescription The description.
	 * @param pValue The value for the attribute.
	 * @param AttributeType the type of attribute (RATING, SPECIFICATION, or PRICE)
	 */
	private Attribute( String pId, String pName, String pDescription, Object pValue, AttributeType pAttributeType)
	{
		aAttributeId = pId;
		aDescription = pDescription;
		aDisplayName = pName;
		aTypedValue = new TypedValue(pValue);
		aAttributeType = pAttributeType;
	}

	/**
	 * Creates a new specification Attribute.
	 * 
	 * @param pId The id of the attribute.
	 * @param pName The display name.
	 * @param pDescription The description.
	 * @param pValue The value for the attribute.
	 * @return The newly created specification attribute.
	 */
	public static Attribute buildSpecification(String pId, String pName, String pDescription, Object pValue )
	{
		return new Attribute(pId, pName, pDescription, pValue, Attribute.AttributeType.SPECIFICATION);
	}

	/**
	 * Creates a new rating Attribute.
	 * 
	 * @param pId The id of the attribute.
	 * @param pName The display name.
	 * @param pDescription The description.
	 * @param pValue The value for the attribute.
	 * @return The newly created rating attribute.
	 */
	public static Attribute buildRating(String pId, String pName, String pDescription, Object pValue )
	{
		return new Attribute(pId, pName, pDescription, pValue, Attribute.AttributeType.RATING);
	}

	/**
	 * Creates a new price Attribute.
	 * 
	 * @param pId The id of the attribute.
	 * @param pName The display name.
	 * @param pDescription The description.
	 * @param pValue The value for the attribute.
	 * @return The newly created price attribute.
	 */
	public static Attribute buildPrice(String pId, String pName, String pDescription, Object pValue )
	{
		return new Attribute(pId, pName, pDescription, pValue, Attribute.AttributeType.PRICE);
	}

	/**
	 * @return True if the attribute is a rating
	 */
	public boolean isRating()
	{
		return aAttributeType == AttributeType.RATING;	
	}

	/**
	 * @return True if the attribute is a specification
	 */
	public boolean isSpecification()
	{
		return aAttributeType == AttributeType.SPECIFICATION;	
	}

	/**
	 * @return True if the attribute is a price
	 */
	public boolean isPrice()
	{
		return aAttributeType == AttributeType.PRICE;	
	}
	
	/**
	 * @return The display name of the attribute.
	 */
	public String getName() 
	{ return aDisplayName; }

	/**
	 * @return The description of the attribute.
	 */
	public String getDescription() 
	{ return aDescription; }

	/**
	 * @return The id of this attribute.
	 */
	public String getId() 
	{ return aAttributeId; }

	/**
	 * @return The typed value.
	 */
	public TypedValue getTypedValue() 
	{ return aTypedValue; }
}
