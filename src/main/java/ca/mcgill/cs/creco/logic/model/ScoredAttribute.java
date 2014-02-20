/**
 * Copyright 2014 McGill University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.mcgill.cs.creco.logic.model;

import java.util.Comparator;

import ca.mcgill.cs.creco.data.*;

/**
 *
 */
public class ScoredAttribute 
{
	private String aAttributeID;
	private String aAttributeName;
	private double aAttributeScore;
	private AttributeValue aAttributeMean;
	
	
	public static final Comparator<ScoredAttribute> sortByScore = 
	new Comparator<ScoredAttribute>() 
    {
		
		/**
		 * Compare a score placing the highest score first.
		 * @param pA score A
		 * @param pB score B
		 * @return -1 for A > B, 1 for B > A, 0 if A==B
		 */
		@Override
        public int compare(ScoredAttribute pA, ScoredAttribute pB) 
        {
        	if(pA.getAttributeScore() >  pB.getAttributeScore())
        	{
        		return -1;
        	}
        	else if(pA.getAttributeScore() < pB.getAttributeScore())
        	{
        		return 1;
        	}
        	else return 0;
        }

     };
	
	public ScoredAttribute(Attribute pAttribute)
	{
		aAttributeID = pAttribute.getId();
		aAttributeScore = 0.0;
		aAttributeName = pAttribute.getName();
	}
	
	public String getAttributeID() 
	{
		return aAttributeID;
	}

	public double getAttributeScore() 
	{
		return aAttributeScore;
	}

	public void setAttributeScore(double pAttributeScore)
	{
		this.aAttributeScore = pAttributeScore;
	}

	public String getAttributeName() 
	{
		return aAttributeName;
	}

	public void setAttributeName(String pAttributeName) 
	{
		this.aAttributeName = pAttributeName;
	}

	@Override
	public String toString()
	{
		return aAttributeName + ", " + aAttributeID + ": " + aAttributeScore + ", " + aAttributeMean + "||";
	}

	public AttributeValue getAttributeMean() {
		return aAttributeMean;
	}

	public void setAttributeMean(AttributeValue pAttributeMean) {
		this.aAttributeMean = pAttributeMean;
	}
	
	
	
}
