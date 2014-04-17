package ca.mcgill.cs.creco.logic;

import ca.mcgill.cs.creco.data.TypedValue;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class UserScoredAttribute {

	private double aUserScore;
	private ScoredAttribute aScoredAttribute;
	
	
	public static final Comparator<UserScoredAttribute> SORT_BY_CORRELATION = 
			new Comparator<UserScoredAttribute>() 
		    {
				
				/**
				 * Compare a scoredAttribute placing the highest absolute correlation first.
				 * @param pA scoredAttribute A
				 * @param pB scoredAttribute B
				 * @return -1 for A > B, 1 for B > A, 0 if A==B
				 */
				@Override
		        public int compare(UserScoredAttribute pA, UserScoredAttribute pB) 
		        {
		        	if(Math.abs(pA.getCorrelation()) >  Math.abs(pB.getCorrelation()))
		        	{
		        		return -1;
		        	}
		        	else if(Math.abs(pA.getCorrelation()) <  Math.abs(pB.getCorrelation()))
		        	{
		        		return 1;
		        	}
		        	else
		        	{
		        		return 0;
		        	}
		        }

		     };
	public static final Comparator<UserScoredAttribute> SORT_BY_USER_SCORE = 
	new Comparator<UserScoredAttribute>() 
    {
		
		/**
		 * Compare a scoredAttribute placing the highest score first.
		 * @param pA scoredAttribute A
		 * @param pB scoredAttribute B
		 * @return -1 for A > B, 1 for B > A, 0 if A==B
		 */
		@Override
        public int compare(UserScoredAttribute pA, UserScoredAttribute pB) 
        {
        	if(pA.getUserScore() >  pB.getUserScore())
        	{
        		return -1;
        	}
        	else if(pA.getUserScore() < pB.getUserScore())
        	{
        		return 1;
        	}
        	else
        	{
        		return 0;
        	}
        }

     };
     /**
		 * Compare a scoredAttribute placing the highest entropy first.
		 * @param pA scoredAttribute A
		 * @param pB scoredAttribute B
		 * @return -1 for A > B, 1 for B > A, 0 if A==B
		 */
    public static final Comparator<UserScoredAttribute> SORT_BY_ENTROPY = 
    			new Comparator<UserScoredAttribute>() 
    		    {
    				
    				/**
    				 * Compare a score placing the highest score first.
    				 * @param pA score A
    				 * @param pB score B
    				 * @return -1 for A > B, 1 for B > A, 0 if A==B
    				 */
    				@Override
    		        public int compare(UserScoredAttribute pA, UserScoredAttribute pB) 
    		        {
    		        	if(pA.getEntropy() >  pB.getEntropy())
    		        	{
    		        		return -1;
    		        	}
    		        	else if(pA.getEntropy() < pB.getEntropy())
    		        	{
    		        		return 1;
    		        	}
    		        	else
    		        	{
    		        		return 0;
    		        	}
    		        }

    		     };
    		     
    public static final Comparator<UserScoredAttribute> SORT_BY_SCORE = 
      			new Comparator<UserScoredAttribute>() 
      		    {
      				
      				/**
     				 * Compare a scoredAttribute placing the highest score first.
      				 * @param pA scoredAttribute A
      				 * @param pB scoredAttribute B
      				 * @return -1 for A > B, 1 for B > A, 0 if A==B
      				 */
      				@Override
      		        public int compare(UserScoredAttribute pA, UserScoredAttribute pB) 
      		        {
      		        	if(pA.getAttributeScore() >  pB.getAttributeScore())
      		        	{
      		        		return -1;
      		        	}
      		        	else if(pA.getAttributeScore() < pB.getAttributeScore())
      		        	{
      		        		return 1;
      		        	}
      		        	else
      		        	{
      		        		return 0;
      		        	}
      		        }
    		     };
	
	public UserScoredAttribute(ScoredAttribute pScoredAttribute, double pUserScore)
	{
		aScoredAttribute = pScoredAttribute;
		aUserScore = pUserScore;
	}
	
	public TypedValue getAttributeDefault()
	{
		return aScoredAttribute.getAttributeDefault();
	}
	public String getAttributeDesc()
	{
		return aScoredAttribute.getAttributeDesc();
	}
	public String getAttributeID()
	{
		return aScoredAttribute.getAttributeID();
	}
	public String getAttributeName()
	{
		return aScoredAttribute.getAttributeName();
	}
	public double getAttributeScore()
	{
		return aScoredAttribute.getAttributeScore();
	}
	public double getCorrelation()
	{
		return aScoredAttribute.getCorrelation();
	}
	public List<TypedValue> getDict()
	{
		return aScoredAttribute.getDict();
	}
	public ScoredAttribute.Direction getDirection()
	{
		return aScoredAttribute.getDirection();
	}
	public double getEntropy()
	{
		return aScoredAttribute.getEntropy();
	}
	public Map<String,Double> getLabelMeanScores()
	{
		return aScoredAttribute.getLabelMeanScores();
	}
	public TypedValue getMax()
	{
		return aScoredAttribute.getMax();
	}
	public TypedValue getMin()
	{
		return aScoredAttribute.getMin();
	}
	public double getTypeThreshold()
	{
		return aScoredAttribute.getTypeThreshold();
	}
	public int getValueRank(TypedValue pValue)
	{
		return aScoredAttribute.getValueRank(pValue);
	}
	public double getValueScore(TypedValue pValue)
	{
		return aScoredAttribute.getValueScore(pValue);
	}
	public boolean isBoolean()
	{
		return aScoredAttribute.isBoolean();
	}
	public boolean isNA()
	{
		return aScoredAttribute.isNA();
	}
	public boolean isNull()
	{
		return aScoredAttribute.isNull();
	}
	public boolean isNumeric()
	{
		return aScoredAttribute.isNumeric();
	}
	public boolean isString()
	{
		return aScoredAttribute.isString();
	}
	
	public ScoredAttribute getScoredAttribute()
	{
		return aScoredAttribute;
	}
	public double getUserScore()
	{
		return aUserScore;
	}
}
