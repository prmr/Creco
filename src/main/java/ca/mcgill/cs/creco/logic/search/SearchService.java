package ca.mcgill.cs.creco.logic.search;

import java.util.List;

import ca.mcgill.cs.creco.data.Category;
import ca.mcgill.cs.creco.data.CategoryList;
import ca.mcgill.cs.creco.data.ProductList;

public class SearchService {

	private CategorySearch categorySearch;
	private ProductSearch productSearch;
	
	public SearchService(CategoryList categoryList, ProductList productList)
	{
		this.categorySearch = new CategorySearch(categoryList);
		//this.productSearch = new ProductSearch(productList);
	}
	
	// Should be CategoryList
	public List<Category> searchEquivalenceClasses(String query)
	{
		return categorySearch.queryCategories(query);
	}
	
	/*
	public List<ScoredProduct> searchProducts(String eqId, String query)
	{
		return categorySearch.queryCategories(query);
	}
	*/
}
