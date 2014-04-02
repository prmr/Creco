package ca.mcgill.cs.creco.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;

import ca.mcgill.cs.creco.data.CategoryNode;

import org.junit.Before;
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
public class TestCategoryTree 
{	
	@Autowired
	IDataStore aDataStore;
	CategoryNode aRootCategory;
	ArrayList<Product> aTestProducts = new ArrayList<Product>();
	
	/**
	 * Make some basic materials that will be used to construct tests.  We make a set of categories that imply a 
	 * Tree which should produce various testable behaviors in CategoryTree.  We also need to make a set of 
	 * test products to go with it.
	 */
	@Before
	public void setupTests()
	{
		
		// make a root node for the tree (i.e. like a franchise in the CR data set
		CategoryNode rootParent = null;
		aRootCategory = new CategoryNode("rootCategoryId", "rootCategoryName", rootParent);
		
		// Add a subtree containing a singleton.  The singleton should get removed
		CategoryNode singleton = new CategoryNode("singletonId", "singletonName", aRootCategory);
		aRootCategory.addSubcategory(singleton);
		CategoryNode childOfSingleton = new CategoryNode("childOfSingletonId", "childOfSingletonName", singleton);
		singleton.addSubcategory(childOfSingleton);
		CategoryNode leaf1 = new CategoryNode("leaf1Id", "leaf1Name", childOfSingleton);
		childOfSingleton.addSubcategory(leaf1);
		CategoryNode leaf2 = new CategoryNode("leaf2Id", "leaf2Name", childOfSingleton);
		childOfSingleton.addSubcategory(leaf2);
		
		// Add a subtree will have similar leaves, so the subtree should be turned into an equivalence class
		CategoryNode equivalenceClass = new CategoryNode("equivalenceClassId", "equivalenceClassName", aRootCategory);
		aRootCategory.addSubcategory(equivalenceClass);
		CategoryNode similarNode1 = new CategoryNode("similarNode1Id", "similarNode1Name", equivalenceClass);
		equivalenceClass.addSubcategory(similarNode1);
		CategoryNode similarNode2 = new CategoryNode("similarNode2Id", "similarNode2Name", equivalenceClass);
		equivalenceClass.addSubcategory(similarNode2);
		
		// This subtree has dissimilar leaves, so the subtree shouldn't be turned into an equivalence class
		CategoryNode nonEquivalenceClass = new CategoryNode("nonEquivalenceClassId", "nonEquivalenceClassName", aRootCategory);
		aRootCategory.addSubcategory(nonEquivalenceClass);
		CategoryNode dissimilarNode1 = new CategoryNode("disimilarNode1Id", "dissimilarNode1Name", nonEquivalenceClass);
		nonEquivalenceClass.addSubcategory(dissimilarNode1);
		CategoryNode dissimilarNode2 = new CategoryNode("disimilarNode2Id", "dissimilarNode2Name", nonEquivalenceClass);
		nonEquivalenceClass.addSubcategory(dissimilarNode2);
				
	}
	
	@Test public void testEliminateSingletons()
	{
		CategoryTree catTree = new CategoryTree();
		catTree.addCategory(aRootCategory);
		String[] expectedFirstChildIds = {"singletonId", "equivalenceClassId", "nonEquivalenceClassId"};
		
		// Confirm that all the expected depth-1 categories are there
		assertEquals(aRootCategory.getNumberOfChildren(), 3);
		boolean hasSingleton = false;
		for(CategoryNode child : aRootCategory.getChildren())
		{
			assertTrue(Arrays.asList(expectedFirstChildIds).contains(child.getId()));
			if(child.getId().equals("singletonId"))
			{
				hasSingleton = true;
			}
		}
		
		// Make sure that the singleton child is among the children
		assertTrue(hasSingleton);
	
		// Run a method that should eliminate the singleton child
		catTree.eliminateAllSingletons();
		
		String[] newExpectedFirstChildIds = {"childOfSingletonId", "equivalenceClassId", "nonEquivalenceClassId"};
		
		// Confirm that all the expected depth-1 categories are there
		// The singleton shouldn't be there anymore
		assertEquals(aRootCategory.getNumberOfChildren(), 3);
		boolean hasSingletonChild = false;
		for(CategoryNode child : aRootCategory.getChildren())
		{
			assertTrue(Arrays.asList(newExpectedFirstChildIds).contains(child.getId()));
			if(child.getId().equals("childOfSingletonId"))
			{
				hasSingletonChild = true;
			}
		}
		
		// Make sure that the singleton's child is among the children
		assertTrue(hasSingletonChild);
	}
	
	@Test public void testAssociateProducts()
	{
	}

	@Test public void testRefresh()
	{
	}

	@Test public void testFindEquivalenceClasses()
	{
	}

}
