package ca.mcgill.cs.creco.util;

import java.io.IOException;

public final class Franchisereturn {
	
private Franchisereturn(){}

public static String[]  getProductString()
{
        return  new String[]{"food.json","health.json","money.json","babiesKids.json","homeGarden.json","appliances.json","cars.json","electronicsComputers.json","category.json"};

}

public static String[]  getProducts()
{
        return  new String[]{"food.json","health.json","money.json","babiesKids.json","homeGarden.json","appliances.json","cars.json","electronicsComputers.json"};

}
public static String getCategoryString()
{
	String category= "category.json";
return category;
}
}