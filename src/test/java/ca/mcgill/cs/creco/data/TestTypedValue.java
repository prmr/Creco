package ca.mcgill.cs.creco.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;

/**
 * @author prmr
 */
public class TestTypedValue 
{
	@Test
	public void testNull()
	{
		TypedValue value = new TypedValue(null);
		assertTrue( value.isNull() );
	}
	
	@Test
	public void testInteger()
	{
		TypedValue value = new TypedValue(new Integer(28));
		assertTrue(value.isNumeric());
		assertEquals( 28.0, value.getNumericValue(), 0);
		
		value = new TypedValue(new Integer(-28));
		assertTrue(value.isNumeric());
		assertEquals( -28.0, value.getNumericValue(), 0);
	}
	
	@Test
	public void testDouble()
	{
		TypedValue value = new TypedValue(new Double(28));
		assertTrue(value.isNumeric());
		assertEquals( 28.0, value.getNumericValue(), 0);
		
		value = new TypedValue(new Double(-28));
		assertTrue(value.isNumeric());
		assertEquals( -28.0, value.getNumericValue(), 0);
		
		value = new TypedValue(new Double(-28.244));
		assertTrue(value.isNumeric());
		assertEquals( -28.244, value.getNumericValue(), 0);
		
		value = new TypedValue(new Float(-28.244));
		assertTrue(value.isNumeric());
		assertEquals( -28.244, value.getNumericValue(), 0.01);
	}
	
	@Test
	public void testBoolean()
	{
		TypedValue value = new TypedValue(new Boolean(true));
		assertTrue(value.isBoolean());
		assertEquals(true, value.getBooleanValue());
				
		value = new TypedValue(new Boolean(false));
		assertTrue(value.isBoolean());
		assertEquals(false, value.getBooleanValue());
	}
	
	@Test
	public void testStringInteger()
	{
		TypedValue value = new TypedValue("28");
		assertTrue(value.isNumeric());
		assertEquals( 28.0, value.getNumericValue(), 0);
		
		value = new TypedValue("-28");
		assertTrue(value.isNumeric());
		assertEquals( -28.0, value.getNumericValue(), 0);
	}
	
	@Test
	public void testStringDouble()
	{
		TypedValue value = new TypedValue("28.123");
		assertTrue(value.isNumeric());
		assertEquals( 28.123, value.getNumericValue(), 0);
		
		value = new TypedValue("-28.123");
		assertTrue(value.isNumeric());
		assertEquals( -28.123, value.getNumericValue(), 0);
	}
	
	@Test
	public void testStringYesNo()
	{
		TypedValue value = new TypedValue("Yes");
		assertTrue(value.isBoolean());
		assertEquals(true, value.getBooleanValue());
		
		value = new TypedValue("yes");
		assertTrue(value.isBoolean());
		assertEquals(true, value.getBooleanValue());
		
		value = new TypedValue("No");
		assertTrue(value.isBoolean());
		assertEquals(false, value.getBooleanValue());
		
		value = new TypedValue("no");
		assertTrue(value.isBoolean());
		assertEquals(false, value.getBooleanValue());
	}
	
	@Test
	public void testStringPlain()
	{
		TypedValue value = new TypedValue("Fuzzy Wuzzy was a woman?");
		assertTrue( value.isString() );
		assertEquals("Fuzzy Wuzzy was a woman?", value.getNominalValue());
	}
	
	@Test(expected=TypedValueException.class)
	public void testUnknown()
	{
		IOException testObject = new IOException();
		new TypedValue(testObject);
	}
	
	@Test
	public void testNA()
	{
		TypedValue value = new TypedValue("NA");
		assertTrue(value.isNA());
		
		value = new TypedValue("N/A");
		assertTrue(value.isNA());
		
		value = new TypedValue("NNA");
		assertFalse(value.isNA());
		
		value = new TypedValue("na");
		assertTrue(value.isNA());
		
		value = new TypedValue("Na");
		assertTrue(value.isNA());
	}
	
	
}
