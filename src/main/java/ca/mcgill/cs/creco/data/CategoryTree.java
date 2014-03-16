package ca.mcgill.cs.creco.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * An object of this class is used to collect category nodes 
 * organized in a tree structure and process them to flatten the 
 * category tree into a set of meaning full product categories.
 * 
 * Using instances of this class requires following this protocol:
 * <ol>
 * <li>Calling <tt>addNode</tt></li>
 * <li>Calling the indexing and processing methods</li>
 * <li>Obtaining the list of categories</li>
 * </ol>
 */
public class CategoryTree implements IDataCollector
{
	private static final double JACCARD_THRESHOLD = 0.8;
	private HashMap<String, Product> aProducts = new HashMap<String, Product>();
	private Hashtable<String, CategoryBuilder> aCategoryIndex = new Hashtable<String, CategoryBuilder>();
	private ArrayList<CategoryBuilder> aRootCategories = new ArrayList<CategoryBuilder>();					// Top-level categories
	private ArrayList<CategoryBuilder> aEquivalenceClasses = new ArrayList<CategoryBuilder>();
	private ArrayList<CategoryBuilder> aSubEquivalenceClasses = new ArrayList<CategoryBuilder>();
	
	private boolean aHasFoundEquivalenceClasses = false;
	
	public CategoryTree() 
	{}
	
	
	/**
	 * Returns a collection of plain Category objects.  Must be called after 
	 * findEquivalenceClasses()
	 * @return 
	 */
	public Collection<Category> getCategories()
	{
		// Must be called after findEquivalenceClasses()
		if(!aHasFoundEquivalenceClasses)
		{
			return null;
		}
		
		// Make a collection of plain Categorys from the list of "EquivalenceClass" Categorys
		Collection<Category> lCategories = new ArrayList<Category>();
		for(CategoryBuilder lCatBuilder : aEquivalenceClasses)
		{
			lCategories.add(lCatBuilder.getCategory());
		}
		return lCategories;
	}

	/**
	 * Adds the root categories (categoriBuilders) to the index.  Recursively adds child
	 * categoryBuilders.
	 */
	void indexRootCategories() {
		for(CategoryBuilder category : aRootCategories)
		{
			index(category);
		}
	}
	
	/**
	 * Adds the root categories (categoriBuilders) to the index.  Recursively adds child
	 * categoryBuilders.
	 */
	void eliminateAllSingletons() {
		for(CategoryBuilder category : aRootCategories)
		{
			eliminateSingletons(category);
		}
	}
	
	@Override
	public void addCategory(CategoryBuilder pCategory)
	{
		aRootCategories.add(pCategory);
	}
	
	@Override
	public void addProduct(Product pProduct)
	{
		aProducts.put(pProduct.getId(), pProduct);
	}

	/**
	 * Not sure if this is still needed, maybe delete
	 */
	public Collection<Product> getProducts() 
	{
		return Collections.unmodifiableCollection(aProducts.values());
	}

	void findEquivalenceClasses() 
	{
		for(CategoryBuilder franchise : aRootCategories)
		{
			recurseFindEquivalenceClasses(franchise, 0);
		}
		aHasFoundEquivalenceClasses = true;
	}
	
	private void recurseFindEquivalenceClasses(CategoryBuilder pCategory, int pMode)
	{
		Iterable<CategoryBuilder> children = pCategory.getChildren();
		int numChildren = pCategory.getNumberOfChildren();
		
		if(pMode == 1)
		{
			aSubEquivalenceClasses.add(pCategory);
			for(CategoryBuilder child : children)
			{
				recurseFindEquivalenceClasses(child, 1);
			}
			return;
		}
		else
		{
			if(numChildren == 0)
			{
				aEquivalenceClasses.add(pCategory);
				aSubEquivalenceClasses.add(pCategory);
				return;
			}
			else
			{
				Double jaccard = pCategory.getJaccardIndex();
				if(jaccard == null || jaccard < JACCARD_THRESHOLD)
				{
					for(CategoryBuilder child : children)
					{
						recurseFindEquivalenceClasses(child, 0);
					}
					return;
				}
				else
				{
					aEquivalenceClasses.add(pCategory);
					for(CategoryBuilder child : children)
					{
						recurseFindEquivalenceClasses(child, 1);
					}
					return;
				}
			}
		}
	}
	
	private void index(CategoryBuilder pCategory)
	{
		aCategoryIndex.put(pCategory.getId(), pCategory);
		for(CategoryBuilder child : pCategory.getChildren())
		{
			index(child);
		}
	}
	
	void refresh() 
	{			
		// Now recursively refresh the category list
		for(CategoryBuilder franchise : aRootCategories)
		{
			recursiveRefresh(franchise, 0);
		}
	}
	
	private void recursiveRefresh(CategoryBuilder pCategory, int pDepth)
	{
		for(CategoryBuilder child : pCategory.getChildren()) 
		{
			recursiveRefresh(child, pDepth + 1);
		}
		
		// We will be "rolling up" counts and various collections from the leaves up to the
		// roots (franchises).  Make sure, for all non-leaves, that these are cleared out to start
		if(pCategory.getNumberOfChildren() > 0)
		{
			pCategory.setRatedCount(0);
			pCategory.setTestedCount(0);
			pCategory.setCount(0);
			pCategory.clearRatings();
			pCategory.clearSpecs();
			pCategory.restartRatingIntersection();
			pCategory.restartSpecIntersection();
		}
	
		// Roll up counts and collections
		for(CategoryBuilder child : pCategory.getChildren())
		{
			// aggregate children's collections
			pCategory.mergeRatings(child.getRatings());
			pCategory.mergeSpecs(child.getSpecifications());
			pCategory.intersectRatings(child);
			pCategory.intersectSpecs(child);
			
			// aggregate children's counts
			pCategory.putProducts(child.getProducts());
			pCategory.incrementRatedCount(child.getRatedCount());
			pCategory.incrementTestedCount(child.getTestedCount());
			pCategory.incrementCount(child.getCount());
		}
		pCategory.calculateJaccard();
	}

	/**
	 * Associate products with categories and vice-versa.
	 * @param pProducts The list of products.
	 */
	void associateProducts() 
	{
		for(Product lProduct : aProducts.values()) 
		{
			CategoryBuilder category = aCategoryIndex.get(lProduct.getCategoryId());
			
			// Create two way link between category and product
			category.addProduct(lProduct);
			lProduct.setCategory(category);
			
			// Aggregate some product info in the category
			category.addRatings(lProduct.getRatings());
			category.putSpecifications(lProduct.getSpecs());
			
			// Increment the counts in this category
			category.incrementCount(1);
			if(lProduct.getIsTested())
			{
				category.incrementTestedCount(1);
			}
			if(lProduct.getNumRatings() > 0)
			{
				category.incrementRatedCount(1);
			}
		}
	}
	
	private void eliminateSingletons(CategoryBuilder pCategory)
	{
		// detect if this is a singleton, if so, splice it out of the hierarchy
		if(pCategory.getNumberOfChildren() == 1)
		{
			CategoryBuilder child = pCategory.getChildren().iterator().next();
			CategoryBuilder parent = pCategory.getParent();
			child.setParent(parent);
			parent.removeChild(pCategory);
			parent.addSubcategory(child);

			eliminateSingletons(child);
		}
		else
		{
			for(CategoryBuilder child : pCategory.getChildren())
			{
				eliminateSingletons(child);
			}
		}
	}
}
