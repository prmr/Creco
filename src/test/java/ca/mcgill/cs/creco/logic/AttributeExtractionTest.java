package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.AttributeStat;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class AttributeExtractionTest
{
	@Autowired
	IDataStore aDataStore;
	
	@Test
	public void testAllCategories() 
	{
		try
		{
			for(Category c : aDataStore.getCategories())
			{
				Set<Product> productSet = new HashSet<Product>();
				for(Product product : c.getProducts())
				{
					productSet.add(product);
				}
				AttributeExtractor ae = new AttributeExtractor(productSet, c);
				ae.getScoredSpecList();
				ae.getScoredRatingList();
			}
			
		}
		catch (Exception e)
		{
			fail("Exception caught " + e);
		}
		
	}
	
	@Test
	public void testSorting() 
	{
		//numeric
		ArrayList<ScoredAttribute> sal = new ArrayList<ScoredAttribute>();
		for(int i = 0; i < 10; i++)
		{
			ScoredAttribute sa = new ScoredAttribute(new Attribute(i+ "", i+ "", i+ "", null), new Category("test", "test", "test", new ArrayList<Product>(), 
					new ArrayList<AttributeStat>(), new ArrayList<AttributeStat>()));
			sa.setAttributeScore(i);
			sal.add(sa);
		}
		
		Collections.sort(sal,ScoredAttribute.SORT_BY_SCORE);
	
		for(int i = 0; i < 10; i++)
		{
			String index = (9 - i) + "";
			assertTrue(index.equals(sal.get(i).getAttributeName()));
		}
		
	}

}
