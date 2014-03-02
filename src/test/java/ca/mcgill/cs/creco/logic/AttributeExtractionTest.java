package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import ca.mcgill.cs.creco.data.CRData;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.model.ScoredAttribute;

public class AttributeExtractionTest
{
	@Test
	public void testAllCategories() {
		String catName = "";
		try
		{
			Iterator<Category> categories = CRData.getData().getCategories();
			while(categories.hasNext())
			{
				Category c = categories.next();
				if(c.getJaccard() == null || c.getJaccard() < 0.8)
				{
					continue;
				}
				catName = c.getName();
				Set<Product> productSet = new HashSet<Product>();
				for (Product product : c.getProducts())
				{
					productSet.add(product);
				}
				AttributeExtractor aec = new AttributeExtractor(productSet, c);
				ArrayList<ScoredAttribute> ssl = aec.getScoredSpecList();
				ArrayList<ScoredAttribute> srl = aec.getScoredRatingList();
				System.out.println("Specs:");
				System.out.println(ssl);
				System.out.println("Ratings:");
				System.out.println(srl);
			}
			
		}
		catch (Exception e)
		{
			System.out.println(catName + ": " + e);			
			fail("Exception caught " + e);
		}
		
	}

}
