package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestScoredAttribute {

	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	AttributeExtractor aAttributeExtractor;
	
	@Autowired
	ProductRanker aProductRanker;
	
	@Test
	public void testSorting() throws NoSuchMethodException, SecurityException 
	{
		
		//Constructor<Product> c = Product.class.getDeclaredConstructor();
		//c.setAccessible(true);
		//Product u = c.newInstance( ); 

		
	}
	
}
