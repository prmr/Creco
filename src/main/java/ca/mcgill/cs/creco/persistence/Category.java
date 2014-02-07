package ca.mcgill.cs.creco.persistence;

import org.json.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;

public class Category {
	public int materialsCount;
	public String pluralName;
	public String parent; 
	public String productGroupId; 
	public String url;
	public String name;
	public String id;
	public String imageCanonical;
	public Integer depth;
	public Integer productsCount;
	public Integer testedProductsCount;
	public String singularName;
	public String franchise;
	public Integer servicesCount;
	public String type;
	public String[] children;
	public Integer ratedProductsCount;
	
	public void setString(String key, String val) {
		if(key.equals("singularName")) {
			this.singularName = val;
		} else if (key.equals("pluralName")) {
			this.pluralName = val;
		} else if (key.equals("parent")) {
			this.parent = val;
		} else if(key.equals("productGroupId")) {
			this.productGroupId = val;
		} else if(key.equals("url")) {
			this.url = val;
		} else if(key.equals("name")) {
			this.name = val;
		} else if(key.equals("id")) {
			this.id = val;
		} else if(key.equals("imageCanonical")) {
			this.imageCanonical = val;
		} else if (key.equals("franchise")) {
			this.franchise = val;
		} else if (key.equals("type")) {
			this.type = val;
		}
	}
	
	public void setInt(String key, Integer val) {
		if(key.equals(materialsCount)) {
			this.materialsCount = val;
		} else if(key.equals(depth)) {
			this.depth = val;
		} else if(key.equals(productsCount)) {
			this.productsCount = val;
		} else if(key.equals(testedProductsCount)) {
			this.testedProductsCount = val;
		} else if(key.equals(servicesCount)) {
			this.servicesCount = val;
		} else if(key.equals(ratedProductsCount)) {
			this.ratedProductsCount = val;
		}
	}

	public void set_parent(String p) {
		this.parent = p;
	}
	
	public void setChildren(String[] children) {
		this.children = children.clone();
	}
	
	public String get_id() {
		return this.id;
	}
	
	public String get_parent() {
		return this.parent;
	}
	
	public String get_pluralName() {
		return this.pluralName;
	}
	
	public String get_url() {
		return this.url;
	}
	
	public String get_name() {
		return this.name;
	}
	
	public String getSingularName() {
		return this.singularName;
	}
	
	public String get_image() {
		return this.imageCanonical;
	}
	
	public String[] getChildren() {
		return this.children.clone();
	}
	
	public String get_type() {
		return this.type;
	}
}

