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
	 * Exposes the products as an unmodifiable collection
	 * @return a collection list of products
	 */
	public Collection<Product> getProducts() 
	{
		return Collections.unmodifiableCollection(aProducts.values());
	}
	
	/**
	 * Searches the CategoryTree to find "meaningful categories".  See the description for
	 * recurseFindEquivalenceClasses for details.
	 */
	void findEquivalenceClasses() 
	{
		for(CategoryBuilder franchise : aRootCategories)
		{
			recurseFindEquivalenceClasses(franchise, 0);
		}
		aHasFoundEquivalenceClasses = true;
	}
	
	/**
	 * Searches the CategoryTree to find "meaningful categories".  These meaningful
	 * categories, or "equivalence classes" contain collections of products which are in
	 * some sense substitutes.  This implementation judges whether a given CategoryBuilder
	 * is an equivalence class by inspecting the Jaccard index of the category, which 
	 * is one way of measuring the similarity between the products of each of its child
	 * CategoryBuilders.  As an example, `Humidifier` is an equivalence class, having 
	 * children `Console Models` and `Tabletop Models`, whose products are similar.
	 * 
	 * This method recursively traverses the CategoryTree's tree structure, from roots towards 
	 * leaves.  As the algorithm
	 * descends, the first CategoryBuilder encountered within a given lineage that has a 
	 * Jaccard index above JACCARD_THRESHOLD is considered an "equivalence class", and is 
	 * added to the aEquivalenceClass ArrayList.
	 * 
	 * Note that "lineage" is intended to denote any series of CategoryBuilders which form a 
	 * that form simple path beginning from a CategryBuilder from aRootCategories.
	 * 
	 * After an equivalence class is identified, the recursion continues deeper, but 
	 * the further recursive calls do not test the Jaccard index, and are simply added 
	 * the CategoryBuilders to the aSubEquivalenceClass ArrayList.
	 * 
	 * If leaf CategoryBuilders are not subsumed under a CategoryBuilder identified as an 
	 * "equivalence class", then the leaf CategoryBuilder is taken to be an equivalence class
	 * itself.  Note that leaf CategoryBuilders, which have no children, do not have a 
	 * meaningful Jaccard index.
	 *  
	 * Explaining the role of pMode:  
	 * This function behaves differently if it is operating on a CategoryBuilder that has an
	 * ancestor already identified to be an equivalence class than if it operates on one
	 * has not.  The second parameter, pMode, is used to provide this articulation.
	 * When pMode is 0, the function behaves as if it has not yet found an equivalence class in
	 * the ancestry of the CategoryBuilder on which it is operating, and does test for 
	 * one.  When pMode is 1, the function behaves as if it has already found an equivalence 
	 * class, and so simply adds subsequent categories to the aSubEquivalenceClasses arrayList
	 * without testing the Jaccard index. 
	 * 
	 * @param pCategory
	 * @param pMode
	 */
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
	
	/**
	 * Adds the plain-category projection of the passed CategoryBuilder to the local \
	 * index of plain categories.  This was originally taken from CRData and may not be 
	 * needed here
	 * @param pCategory
	 */
	private void index(CategoryBuilder pCategory)
	{
		aCategoryIndex.put(pCategory.getId(), pCategory);
		for(CategoryBuilder child : pCategory.getChildren())
		{
			index(child);
		}
	}
	
	/**
	 * Aggregates information about the ratings and specs for all products under a given
	 * category, and aggregates the counts of the number of tested products and rated
	 * products under a given category.  See recursiveRefresh() for more details.
	 */
	void refresh() 
	{			
		// Now recursively refresh the category list
		for(CategoryBuilder franchise : aRootCategories)
		{
			recursiveRefresh(franchise, 0);
		}
	}
	
	/**
	 * One of the purposes of CategoryTree is to aggregate information about the ratings
	 * and specs for all the products under a given category.  This is done recursively:
	 * a non-leaf category aggregates information about the specs and ratings of all its
	 * children.  Thus, this information must be "rolled up", from leaves to root, which
	 * is accomplished here recursion.
	 * 
	 * This method also "rolls up" the counts of the number of tested products, and the
	 * number of rated products under a given category.
	 * 
	 * @param pCategory
	 * @param pDepth
	 */
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
	
	/**
	 * This method removes "singletons" from the category tree structure.
	 * 
	 * Within the category tree structure, there are cases where a given category has a 
	 * single child category.  Such a category, having a single child, is what is being
	 * called a "singleton" here -- not to be confused with the programmatic design pattern.  
	 * Singletons are redundant, and interfere with the current implementation of 
	 * findEquivalenceClasses().
	 * 
	 * @param pCategory
	 */
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
