package ca.mcgill.cs.creco.logic;

import java.util.List;


/**
 *@deprecated
 */
public class AttributeValue 
{
	
	private boolean aBool;
	private boolean aNominal;
	private boolean aNumeric;
	
	private boolean aBoolValue;
	private String aNominalValue;
	private double aNumericValue;
	
	private double aMin;
	private double aMax;
	private List<String> aDict;
	
	
	/**Constructor for boolean attribute.
	 * @param pBool attribute value
	 */
	public AttributeValue(boolean pBool)
	{
		aBoolValue = pBool;
		aBool = true;
	}
	/**Constructor for nominal attribute.
	 * @param pNominal attribute value
	 */
	public AttributeValue(String pNominal)
	{
		aNominalValue = pNominal;
		aNominal = true;
	}
	public AttributeValue(String pNominal, List<String> pDict)
	{
		aNominalValue = pNominal;
		aNominal = true;
		aDict = pDict;
	}
	/**Constructor for numeric attribute.
	 * @param pNumeric attribute value
	 */
	public AttributeValue(double pNumeric)
	{
		aNumericValue = pNumeric;
		aNumeric = true;
	}
	public AttributeValue(double pNumeric, double pMin, double pMax)
	{
		aNumericValue = pNumeric;
		aNumeric = true;
		aMin = pMin;
		aMax = pMax;
	}
	/**Check is attribute is boolean.
	 * @return true is boolean attribute
	 */
	public boolean isBool() 
	{
		return aBool;
	}
	/**Check is attribute is numeric.
	 * @return true is numeric attribute
	 */
	public boolean isNominal() 
	{
		return aNominal;
	}
	/**Check is attribute is numeric.
	 * @return true is numeric attribute
	 */
	public boolean isNumeric() 
	{
		return aNumeric;
	}

	/**
	 * @return boolean value
	 */
	public boolean getBoolValue() 
	{
		return aBoolValue;
	}

	/**
	 * @return nominal value as String
	 */
	public String getNominalValue() 
	{
		return aNominalValue;
	}
	
	/**
	 * @return numeric value as double
	 */
	public double getNumericValue() 
	{
		return aNumericValue;
	}
 
	
	
	public double getMin() {
		return aMin;
	}
	public double getMax() {
		return aMax;
	}
	public List<String> getDict() {
		return aDict;
	}
	
	@Override
	public String toString()
	{
		if(aBool)
		{
			return aBoolValue +"";
		}
		if(aNumeric)
		{
			return aNumericValue +"";
		}
		return aNominalValue;
	}
}
