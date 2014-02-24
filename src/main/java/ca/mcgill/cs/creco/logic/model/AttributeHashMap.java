package ca.mcgill.cs.creco.logic.model;

import java.util.HashMap;

import weka.core.FastVector;

public class AttributeHashMap {
	
	private FastVector aVector;
	private HashMap<String, String> aMap;
	

	public AttributeHashMap(FastVector pVector, HashMap<String, String> pMap)
	{
		aVector = pVector;
		aMap = pMap;
	}
	
	public FastVector getVector() {
		return aVector;
	}

	public String getHashValue(String key)
	{
		return aMap.get(key);
	}
	
	
	

}
