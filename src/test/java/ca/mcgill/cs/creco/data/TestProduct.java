package ca.mcgill.cs.creco.data;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author enewe101
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test-context.xml"})
public class TestProduct 
{
	@Autowired
	IDataStore aDataStore;
	
	//Ids for products known to have dead URLs
	String[] aDeadLinkedProductIds = 
	{
		"202429",  "120024",  "139738",  "87588",  "230084",  "210251",  
		"256599",  "256494",  "256661",  "256187",  "238640",  "231129",  
		"255995",  "239143",  "195257",  "211417",  "156120",  "199094",  
		"50386",  "204872",  "158381",  "204692",  "230044",  "219123",  
		"230555",  "221186",  "221225",  "221265",  "226981",  "251053",  
		"158587",  "175593",  "177983",  "212260",  "222320",  "191121",  
		"210268",  "201720",  "199904",  "7619",  "6860",  "237924",  
		"238439",  "254552",  "234218",  "199920",  "207692",  "207775",  
		"207597",  "227342",  "248173",  "251034",  "218864",  "219892",  
		"226025"
	};
	
	// Ids for products known to have working URLs
	String[] aLiveLinkedProductIds =
	{
		"8291",  "202523",  "155545",  "123719",  "192581",  "240966",  
		"241871",  "242109",  "237802",  "233548",  "238964",  "237482",  
		"242381",  "241572",  "254569",  "236967",  "235257",  "233158",  
		"246995",  "235323",  "239264",  "243677",  "238537",  "234442",  
		"239417",  "233372",  "245458",  "234615",  "244643",  "244186",  
		"238683",  "233534",  "237387",  "238490",  "233228",  "239471",  
		"248892",  "230772",  "224717",  "220015",  "217474",  "191548",  
		"218573",  "218146",  "229217",  "226127",  "218421",  "141020",  
		"220398",  "197819",  "223872",  "239445",  "239038",  "238924",  
		"240956",  "127081"
	};

	@Test public void testProductLinks()
	{
		// Verify that products having dead URLs return the empty string as their URL
		for(String deadProductId : aDeadLinkedProductIds)
		{
			String deadUrl = aDataStore.getProduct(deadProductId).getUrl();
			assertEquals(deadUrl, "");
		}

		// Verify that products having live URLs return a URL starting with "http"
		for(String liveProductId : aLiveLinkedProductIds)
		{
			String liveUrl = aDataStore.getProduct(liveProductId).getUrl();
			String protocol = liveUrl.substring(0,4);
			assertEquals(protocol, "http");
		}
	}
}
