package rolling.stats.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

import rolling.stats.DataItem;

public class DataItemTest {
	DataItem di = new DataItem(3.141);
	DataItem diTime = new DataItem(3.14, LocalDateTime.of(2021, 1, 6, 6, 6, 6));
	
	@Test
	public void testToString() {
		assertEquals("Item: 3.14 created: Jan 6, 2021, 6:06:06 AM", diTime.toString());
	}
	
	@Test
	public void testStartToString() {
		assertEquals("Jan 6, 2021, 6:06:06 AM", diTime.startToString());
	}
	
	@Test
	public void testValueToString() {
		assertEquals("3.14", di.valueToString());
	}
	
	@Test
	public void testGetValue() {
		assertEquals(Double.valueOf(3.141), di.getValue());
	}
	
	@Test
	public void testGetStartDate() {
		assertEquals(LocalDateTime.of(2021, 1, 6, 6, 6, 6), diTime.getStart());
	}
}
