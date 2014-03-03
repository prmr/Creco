package ca.mcgill.cs.creco.data;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author prmr
 */
public class TestCRData 
{	
	static
	{
		// optimization. If there's an IOException while loading, it's going
		// to show up in a test.
		try
		{
			CRData.setupWithFileNames(new String[] {
					"test-appliances.json", "test-electronicsComputers.json",
					"test-cars.json", "test-health.json", "test-homeGarden.json", 
					"test-food.json", "test-babiesKids.json", "test-money.json"
				},"test-category.json");
		}
		catch( IOException e )
		{
			fail("Test data not loaded " + e.getMessage());
		}
	}
	
	@Test
	public void testIndex() throws IOException
	{
		CRData data = CRData.getData();
		
		assertEquals(data.getCategory("28934").getName(), "Car");
		assertEquals(data.getCategory("28935").getName(), "Tire & car care");
		assertEquals(data.getCategory("34667").getName(), "Headlight restoration kits");
		assertEquals(data.getCategory("34687").getName(), "Car repair shop");
		assertEquals(data.getCategory("28985").getName(), "Baby & kid");
		assertEquals(data.getCategory("30710").getName(), "School-age kid");
		assertEquals(data.getCategory("32963").getName(), "Toy");
		assertEquals(data.getCategory("33083").getName(), "Bassinet");
		assertEquals(data.getCategory("32975").getName(), "Play yard");
		assertEquals(data.getCategory("32976").getName(), "Play yard");
		assertEquals(data.getCategory("33204").getName(), "Play Yard");
		assertEquals(data.getCategory("33546").getName(), "Food");
		assertEquals(data.getCategory("36894").getName(), "Cereal");
		assertEquals(data.getCategory("28949").getName(), "Electronic & Computer");
		assertEquals(data.getCategory("34589").getName(), "Paper shredder");
		assertEquals(data.getCategory("30895").getName(), "Cordless phones with answerer");
		assertEquals(data.getCategory("34458").getName(), "Money");
		assertEquals(data.getCategory("28967").getName(), "Appliance");
		assertEquals(data.getCategory("33658").getName(), "Thermostat");
		assertEquals(data.getCategory("32958").getName(), "Humidifier");
		assertEquals(data.getCategory("32968").getName(), "Humidifier");
		assertEquals(data.getCategory("36786").getName(), "Health");
		assertEquals(data.getCategory("35173").getName(), "Diet plan");
		assertEquals(data.getCategory("28937").getName(), "Home & garden");
		assertEquals(data.getCategory("34740").getName(), "Toilet paper");
		
	}
	
	@Test
	public void testParentChild() throws IOException
	{
		CRData data = CRData.getData();
		
		assertNull(data.getCategory("28934").getParent());
		assertNull(data.getCategory("28985").getParent());
		assertNull(data.getCategory("33546").getParent());
		assertNull(data.getCategory("28934").getParent());
		assertNull(data.getCategory("28949").getParent());
		assertEquals("28934", data.getCategory("28935").getParent().getId());
		assertEquals("28935", data.getCategory("34667").getParent().getId());
		assertEquals("28935", data.getCategory("34686").getParent().getId());
		assertEquals("28986", data.getCategory("32975").getParent().getId());
		assertEquals("28949", data.getCategory("34587").getParent().getId());
		assertEquals("34589", data.getCategory("34592").getParent().getId());
		assertEquals("28683", data.getCategory("30929").getParent().getId());
		assertEquals("28698", data.getCategory("33295").getParent().getId());
		assertEquals("28723", data.getCategory("33353").getParent().getId());
		assertEquals("28959", data.getCategory("28960").getParent().getId());
		assertEquals("28949", data.getCategory("28959").getParent().getId());
		assertEquals("28968", data.getCategory("32958").getParent().getId());
		assertEquals("28937", data.getCategory("28941").getParent().getId());
		assertEquals("33011", data.getCategory("33071").getParent().getId());
		assertEquals("34610", data.getCategory("34727").getParent().getId());
		assertEquals("28947", data.getCategory("33768").getParent().getId()); // Category merging
	}
}
