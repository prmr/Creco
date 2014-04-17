package ca.mcgill.cs.creco.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import ca.mcgill.cs.creco.data.CategoryNode;
import ca.mcgill.cs.creco.data.Attribute;

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
	public void buildCategories()
	{
		// make a root node for the tree (i.e. like a franchise in the CR data set
		CategoryNode rootParent = null;
		aRootCategory = new CategoryNode("rootCategoryId", "rootCategoryName", rootParent);
		
		// Add a subtree containing a singleton.  The singleton should get removed
		CategoryNode singleton = new CategoryNode("singletonId", "singletonName", aRootCategory);
		aRootCategory.addSubcategory(singleton);
		CategoryNode childOfSingleton = new CategoryNode("childOfSingletonId", "childOfSingletonName", singleton);
		singleton.addSubcategory(childOfSingleton);
		CategoryNode leaf0 = new CategoryNode("leaf0Id", "leaf0Name", childOfSingleton);
		childOfSingleton.addSubcategory(leaf0);
		CategoryNode leaf1 = new CategoryNode("leaf1Id", "leaf1Name", childOfSingleton);
		childOfSingleton.addSubcategory(leaf1);
		
		// Add a subtree will have similar leaves, so the subtree should be turned into an equivalence class
		CategoryNode equivalenceClass = new CategoryNode("equivalenceClassId", "equivalenceClassName", aRootCategory);
		aRootCategory.addSubcategory(equivalenceClass);
		CategoryNode similarNode0 = new CategoryNode("similarNode0Id", "similarNode0Name", equivalenceClass);
		equivalenceClass.addSubcategory(similarNode0);
		CategoryNode similarNode1 = new CategoryNode("similarNode1Id", "similarNode1Name", equivalenceClass);
		equivalenceClass.addSubcategory(similarNode1);
		
		// This subtree has dissimilar leaves, so the subtree shouldn't be turned into an equivalence class
		CategoryNode nonEquivalenceClass = new CategoryNode("nonEquivalenceClassId", "nonEquivalenceClassName", aRootCategory);
		aRootCategory.addSubcategory(nonEquivalenceClass);
		CategoryNode dissimilarNode0 = new CategoryNode("dissimilarNode0Id", "dissimilarNode0Name", nonEquivalenceClass);
		nonEquivalenceClass.addSubcategory(dissimilarNode0);
		CategoryNode dissimilarNode1 = new CategoryNode("dissimilarNode1Id", "dissimilarNode1Name", nonEquivalenceClass);
		nonEquivalenceClass.addSubcategory(dissimilarNode1);
	}

	/**
	 * Make make a set of products for testing purposes.
	 */
	@Before
	public void buildProducts()
	{
		int numProducts = 3; 

		// Make some products for each of the leaves below the singleton-child
		int numSingletonLeaves = 2;
		for(int i=0; i<numSingletonLeaves; i++)
		{
			for(int j = 0; j<numProducts; j++)
			{
				// Make attributes for the singleton-child's product
				ArrayList<Attribute> testAttributes = new ArrayList<Attribute>();
				testAttributes.add(Attribute.buildSpecification("specId" + i, "specName" + i, "Number of pixels per inch", 72));
				testAttributes.add(Attribute.buildSpecification("ratingId" + i, "ratingName" + i, "Ease of use", 5));
				testAttributes.add(Attribute.buildPrice("priceId", "priceName", "Typical retail price", 99.99));
	
				// Make a product for the singleton-child and add it to the collection of test products
				aTestProducts.add(new Product("productIdSingleton" + i + "-" + j, "productNameSingleton" + i + "-" + j, true, 
					"leaf"+i+"Id", "brandName", "http://www.example.com", testAttributes,"",""));
			}
		}
		
		// Make some products for each of the leaves below the equivalence-class
		int numEqLeaves = 2;
		for(int i=0; i<numEqLeaves; i++)
		{
			for(int j = 0; j<numProducts; j++)
			{
				// Make attributes for the singleton-child's product. 
				// Note they are the same for each product made
				ArrayList<Attribute> testAttributes = new ArrayList<Attribute>();
				testAttributes.add(Attribute.buildSpecification("specIdEq", "specName", "Number of pixels per inch", 72));
				testAttributes.add(Attribute.buildSpecification("ratingIdEq", "ratingName", "Ease of use", 5));
				testAttributes.add(Attribute.buildPrice("priceId", "priceName", "Typical retail price", 99.99));
	
				// Make a product for the singleton-child and add it to the collection of test products
				aTestProducts.add(new Product("productIdEq" + i + "-" + j, "productNameEq" + i + "-" + j, 
					true, "similarNode" + i + "Id", "brandName", "http://www.example.com", testAttributes,"",""));
			}
		}

		// Make some products for each of the leaves below the non-equivalence-class
		int numNonEqLeaves = 2;
		for(int i=0; i<numNonEqLeaves; i++)
		{
			for(int j = 0; j<numProducts; j++)
			{
				// Make attributes for the singleton-child's product. 
				// Note two of these three attributes will have different attribute ids, depending on the equivalence class leaf
				ArrayList<Attribute> testAttributes = new ArrayList<Attribute>();
				testAttributes.add(Attribute.buildSpecification("specIdEq" + i, "specName" + i, "Number of pixels per inch", 72));
				testAttributes.add(Attribute.buildSpecification("ratingIdEq" + i, "ratingName" + i, "Ease of use", 5));
				testAttributes.add(Attribute.buildPrice("priceId", "priceName", "Typical retail price", 99.99));
	
				// Make a product for the singleton-child and add it to the collection of test products
				aTestProducts.add(new Product("productIdNonEq" + i + "-" + j, "productNameNonEq" + i + "-" + j, true, 
					"dissimilarNode" + i + "Id", "brandName", "http://www.example.com", testAttributes,"",""));
			}
		}
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
		CategoryTree catTree = new CategoryTree();
		catTree.addCategory(aRootCategory);
		catTree.indexRootCategories();
		
		for(Product prod : aTestProducts)
		{
			catTree.addProduct(prod);
		}
		
		// verify that the products are there
		assertEquals(catTree.getProducts().size(), 18);
		
		// verify that the products are not yet associated
		for(CategoryNode depth1Cat: aRootCategory.getChildren())
		{
			// This branch of the tree goes deeper than the others
			Iterable<CategoryNode> children;
			if(depth1Cat.getId().equals("singletonId"))
			{
				CategoryNode depth2Cat = depth1Cat.getChildren().iterator().next();
				children = depth2Cat.getChildren(); 
			}
			else
			{
				children = depth1Cat.getChildren();
			}
			
			for(CategoryNode leaf: children)
			{
				assertEquals(0, leaf.getCount());
			}
		}
		
		// run a method to associate products to their categories
		catTree.associateProducts();
		
		// verify that the products have now been associated
		CategoryNode dissimilarNode1 = null;
		for(CategoryNode depth1Cat: aRootCategory.getChildren())
		{
			// This branch of the tree goes deeper than the others
			Iterable<CategoryNode> children;
			if(depth1Cat.getId().equals("singletonId"))
			{
				CategoryNode depth2Cat = depth1Cat.getChildren().iterator().next();
				children = depth2Cat.getChildren(); 
			}
			else
			{
				children = depth1Cat.getChildren();
			}
			
			for(CategoryNode leaf: children)
			{
				assertEquals(3, leaf.getCount());
				
				// Keep a reference to one of the leaves for a more detailed spot-check
				if(leaf.getId().equals("dissimilarNode1Id"))
				{
					dissimilarNode1 = leaf;
				}
			}
		}
		
		
		// Spot check that the children are in the right places
		boolean found0 = false, found1 = false, found2 = false;
		for(Product prod : dissimilarNode1.getProducts())
		{
			if(prod.getId().equals("productIdNonEq1-0"))
			{
				found0 = true;
			}
			else if(prod.getId().equals("productIdNonEq1-1"))
			{
				found1 = true;
			}
			else if(prod.getId().equals("productIdNonEq1-2"))
			{
				found2 = true;
			}
			else
			{
				// We should never get here, if we do, it's because a wrong product was associated
				fail("The test product with id " + prod.getId() + " should not have been associated to category " 
					+ dissimilarNode1.getId() + ". (But it was!)");
			}
		}
		assertTrue(found0);
		assertTrue(found1);
		assertTrue(found2);
	}

	@Test public void testRefresh()
	{
		CategoryTree catTree = new CategoryTree();
		catTree.addCategory(aRootCategory);
		catTree.indexRootCategories();
		
		for(Product prod : aTestProducts)
		{
			catTree.addProduct(prod);
		}
		
		// verify that the products are there
		assertEquals(catTree.getProducts().size(), 18);
		
		// verify that the root category has no products
		assertEquals(0, aRootCategory.getCount());
		
		// run a method to associate products to their categories
		catTree.associateProducts();
		
		// Now refresh
		catTree.refresh();

		// The root should have all the products now
		assertEquals(18, aRootCategory.getCount());
		
		// The Jaccard should have been calculated, but only the price is common to all
		// products.  That means of 11 unique attributes, only 1 is common
		double epsilon = 10e-8;
		assertTrue(1/11.0 - epsilon < aRootCategory.getJaccardIndex());
		assertTrue(1/11.0 + epsilon > aRootCategory.getJaccardIndex());
	}

	@Test public void testFindEquivalenceClasses()
	{
		CategoryTree catTree = new CategoryTree();
		catTree.addCategory(aRootCategory);
		catTree.indexRootCategories();
		catTree.eliminateAllSingletons();
		for(Product prod : aTestProducts)
		{
			catTree.addProduct(prod);
		}
		
		catTree.associateProducts();
		catTree.refresh();

		// At this point the CategoryTree is basically loaded and sync'd, but equivalence classes
		// have not been calculated yet.
		assertNull(catTree.getCategories());
		
		// run a method to find the equivalence classes
		catTree.findEquivalenceClasses();
		
		// Now we will have equivalence classes
		String[] expectedEquivalenceClasses = 
		{
			"leaf0Id", "leaf1Id", "equivalenceClassId", "dissimilarNode0Id", "dissimilarNode1Id"
		};
		assertEquals(expectedEquivalenceClasses.length, catTree.getCategories().size());
		for(CategoryNode cat : catTree.getCategories())
		{
			assertTrue(Arrays.asList(expectedEquivalenceClasses).contains(cat.getId()));
		}
	}

}