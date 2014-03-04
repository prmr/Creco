package ca.mcgill.cs.creco.data;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author prmr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestCRData 
{	
	@Autowired
	IDataStore aDataStore;
	
	@Test
	public void testIndex() throws IOException
	{
		assertEquals(aDataStore.getCategory("28934").getName(), "Car");
		assertEquals(aDataStore.getCategory("28935").getName(), "Tire & car care");
		assertEquals(aDataStore.getCategory("34667").getName(), "Headlight restoration kits");
		assertEquals(aDataStore.getCategory("34687").getName(), "Car repair shop");
		assertEquals(aDataStore.getCategory("28985").getName(), "Baby & kid");
		assertEquals(aDataStore.getCategory("30710").getName(), "School-age kid");
		assertEquals(aDataStore.getCategory("32963").getName(), "Toy");
		assertEquals(aDataStore.getCategory("33083").getName(), "Bassinet");
		assertEquals(aDataStore.getCategory("32975").getName(), "Play yard");
		assertEquals(aDataStore.getCategory("32976").getName(), "Play yard");
		assertEquals(aDataStore.getCategory("33204").getName(), "Play Yard");
		assertEquals(aDataStore.getCategory("33546").getName(), "Food");
		assertEquals(aDataStore.getCategory("36894").getName(), "Cereal");
		assertEquals(aDataStore.getCategory("28949").getName(), "Electronic & Computer");
		assertEquals(aDataStore.getCategory("34589").getName(), "Paper shredder");
		assertEquals(aDataStore.getCategory("30895").getName(), "Cordless phones with answerer");
		assertEquals(aDataStore.getCategory("34458").getName(), "Money");
		assertEquals(aDataStore.getCategory("28967").getName(), "Appliance");
		assertEquals(aDataStore.getCategory("33658").getName(), "Thermostat");
		assertEquals(aDataStore.getCategory("32958").getName(), "Humidifier");
		assertEquals(aDataStore.getCategory("32968").getName(), "Humidifier");
		assertEquals(aDataStore.getCategory("36786").getName(), "Health");
		assertEquals(aDataStore.getCategory("35173").getName(), "Diet plan");
		assertEquals(aDataStore.getCategory("28937").getName(), "Home & garden");
		assertEquals(aDataStore.getCategory("34740").getName(), "Toilet paper");
	}
	
	@Test
	public void testParentChild() throws IOException
	{
		assertNull(aDataStore.getCategory("28934").getParent());
		assertNull(aDataStore.getCategory("28985").getParent());
		assertNull(aDataStore.getCategory("33546").getParent());
		assertNull(aDataStore.getCategory("28934").getParent());
		assertNull(aDataStore.getCategory("28949").getParent());
		assertEquals("28934", aDataStore.getCategory("28935").getParent().getId());
		assertEquals("28935", aDataStore.getCategory("34667").getParent().getId());
		assertEquals("28935", aDataStore.getCategory("34686").getParent().getId());
		assertEquals("28986", aDataStore.getCategory("32975").getParent().getId());
		assertEquals("28949", aDataStore.getCategory("34587").getParent().getId());
		assertEquals("34589", aDataStore.getCategory("34592").getParent().getId());
		assertEquals("28683", aDataStore.getCategory("30929").getParent().getId());
		assertEquals("28698", aDataStore.getCategory("33295").getParent().getId());
		assertEquals("28723", aDataStore.getCategory("33353").getParent().getId());
		assertEquals("28959", aDataStore.getCategory("28960").getParent().getId());
		assertEquals("28949", aDataStore.getCategory("28959").getParent().getId());
		assertEquals("28968", aDataStore.getCategory("32958").getParent().getId());
		assertEquals("28937", aDataStore.getCategory("28941").getParent().getId());
		assertEquals("33011", aDataStore.getCategory("33071").getParent().getId());
		assertEquals("34610", aDataStore.getCategory("34727").getParent().getId());
		assertEquals("28947", aDataStore.getCategory("33768").getParent().getId()); // Category merging
	}
}
