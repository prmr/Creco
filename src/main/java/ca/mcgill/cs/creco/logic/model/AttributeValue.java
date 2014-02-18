package ca.mcgill.cs.creco.logic.model;

public class AttributeValue {
	
	private boolean aBool;
	private boolean aNominal;
	private boolean aNumeric;
	
	private boolean aBoolValue;
	private String aNominalValue;
	private double aNumericValue;
	
	
	public AttributeValue(boolean pBool)
	{
		aBoolValue = pBool;
		aBool = true;
	}
	public AttributeValue(String pNominal)
	{
		aNominalValue = pNominal;
		aNominal = true;
	}
	public AttributeValue(double pNumeric)
	{
		aNumericValue = pNumeric;
		aNumeric = true;
	}
	public boolean isBool() 
	{
		return aBool;
	}
	public boolean isNominal() 
	{
		return aNominal;
	}
	public boolean isNumeric() 
	{
		return aNumeric;
	}

	public boolean getBoolValue() 
	{
		return aBoolValue;
	}

	public String getNominalValue() 
	{
		return aNominalValue;
	}
	public double getNumericValue() 
	{
		return aNumericValue;
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
