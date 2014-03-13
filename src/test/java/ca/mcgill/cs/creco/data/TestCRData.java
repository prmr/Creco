package ca.mcgill.cs.creco.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author prmr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestCRData 
{	
	@Autowired
	IDataStore aDataStore;
	
	 // Categories that are not eq classes do not exist.
	@Test public void testNonExistentCategories()
	{
		assertNull(aDataStore.getCategory("28934"));
		assertNull(aDataStore.getCategory("28935"));
		assertNull(aDataStore.getCategory("33191"));
		assertNull(aDataStore.getCategory("28713"));
		assertNull(aDataStore.getCategory("28985"));
	}
	
	// Recognized categories
	@Test public void testgetBasicFields()
	{
		Category category = aDataStore.getCategory("34667");
		assertEquals("34667", category.getId());
		assertEquals("Car", category.getFranchise());
		assertEquals("Headlight restoration kits", category.getName());
		
		category = aDataStore.getCategory("31103");
		assertEquals("31103", category.getId());
		assertEquals("Car", category.getFranchise());
		assertEquals("Performance all season tire", category.getName());
		
		category = aDataStore.getCategory("32963");
		assertEquals("32963", category.getId());
		assertEquals("Baby & kid", category.getFranchise());
		assertEquals("Toy", category.getName());
		
		category = aDataStore.getCategory("34784");
		assertEquals("34784", category.getId());
		assertEquals("Food", category.getFranchise());
		assertEquals("Bagel", category.getName());
		
		category = aDataStore.getCategory("34605");
		assertEquals("34605", category.getId());
		assertEquals("Electronic & Computer", category.getFranchise());
		assertEquals("Bluetooth headset", category.getName());
		
		category = aDataStore.getCategory("34815");
		assertEquals("34815", category.getId());
		assertEquals("Money", category.getFranchise());
		assertEquals("Credit card", category.getName());
		
		category = aDataStore.getCategory("28666");
		assertEquals("28666", category.getId());
		assertEquals("Appliance", category.getFranchise());
		assertEquals("Air conditioner", category.getName());
		
		category = aDataStore.getCategory("28735");
		assertEquals("28735", category.getId());
		assertEquals("Health", category.getFranchise());
		assertEquals("Treadmill", category.getName());
		
		category = aDataStore.getCategory("33008");
		assertEquals("33008", category.getId());
		assertEquals("Home & garden", category.getFranchise());
		assertEquals("Chain saw", category.getName());
	}
	
	@Test public void testGetProduct()
	{
		Product product = aDataStore.getProduct("8291");
		assertEquals("8291", product.getId());
		
		product = aDataStore.getProduct("46018");
		assertEquals("46018", product.getId());
		
		product = aDataStore.getProduct("247118");
		assertEquals("247118", product.getId());
		
		product = aDataStore.getProduct("244198");
		assertEquals("244198", product.getId());
	}
	
	// Ensures that all N/A and NA values receive the string NA and become 
	// tagged as an NA value. See Issue #0050
	@Test public void testNAValues()
	{
		Attribute attribute = aDataStore.getProduct("220271").getRating("4512");
		assertEquals("NA", attribute.getValue());
		assertEquals(TypedValue.Type.NA, attribute.getType());
		
		attribute = aDataStore.getProduct("203938").getSpec("979");
		assertEquals("NA", attribute.getValue());
		assertEquals(TypedValue.Type.NA, attribute.getType());
		
		attribute = aDataStore.getProduct("229028").getSpec("4188");
		assertEquals("NA", attribute.getValue());
		assertEquals(TypedValue.Type.NA, attribute.getType());
	}
}
