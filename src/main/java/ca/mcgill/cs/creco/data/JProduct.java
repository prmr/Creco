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
package ca.mcgill.cs.creco.data;

public class JProduct
{
	public String name;
	public String id;
	public JCategory category;
	private JRating[] ratings;
	public JBrand brand;
	private String modelOverviewPageUrl;
	private float overallScore;
	private String overallScoreDisplayName;
	public String displayName;
	private JRating price;
	private float overallScoreMin;
	private float overallScoreMax;
	private String upc;
	private String mpn;
	private String lows;
	public String description; // text
	public String highs; // text
	public String bottomLine; // text
	private String imageLarge;
	private String imageThumbnail;
	private JRating[] specs;
	public boolean isTested;
	private boolean isBestBuy;
	public boolean isRecommended;
	private boolean isBestSeller;
	private JGroup[] groups;
	private String franchise;
	private String genericColor;
	public String review;  // text
	private String subcategory;
	private String subfranchise;
	public String summary; // text
	private String supercategory;
	private String theCategory;

	public String toString()
	{
		return bottomLine;
	}
}
