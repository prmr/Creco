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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author prmr
 * Manages the local data path.
 */
public final class DataPath
{
	private static String POINTER = ".localdatapath";
	
	private DataPath(){}
	
	/**
	 * @return The path where the consumer reports data can be found locally. 
	 * The return value is always terminated by a path separator character.
	 * @throws IOException If the value cannot be obtained.
	 */
	public static String get() throws IOException
	{
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(POINTER));
			String path = in.readLine();
			if( !path.endsWith(File.separator))
			{
				path += File.separator;
			}
			return path;
		}
		finally
		{
			if( in != null )
			{
				in.close();
			}
		}
	}
}
