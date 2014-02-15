package ca.mcgill.cs.creco.data;

public class SpecStat extends AttributeStat{
	public SpecStat(Attribute attribute)
	{
		super(attribute);
	}
	
	public SpecStat(AttributeStat attribute)
	{
		super(attribute);
	}
	
	public Spec getSpec()
	{
		return (Spec)super.getAttribute();
	}
}
