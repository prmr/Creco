package ca.mcgill.cs.creco.web.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class ProductListVO implements Serializable
{

	private static final long serialVersionUID = -5156221733524698009L;
	private ArrayList<ProductVO> products = new ArrayList<ProductVO>();

	public ProductListVO()
	{
		
	}

	public ArrayList<ProductVO> getProducts()
	{
		return products;
	}

	public void setProducts(ArrayList<ProductVO> products)
	{
		this.products = products;
	}
	
}
