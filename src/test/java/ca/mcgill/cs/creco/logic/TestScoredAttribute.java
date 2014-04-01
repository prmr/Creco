package ca.mcgill.cs.creco.logic;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.mcgill.cs.creco.data.Attribute;
import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.IDataStore;
import ca.mcgill.cs.creco.data.Product;

public class TestScoredAttribute {

	@Autowired
	IDataStore aDataStore;
	
	@Autowired
	AttributeExtractor aAttributeExtractor;
	
	@Autowired
	ProductRanker aProductRanker;
	
	@Test
	public void testSorting() 
	{
	}
	
}
