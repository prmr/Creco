package ca.mcgill.cs.creco.persistence;

public class DownLevel {
	Category[] supercategory;
	Category[] category;
	Category[] subfranchise;
	Category[] subcategory;
	
	public static final String[] getDownLevelTypes() {
		return new String[] {"supercategory", "category", "subfranchise", "subcategory"};
	}

	public Category[] getChildren()
	{
		if(this.supercategory != null)
		{
			return this.supercategory;
		}
		else if(this.category != null)
		{
			return this.category;
		}
		else if(this.subfranchise != null)
		{
			return this.subfranchise;
		}
		else if(this.subcategory != null)
		{
			return this.subcategory;
		}
		else
		{
			return null;
		}
	}
}
