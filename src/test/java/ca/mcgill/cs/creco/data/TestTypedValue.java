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
	public void testRange()
	{
		TypedValue value = new TypedValue("Limited to 4-22Lbs");
		assertTrue( value.isNumeric());
		assertEquals( 13.0, value.getNumeric(), 0);
		
		value = new TypedValue("Limited to 4-22\"");
		assertTrue( value.isNumeric());
		assertEquals( 13.0, value.getNumeric(), 0);
		
		value = new TypedValue("Limited to 4-22");
		assertTrue( value.isNumeric());
		assertEquals( 13.0, value.getNumeric(), 0);
		
		value= new TypedValue("5-35Lbs");
		assertTrue( value.isNumeric());
		assertEquals(20.0,value.getNumeric(),0);
		
		value= new TypedValue("5-35\"");
		assertTrue( value.isNumeric());
		assertEquals(20.0,value.getNumeric(),0);
		
		value= new TypedValue("5-35");
		assertTrue( value.isNumeric());
		assertEquals(20.0,value.getNumeric(),0);
	}
	@Test
	public void testNull()
	{
		TypedValue value = new TypedValue(null);
		assertTrue( value.isNull() );
		assertFalse( new TypedValue(28).isNull() );
	}
	
	@Test(expected=TypedValueException.class)
	public void testInteger()
	{
		TypedValue value = new TypedValue(new Integer(28));
		assertTrue(value.isNumeric());
		assertEquals( 28.0, value.getNumeric(), 0);
		assertFalse(value.isNull());
		assertFalse(value.isBoolean());
		assertFalse(value.isNA());
		assertFalse(value.isString());
		
		value = new TypedValue(new Integer(-28));
		assertTrue(value.isNumeric());
		assertEquals( -28.0, value.getNumeric(), 0);
		value.getBoolean();
	}
	
	@Test(expected=TypedValueException.class)
	public void testDouble()
	{
		TypedValue value = new TypedValue(new Double(28));
		assertTrue(value.isNumeric());
		assertFalse(value.isNull());
		assertFalse(value.isBoolean());
		assertFalse(value.isNA());
		assertFalse(value.isString());
		assertEquals( 28.0, value.getNumeric(), 0);
		
		value = new TypedValue(new Double(-28));
		assertTrue(value.isNumeric());
		assertEquals( -28.0, value.getNumeric(), 0);
		
		value = new TypedValue(new Double(-28.244));
		assertTrue(value.isNumeric());
		assertEquals( -28.244, value.getNumeric(), 0);
		
		value = new TypedValue(new Float(-28.244));
		assertTrue(value.isNumeric());
		assertEquals( -28.244, value.getNumeric(), 0.01);
		
		value.getString();
	}
	
	@Test(expected=TypedValueException.class)
	public void testBoolean()
	{
		TypedValue value = new TypedValue(new Boolean(true));
		assertTrue(value.isBoolean());
		assertEquals(true, value.getBoolean());
				
		value = new TypedValue(new Boolean(false));
		assertTrue(value.isBoolean());
		assertEquals(false, value.getBoolean());
		
		value.getNumeric();
	}
	
	@Test(expected=TypedValueException.class)
	public void testStringInteger()
	{
		TypedValue value = new TypedValue("28");
		assertTrue(value.isNumeric());
		assertEquals( 28.0, value.getNumeric(), 0);
		
		value = new TypedValue("-28");
		assertTrue(value.isNumeric());
		assertEquals( -28.0, value.getNumeric(), 0);
		
		value.getString();
	}
	
	@Test(expected=TypedValueException.class)
	public void testStringDouble()
	{
		TypedValue value = new TypedValue("28.123");
		assertTrue(value.isNumeric());
		assertEquals( 28.123, value.getNumeric(), 0);
		
		value = new TypedValue("-28.123");
		assertTrue(value.isNumeric());
		assertEquals( -28.123, value.getNumeric(), 0);
		
		value = new TypedValue("90 hours");
		assertTrue(value.isNumeric());
		assertEquals( 90, value.getNumeric(), 0);
		
		value = new TypedValue("50 days");
		assertTrue(value.isNumeric());
		assertEquals( 50, value.getNumeric(), 0);
		
		value = new TypedValue("56 \"");
		assertTrue(value.isNumeric());
		assertEquals( 56, value.getNumeric(), 0);
		
		value.getString();
	}
	
	@Test(expected=TypedValueException.class)
	public void testStringYesNo()
	{
		TypedValue value = new TypedValue("Yes");
		assertTrue(value.isBoolean());
		assertEquals(true, value.getBoolean());
		
		value = new TypedValue("yes");
		assertTrue(value.isBoolean());
		assertEquals(true, value.getBoolean());
		
		value = new TypedValue("No");
		assertTrue(value.isBoolean());
		assertEquals(false, value.getBoolean());
		
		value = new TypedValue("no");
		assertTrue(value.isBoolean());
		assertEquals(false, value.getBoolean());
		
		value.getString();
	}
	
	@Test(expected=TypedValueException.class)
	public void testStringPlain()
	{
		TypedValue value = new TypedValue("Fuzzy Wuzzy was a woman?");
		assertTrue( value.isString() );
		assertFalse(value.isNull());
		assertFalse(value.isNumeric());
		assertFalse(value.isBoolean());
		assertFalse(value.isNA());
		assertEquals("Fuzzy Wuzzy was a woman?", value.getString());
		
		value.getBoolean();
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
		assertFalse(value.isNull());
		assertFalse(value.isNumeric());
		assertFalse(value.isBoolean());
		assertFalse(value.isString());
		
		value = new TypedValue("N/A");
		assertTrue(value.isNA());
		
		value = new TypedValue("NNA");
		assertFalse(value.isNA());
		
		value = new TypedValue("na");
		assertTrue(value.isNA());
		
		value = new TypedValue("Na");
		assertTrue(value.isNA());
	}
	
	@Test public void testRawBoolean()
	{
		TypedValue value = new TypedValue(true);
		assertTrue(value.isBoolean());
		assertFalse(value.isNull());
		assertFalse(value.isNumeric());
		assertFalse(value.isNA());
		assertFalse(value.isString());
		assertTrue(value.getBoolean());
		
		value = new TypedValue(false);
		assertTrue(value.isBoolean());
		assertFalse(value.getBoolean());
	}
	
	@Test public void testRawInteger()
	{
		TypedValue value = new TypedValue(28);
		assertTrue(value.isNumeric());
		assertFalse(value.isNull());
		assertFalse(value.isBoolean());
		assertFalse(value.isNA());
		assertFalse(value.isString());
		assertEquals(28.0,value.getNumeric(),0);
		
		value = new TypedValue(-28);
		assertTrue(value.isNumeric());
		assertEquals(-28.0,value.getNumeric(),0);
	}
	
	@Test public void testToString()
	{
		TypedValue value = new TypedValue(28);
		assertEquals("NUMERIC: 28.0" ,value.toString());
		
		value = new TypedValue();
		assertEquals("NA" ,value.toString());
		
		value = new TypedValue(null);
		assertEquals("NULL" ,value.toString());
		
		value = new TypedValue(false);
		assertEquals("BOOLEAN: false" ,value.toString());
		
		value = new TypedValue("Foo");
		assertEquals("STRING: Foo" ,value.toString());
	}
	
	@Test public void testEqualsNull()
	{
		TypedValue value1 = new TypedValue(null);
		TypedValue value2 = new TypedValue(null);
		TypedValue value3 = new TypedValue(28);
		assertFalse(value1.equals(null));
		assertTrue(value1.equals(value1));
		assertFalse(value1.equals(new Integer(28)));
		assertTrue(value1.equals(value2));
		assertTrue(value2.equals(value1));
		assertFalse(value1.equals(value3));
		assertFalse(value3.equals(value1));
		assertEquals(value1.hashCode(), value2.hashCode());
	}
	
	@Test public void testEqualsNA()
	{
		TypedValue value1 = new TypedValue("NA");
		TypedValue value2 = new TypedValue("NA");
		TypedValue value3 = new TypedValue(28);
		assertTrue(value1.equals(value1));
		assertFalse(value1.equals(new Integer(28)));
		assertTrue(value1.equals(value2));
		assertTrue(value2.equals(value1));
		assertFalse(value1.equals(value3));
		assertFalse(value3.equals(value1));
		assertEquals(value1.hashCode(), value2.hashCode());
	}
	
	@Test public void testEqualsBoolean()
	{
		TypedValue value1 = new TypedValue("Yes");
		TypedValue value2 = new TypedValue(true);
		TypedValue value3 = new TypedValue("no");
		TypedValue value4 = new TypedValue(28);
		assertTrue(value1.equals(value1));
		assertTrue(value2.equals(value1));
		assertFalse(value1.equals(value3));
		assertFalse(value3.equals(value1));
		assertFalse(value1.equals(value4));
		assertFalse(value4.equals(value1));
		
		assertEquals(value1.hashCode(), value2.hashCode());
	}
	
	@Test public void testEqualsNumeric()
	{
		TypedValue value1 = new TypedValue(28);
		TypedValue value2 = new TypedValue(28.0);
		TypedValue value3 = new TypedValue("no");
		TypedValue value4 = new TypedValue(true);
		TypedValue value5 = new TypedValue(-0.0765);
		TypedValue value6 = new TypedValue(-0.07650000001);
		TypedValue value7 = new TypedValue(-0.076501);
		assertTrue(value1.equals(value1));
		assertTrue(value1.equals(value2));
		assertTrue(value2.equals(value1));
		assertFalse(value1.equals(value3));
		assertFalse(value3.equals(value1));
		assertFalse(value2.equals(value4));
		assertFalse(value4.equals(value2));
		assertTrue(value5.equals(value6));
		assertTrue(value6.equals(value5));
		assertFalse(value5.equals(value7));
		assertFalse(value7.equals(value5));
	
		assertEquals(value1.hashCode(), value2.hashCode());
		assertEquals(value5.hashCode(), value6.hashCode());
	}
	
	@Test public void testEqualsString()
	{
		TypedValue value1 = new TypedValue("Foo");
		TypedValue value2 = new TypedValue("Foo");
		TypedValue value3 = new TypedValue("Bar");
		TypedValue value4 = new TypedValue(true);
		assertTrue(value1.equals(value1));
		assertTrue(value1.equals(value2));
		assertTrue(value2.equals(value1));
		assertFalse(value1.equals(value3));
		assertFalse(value3.equals(value1));
		assertFalse(value1.equals(value4));
		assertFalse(value4.equals(value1));
	
		assertEquals(value1.hashCode(), value2.hashCode());
	}
	
	
}