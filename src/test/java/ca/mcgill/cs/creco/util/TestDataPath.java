package ca.mcgill.cs.creco.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;

import org.junit.Test;

public class TestDataPath
{
	@Test
	public void windowsPath() throws Exception
	{
		// Reflectively reset the constant to be able to test 
		// in an environment-independent manner.
		Field pointer = DataPath.class.getDeclaredField("POINTER");
		pointer.setAccessible(true);
		pointer.set(null, "src/test/java/ca/mcgill/cs/creco/util/.localdatapath1");
		String path = DataPath.get();
		assertEquals("C:\\temp\\data\\",DataPath.get());

	}
}
