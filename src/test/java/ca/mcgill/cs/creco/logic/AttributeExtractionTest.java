package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.mcgill.cs.creco.data.CRData;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.CategoryList;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.AttributeExtractor;
import ca.mcgill.cs.creco.logic.model.ScoredAttribute;

import com.google.common.collect.Lists;

public class AttributeExtractionTest {

	@Test
	public void testAllCategories() {
		String catName = "";
		try
		{
			CRData data = CRData.getData();
			CategoryList catList = data.getCategoryList();
			for(Category c : catList)
			{
				if(c.getJaccard() == null || c.getJaccard() < 0.8)
				{
					continue;
				}
				catName = c.getName();
				Iterable<Product> products = c.getProducts();
				List<Product> productList = Lists.newArrayList(products);
				AttributeExtractor aec = new AttributeExtractor(productList, c);
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
			fail("Exception caught");
		}
		
	}

}
