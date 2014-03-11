package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.Category2;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;
import ca.mcgill.cs.creco.logic.search.ScoredProduct;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class AttributeExtractionTest
{
	@Autowired
	IDataStore aDataStore;
	
	// prmr removed because that was the only accessor of getCategories.
//	@Test
//	public void testAllCategories() 
//	{
//		String catName = "";
//		try
//		{
//			for(Category2 c : aDataStore.getCategories())
//			{
//				catName = c.getName();
//				Set<Product> productSet = new HashSet<Product>();
//				for(Product product : c.getProducts())
//				{
//					productSet.add(product);
//				}
//				AttributeExtractor aec = new AttributeExtractor(productSet, c);
//				ArrayList<ScoredAttribute> ssl = aec.getScoredSpecList();
//				ArrayList<ScoredAttribute> srl = aec.getScoredRatingList();
//			}
//			
//		}
//		catch (Exception e)
//		{
//			fail("Exception caught " + e);
//		}
//		
//	}
	
	@Test
	public void testSorting() 
	{
		//numeric
		ArrayList<ScoredAttribute> sal = new ArrayList<ScoredAttribute>();
		for(int i = 0; i < 10; i++)
		{
			ScoredAttribute sa = new ScoredAttribute(new Attribute(i+ "", i+ "", i+ "", null));
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
	
	@Test
	public void testMeanExtractionNumeric() 
	{
		ArrayList<Product> pal = new  ArrayList<Product>();
		for(int i = 0; i < 10; i++)
		{
			Product p = new Product(i+"",i+"", true, "thing","ImaginaryProducts");
			p.addSpec(new Attribute("test", i+ "", i+ "", i));
			pal.add(p);
			
		}
		
		assertTrue(4.5 == AttributeExtractor.extractMean( pal , "test").getNumericValue());
		
	}
	
	@Test
	public void testModeExtractionNominal() 
	{
		ArrayList<Product> pal = new  ArrayList<Product>();
		for(int i = 0; i < 10; i++)
		{
			Product p = new Product(i+"",i+"", true, "thing","ImaginaryProducts");
			if(i < 7)
			{
				p.addSpec(new Attribute("test", i+ "", i+ "", "mode"));
			}
			else
			{
				p.addSpec(new Attribute("test", i+ "", i+ "", "error"));
			}
			
			pal.add(p);
			
		}
		String answer = "mode";
		assertTrue(answer.equals(AttributeExtractor.extractMean( pal , "test").getNominalValue()));
		
	}
	
	@Test
	public void testModeExtractionBoolean() 
	{
		ArrayList<Product> pal = new  ArrayList<Product>();
		for(int i = 0; i < 10; i++)
		{
			Product p = new Product(i+"",i+"", true, "thing","ImaginaryProducts");
			if(i < 7)
			{
				p.addSpec(new Attribute("test", i+ "", i+ "", true));
			}
			else
			{
				p.addSpec(new Attribute("test", i+ "", i+ "", false));
			}
			
			pal.add(p);
			
		}
		assertTrue(AttributeExtractor.extractMean( pal , "test").getBoolValue());
	}
	
	@Test
	public void testExtractMixed() 
	{
		ArrayList<Product> pal = new  ArrayList<Product>();
		for(int i = 0; i < 10; i++)
		{
			Product p = new Product(i+"",i+"", true, "thing","ImaginaryProducts");
			if(i < 3)
			{
				p.addSpec(new Attribute("test", i+ "", i+ "", true));
			}
			else if (i < 7)
			{
				p.addSpec(new Attribute("test", i+ "", i+ "", "string"));
			}
			else{
				p.addSpec(new Attribute("test", i+ "", i+ "", 1));
			}
			
			pal.add(p);
			
		}
		String answer = "string";
		assertTrue(answer.equals(AttributeExtractor.extractMean( pal , "test").getNominalValue()));
		
	}
	
	@Test
	public void testEmptySepcs() 
	{
		
		Category cat = new Category("empty", "test", null);
		ArrayList<ScoredProduct> spal = new  ArrayList<ScoredProduct>();
		for(int i = 0; i < 10; i++)
		{
			Product p = new Product(i+"",i+"", true, "thing","ImaginaryProducts");
			if(i < 7)
			{
				p.addSpec(new Attribute("test", i+ "", i+ "", 1));
			}
			else
			{
				p.addSpec(new Attribute("test", i+ "", i+ "", 0));
			}
			spal.add(new ScoredProduct(p, (float) 0.0,"empty"));			
		}
		AttributeExtractor ae = new AttributeExtractor(spal, cat);
		ArrayList<ScoredAttribute> sal = ae.getScoredSpecList();
		assertTrue(sal.size() == 0);
		
	}

}
