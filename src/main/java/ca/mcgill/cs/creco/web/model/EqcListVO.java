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

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class EqcListVO implements Serializable
{

	private static final long serialVersionUID = -5156221733524698009L;
	private ArrayList<EqcVO> eqcs = new ArrayList<EqcVO>();

	public EqcListVO()
	{
		
	}

	public ArrayList<EqcVO> getEqcs()
	{
		return eqcs;
	}

	public void setEqcs(ArrayList<EqcVO> eqcs)
	{
		this.eqcs = eqcs;
	}

	
}
