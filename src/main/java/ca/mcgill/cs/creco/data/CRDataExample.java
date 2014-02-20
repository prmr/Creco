//package ca.mcgill.cs.creco.data;
//
//import java.io.IOException;
//
//public class CRDataExample {
//	public static void main(String[] args) throws IOException
//	{
//		
//		// Get the path to the data
//		String dataPath = DataPath.get();
//
//		// Build the CRData as a Java Object
//		CRData crData = new CRData(dataPath);
//		
//		
//		
//		// ====================================
//		// That's it.  Now you can use the data
//		// let's look at some basic examples:
//		// ====================================
//
//		CategoryList catList = crData.getCategoryList();
//		ProductList prodList = crData.getProductList();
//		
//		// Quickly dump the category tree in human-readable form debugging purposes
//		System.out.println(catList.dumpTree());
//		System.out.println("\nSometimes its helpful, for debugging, just to see the category tree");
//		System.out.println("The above was output using catList.dumpTree()\n");
//		System.out.println("(E) indicates an equivalence class, and (e) is a subEquivalence class.");
//		
//		// Quickly get an overview of a particular category.  Also handy for debugging odd cases.
//		System.out.println("\nWant to quickly see the stats for a category? Use Category.describe()");
//		System.out.println("Below you can see all the different ratings and specs that products in the category have.");
//		System.out.println("It also shows the number of products that actually include them. See the source for getters.");
//		System.out.println("Hopefully, this is a leg up for feature selection.  Obviously more clever tricks will be needed though!");
//		
//		System.out.println(catList.get("32968").describe());
//		System.out.println(catList.get("34382").describe());
//		System.out.println(catList.get("34381").describe());
//		
//		// Accessing the products of a category; accessing the category of a product
//		System.out.println("You can access products from the category, or category from the products");
//		Category humidifiers = catList.get("32968");
//		Product aHumidifier = humidifiers.getProducts().iterator().next();
//		System.out.println(humidifiers.getName() + " has " + humidifiers.getNumProducts() + " products in it.");
//		System.out.println(humidifiers.getName() + " contains " + aHumidifier.getName());
//		System.out.println(aHumidifier.getName() + " is contained by " + aHumidifier.getCategory().getName());
//		
//		// Get the immediate child categories
//		System.out.println("\nYou can access the children of a category using getChildren.");
//		Category ranges = catList.get("28974");
//		System.out.println("For example, " + ranges.getName() + " contains:");
//		for(Category child : ranges.getChildren())
//		{
//			System.out.println(" - " + child.getName());
//		}
//		
//		// Traverse upwards too
//		Category rangeChild = ranges.getChildren().iterator().next();
//		System.out.println("\nTraverse upwards too:");
//		System.out.println("The parent of " + rangeChild.getName() + " is " + rangeChild.getParent().getName());
//		
//		// Generalized getters provide access to all CR data fields
//		System.out.println("\nThe original CR data has many fields!  I didn't write getters for them all.");
//		System.out.println("You can still access them using generic accessors, e.g. product.getString(\"modelOverviewPageUrl\")");
//		System.out.println("The page for " + aHumidifier.getName() + " is " + aHumidifier.getModelOverviewPageUrl());
//		System.out.println("If that annoys you, feel free to write a getter!");
//		
//		// Work on a smaller data set for faster debugging
//		System.out.println("\nIt can be annoying to wait for the product files to get read.");
//		System.out.println("At the moment CRData accepts a second parameter to tell it which product files to load.");
//		System.out.println("This isn't working yet because I haven't made it smart enough to eliminate the empty categories,");
//		System.out.println("which leads to null pointer exceptions.  Comming soon!");
//		// CRData smallCRData = new CRData(dataPath, new String[] {"product_homeGarden.json"});
//	}
//}

