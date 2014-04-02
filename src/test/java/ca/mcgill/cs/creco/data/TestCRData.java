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
	
	// TODO: Add test to CRData: check that cat = cat.getProducts()[0].getCategory()

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
	
// Testing all the counts of products
		@Test public void testallcounts()
		{
			assertEquals(322,aDataStore.getCategories().size());
			assertEquals(24268,aDataStore.getProducts().size());
			for(Category categoryname: aDataStore.getCategories())
				assertEquals(categoryname.getNumberOfProducts(),categoryname.getProducts().size());

		}
	// Testing the ratings and specifications counts with matching ratings and specifications	
		@Test public void testratingsandspecs()
		{
			Category category = null;
			
			category= aDataStore.getCategory("32968");
			//assertEquals(6,category.getRatings().size());
//			assertEquals("Output",category.getRating("4556").getAttribute().getName());
//			assertEquals("Noise",category.getRating("767").getAttribute().getName());
//			//assertEquals(20,category.getSpecifications().size());
//			assertEquals("Claimed maximum run time",category.getSpecification("6882").getAttribute().getName());
//			assertEquals("Claimed humidification area",category.getSpecification("4561").getAttribute().getName());
//			assertEquals("Cord length",category.getSpecification("3646").getAttribute().getName());
//			assertEquals("Color",category.getSpecification("6").getAttribute().getName());
//
//			
//			category= aDataStore.getCategory("28726");
//			//assertEquals(14,category.getRatings().size());
//			//assertEquals("Broadband data",category.getRating("2656").getAttribute().getName());
//			assertEquals("Display diagonal size",category.getRating("6626").getAttribute().getName());
//			assertEquals("Messaging",category.getRating("3594").getAttribute().getName());
//			//assertEquals(34,category.getSpecifications().size());
//			assertEquals("Speakerphone",category.getSpecification("135").getAttribute().getName());
//			assertEquals("Mac compatible",category.getSpecification("2210").getAttribute().getName());
//			assertEquals("GPS navigation",category.getSpecification("2747").getAttribute().getName());
//			assertEquals("Case style",category.getSpecification("2649").getAttribute().getName());
//			assertEquals("Carrier used",category.getSpecification("2648").getAttribute().getName());
//			assertEquals("Full retail price",category.getSpecification("7150").getAttribute().getName());
//			assertEquals("QWERTY keyboard",category.getSpecification("2799").getAttribute().getName());
//			assertEquals("Height",category.getSpecification("6934").getAttribute().getName());
//			
//			category= aDataStore.getCategory("28701");
//			//assertEquals(12,category.getRatings().size());
//			assertEquals("Ergonomics",category.getRating("2318").getAttribute().getName());
//			assertEquals("Weight",category.getRating("6464").getAttribute().getName());
//			assertEquals("Touchscreen",category.getRating("4512").getAttribute().getName());
//			assertEquals("Portability",category.getRating("2703").getAttribute().getName());
//			//assertEquals(40,category.getSpecifications().size());
//			assertEquals("Built-in webcam",category.getSpecification("2598").getAttribute().getName());
//			assertEquals("Digital video out",category.getSpecification("4134").getAttribute().getName());
//			assertEquals("Tech support length",category.getSpecification("7018").getAttribute().getName());
//			assertEquals("FireWire 800 port",category.getSpecification("4135").getAttribute().getName());
//			assertEquals("Operating system (as tested)",category.getSpecification("6963").getAttribute().getName());
//			assertEquals("Optical drive",category.getSpecification("2790").getAttribute().getName());
//			assertEquals("Thunderbolt port",category.getSpecification("6338").getAttribute().getName());
//			assertEquals("DVI video out",category.getSpecification("2615").getAttribute().getName());
//			assertEquals("WiMAX",category.getSpecification("4869").getAttribute().getName());
//			assertEquals("Gigabit Ethernet",category.getSpecification("2613").getAttribute().getName());
//					
//					
//			category= aDataStore.getCategory("32003");
//			//assertEquals(4,category.getRatings().size());
//			assertEquals("Life",category.getRating("3740").getAttribute().getName());
//			assertEquals("Overall score",category.getRating("254").getAttribute().getName());
//			//assertEquals(5,category.getSpecifications().size());
//			assertEquals("Claimed CCA",category.getSpecification("3744").getAttribute().getName());
//			assertEquals("Has removable caps",category.getSpecification("6331").getAttribute().getName());
//			assertEquals("Handle",category.getSpecification("732").getAttribute().getName());
//			
//			
//			category= aDataStore.getCategory("28683");
//			//assertEquals(8,category.getRatings().size());
//			assertEquals("Ergonomics",category.getRating("2318").getAttribute().getName());
//			assertEquals("Touchscreen",category.getRating("4512").getAttribute().getName());
//			assertEquals("Performance",category.getRating("2333").getAttribute().getName());
//			assertEquals("Overall score",category.getRating("254").getAttribute().getName());
//			//assertEquals(53,category.getSpecifications().size());
//			assertEquals("Warranty length",category.getSpecification("7033").getAttribute().getName());
//			assertEquals("DVI video out",category.getSpecification("2615").getAttribute().getName());
//			assertEquals("Gigabit Ethernet",category.getSpecification("2613").getAttribute().getName());
//			assertEquals("Color",category.getSpecification("6").getAttribute().getName());
//			assertEquals("Voice command",category.getSpecification("2664").getAttribute().getName());
//			assertEquals("Memory-card reader",category.getSpecification("1557").getAttribute().getName());
//			assertEquals("Wi-Fi",category.getSpecification("2948").getAttribute().getName());
//			assertEquals("Touchpad",category.getSpecification("2037").getAttribute().getName());
//			assertEquals("Blu-ray reader",category.getSpecification("3669").getAttribute().getName());
//			assertEquals("SM (SmartMedia) reader",category.getSpecification("3663").getAttribute().getName());
//			assertEquals("TV tuner",category.getSpecification("1218").getAttribute().getName());
//			assertEquals("Free recycling of old PC",category.getSpecification("3665").getAttribute().getName());
//			assertEquals("Wireless keyboard",category.getSpecification("3666").getAttribute().getName());
//			assertEquals("Built-in microphone",category.getSpecification("50").getAttribute().getName());
//			
//			
//			category= aDataStore.getCategory("28727");
//			//assertEquals(9,category.getRatings().size());
//			assertEquals("Convenience",category.getRating("760").getAttribute().getName());
//			assertEquals("Bowl size",category.getRating("6866").getAttribute().getName());
//			assertEquals("Weight",category.getRating("6464").getAttribute().getName());
//			assertEquals("Mixing",category.getRating("2376").getAttribute().getName());
//			//assertEquals(7,category.getSpecifications().size());
//			assertEquals("Single beater style",category.getSpecification("2394").getAttribute().getName());
//			assertEquals("Color",category.getSpecification("6").getAttribute().getName());
//			assertEquals("Style",category.getSpecification("6597").getAttribute().getName());
//			assertEquals("Whisk",category.getSpecification("2370").getAttribute().getName());
//			assertEquals("Detachable hand mixer",category.getSpecification("2380").getAttribute().getName());
			
		}
}
