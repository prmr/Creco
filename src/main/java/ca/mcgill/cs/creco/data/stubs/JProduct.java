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
package ca.mcgill.cs.creco.data.stubs;

/**
 * Stub for a json object. Do not change the field name
 * because they have to match the json keys.
 * Add getters as required.
 */
public class JProduct
{
	private String name;
	private String id;
	private JCategory category;
	private JRating[] ratings;
	private JBrand brand;
	private String modelOverviewPageUrl;
	private float overallScore;
	private String overallScoreDisplayName;
	private String displayName;
	private JRating price;
	private float overallScoreMin;
	private float overallScoreMax;
	private String upc;
	private String mpn;
	private String lows;
	private String description; // text
	private String highs; // text
	private String bottomLine; // text
	private String imageLarge;
	private String imageThumbnail;
	private JRating[] specs;
	private boolean isTested;
	private boolean isBestBuy;
	private boolean isRecommended;
	private boolean isBestSeller;
	private JGroup[] groups;
	private String franchise;
	private String genericColor;
	private String review;  // text
	private String subcategory;
	private String subfranchise;
	private String summary; // text
	private String supercategory;
	private String theCategory;
}
