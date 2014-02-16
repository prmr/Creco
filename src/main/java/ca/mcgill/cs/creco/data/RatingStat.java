package ca.mcgill.cs.creco.data;

public class RatingStat extends AttributeStat{

	public RatingStat(Attribute attribute)
	{
		super(attribute);
	}
	
	public RatingStat(AttributeStat attribute)
	{
		super(attribute);
	}
	
	public Rating getRating()
	{
		return (Rating)super.getAttribute();
	}
}
