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
	private static final String PATH_POINTER = ".localdatapath";
	
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
			in = new BufferedReader(new FileReader(PATH_POINTER));
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
