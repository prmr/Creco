package ca.mcgill.cs.creco.data;

public class Brand {
	String url;
	String displayName;
	String id;
	String name;

	public String getString(String key)
	{
		if(key.equals("url"))
		{
			return this.url;
		}
		else if(key.equals("displayName"))
		{
			return this.displayName;
		}
		else if(key.equals("id"))
		{
			return this.id;
		}
		else if(key.equals("name"))
		{
			return this.name;
		}
		else
		{
			return null;
		}
	}
}
