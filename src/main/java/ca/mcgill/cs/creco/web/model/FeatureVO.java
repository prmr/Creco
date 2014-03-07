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
package ca.mcgill.cs.creco.web.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import ca.mcgill.cs.creco.logic.TypedValue;
/*
 * A class representing a single feature (i.e., rating and spec) to be 
 * displayed to the user front end.
 * */
@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class FeatureVO
{
	private String id;
	private String name;
	private double score;
	private String type;
	private ArrayList<String> value;
	private String uValue;	
	private Number minValue=0;
	private Number maxValue=50;
	private Boolean visible;
	private Boolean spec;
	private Boolean rate;
//	private AttribureValue mean;
	

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ArrayList<String> getValue() {
		return value;
	}

	public void setValue(ArrayList<String> value) {
		this.value = value;
	}

	public Number getMinValue() {
		return minValue;
	}

	public void setMinValue(Number minValue) {
		this.minValue = minValue;
	}

	public Number getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Number maxValue) {
		this.maxValue = maxValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getSpec() 
	{
		return spec;
	}

	public void setSpec(Boolean spec) 
	{
		this.spec = spec;
	}
	
	public Boolean getRate() 
	{
		return rate;
	}

	public void setRate(Boolean rate) 
	{
		this.rate = rate;
	}

	
	public Boolean isSpec() {
		return this.spec;		
	}
	
	public Boolean isRate() {
		return this.rate;		
	}

	public String getuValue() {
		return uValue;
	}

	public void setuValue(String uValue) {
		this.uValue = uValue;
	}

}
