package ca.mcgill.cs.creco.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import ca.mcgill.cs.creco.data.TypedValue.Type;

/**
 * @author prmr
 */
public class TestTypedValue 
{
	@Test
	public void testNull()
	{
		TypedValue value = new TypedValue(null);
		assertNull(value.getOriginalValue());
		assertNull(value.getValue());
		assertNull(value.getNominalValue());
		assertEquals(Type.NULL, value.getType());
	}
	
	@Test
	public void testInteger()
	{
		TypedValue value = new TypedValue(new Integer(28));
		assertEquals(new Integer(28), value.getOriginalValue());
		assertEquals(new Integer(28), value.getValue());
		assertEquals(Type.INTEGER, value.getType());
		
		value = new TypedValue(new Integer(-28));
		assertEquals(new Integer(-28), value.getOriginalValue());
		assertEquals(new Integer(-28), value.getValue());
		assertEquals(Type.INTEGER, value.getType());
	}
	
	@Test
	public void testDouble()
	{
		TypedValue value = new TypedValue(new Double(28));
		assertEquals(new Double(28), value.getOriginalValue());
		assertEquals(new Double(28), value.getValue());
		assertEquals(Type.DOUBLE, value.getType());
		
		value = new TypedValue(new Double(-28));
		assertEquals(new Double(-28), value.getOriginalValue());
		assertEquals(new Double(-28), value.getValue());
		assertEquals(Type.DOUBLE, value.getType());
		
		value = new TypedValue(new Double(-28.244));
		assertEquals(new Double(-28.244), value.getOriginalValue());
		assertEquals(new Double(-28.244), value.getValue());
		assertEquals(Type.DOUBLE, value.getType());
		
		value = new TypedValue(new Float(-28.244));
		assertEquals(new Float(-28.244), value.getOriginalValue());
		assertEquals(new Float(-28.244), value.getValue());
		assertEquals(Type.DOUBLE, value.getType());
	}
	
	@Test
	public void testBoolean()
	{
		TypedValue value = new TypedValue(new Boolean(true));
		assertEquals(new Boolean(true), value.getOriginalValue());
		assertEquals(new Boolean(true), value.getValue());
		assertEquals(Type.BOOLEAN, value.getType());
		
		value = new TypedValue(new Boolean(false));
		assertEquals(new Boolean(false), value.getOriginalValue());
		assertEquals(new Boolean(false), value.getValue());
		assertEquals(Type.BOOLEAN, value.getType());
	}
	
	@Test
	public void testStringInteger()
	{
		TypedValue value = new TypedValue("28");
		assertEquals("28", value.getOriginalValue());
		assertTrue(28 ==  value.getNumericValue());
		assertEquals(Type.INTEGER, value.getType());
		
		value = new TypedValue("-28");
		assertEquals("-28", value.getOriginalValue());
		assertTrue(-28 == value.getNumericValue());
		assertEquals(Type.INTEGER, value.getType());
	}
	
	@Test
	public void testStringDouble()
	{
		TypedValue value = new TypedValue("28.123");
		assertEquals("28.123", value.getOriginalValue());
		assertEquals(new Double(28.123), value.getValue());
		assertEquals(Type.DOUBLE, value.getType());
		
		value = new TypedValue("-28.123");
		assertEquals("-28.123", value.getOriginalValue());
		assertEquals(new Double(-28.123), value.getValue());
		assertEquals(Type.DOUBLE, value.getType());
	}
	
	@Test
	public void testStringYesNo()
	{
		TypedValue value = new TypedValue("Yes");
		assertEquals("Yes", value.getOriginalValue());
		assertEquals(new Boolean(true), value.getValue());
		assertEquals(Type.BOOLEAN, value.getType());
		
		value = new TypedValue("yes");
		assertEquals("yes", value.getOriginalValue());
		assertEquals(new Boolean(true), value.getValue());
		assertEquals(Type.BOOLEAN, value.getType());
		
		value = new TypedValue("No");
		assertEquals("No", value.getOriginalValue());
		assertEquals(new Boolean(false), value.getValue());
		assertEquals(Type.BOOLEAN, value.getType());
		
		value = new TypedValue("no");
		assertEquals("no", value.getOriginalValue());
		assertEquals(new Boolean(false), value.getValue());
		assertEquals(Type.BOOLEAN, value.getType());
	}
	
	@Test
	public void testStringPlain()
	{
		TypedValue value = new TypedValue("Fuzzy Wuzzy was a woman?");
		assertEquals("Fuzzy Wuzzy was a woman?", value.getOriginalValue());
		assertEquals("Fuzzy Wuzzy was a woman?", value.getNominalValue());
		assertEquals(Type.STRING, value.getType());
	}
	
	@Test
	public void testUnknown()
	{
		IOException testObject = new IOException();
		TypedValue value = new TypedValue(testObject);
		assertEquals(testObject, value.getOriginalValue());
		assertEquals(testObject, value.getValue());
		assertEquals(Type.UNKNOWN, value.getType());
	}
	
	
}
