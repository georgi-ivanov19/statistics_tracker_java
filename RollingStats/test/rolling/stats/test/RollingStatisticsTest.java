package rolling.stats.test;

import static org.junit.Assert.*;

import rolling.stats.*;
import org.junit.Before;
import org.junit.Test;

public class RollingStatisticsTest {

	/*
	 * used https://www.calculator.net/statistics-calculator for the calculations
	 */
	RollingStatistics rs = new RollingStatistics(10);

	@Before
	public void setUp() throws Exception {
		rs.add(13.346);
		rs.add(15.4);
		rs.add(25.00);
		rs.add(3.14);
		rs.add(22.98);
		rs.add(14.11);
		rs.add(19.89);
		rs.add(13.22);
		rs.add(9.79);
		rs.add(27.45);
	}

	@Test
	public void testAdd() {
		rs.add(10.10);
		assertEquals(Integer.valueOf(10), rs.getNumberOfItems());
	}
	
	@Test
	public void testQuery() {
		assertEquals("Statistics for the range 1 - 10\n"
				+ "Items => [13.346, 15.4, 25.0, 3.14, 22.98, 14.11, 19.89, 13.22, 9.79, 27.45]\n"
				+ "Count => 10\n"
				+ "Min => 3.14\n"
				+ "Max => 27.45\n"
				+ "Sum => 164.33\n"
				+ "Mean => 16.43\n"
				+ "Median => 14.75\n"
				+ "Variance => 49.67\n"
				+ "Standard Deviation => 7.05\n",rs.queryAll());
	}
	
	@Test
	public void testCount() {
		assertEquals(Integer.valueOf(10), rs.getNumberOfItems());
	}

	@Test
	public void testSum() {
		assertEquals(Double.valueOf(164.326), rs.getSum());
		assertEquals("164.33", rs.sumToString());
	}

	@Test
	public void testMin() {
		assertEquals(Double.valueOf(3.14), rs.getMin());
		assertEquals("3.14", rs.minToString());
	}

	@Test
	public void testMax() {
		assertEquals(Double.valueOf(27.45), rs.getMax());
		assertEquals("27.45", rs.maxToString());
	}

	@Test
	public void testMean() {
		assertEquals(Double.valueOf(16.4326), rs.getMean());
		assertEquals("16.43", rs.meanToString());
	}

	@Test
	public void testMedian() {
		assertEquals(Double.valueOf(14.754999999999999), rs.getMedian());
		assertEquals("14.75", rs.medianToString());
	}

	@Test
	public void testVariance() {
		assertEquals(Double.valueOf(49.673148839999996), rs.getVariance());
		assertEquals("49.67", rs.varianceToString());
	}

	@Test
	public void testStandartDeviation() {
		assertEquals(Double.valueOf(7.04791805003435), rs.getStandardDeviation());
		assertEquals("7.05", rs.SDtoString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMaxItems() {
		RollingStatistics illegalRS = new RollingStatistics(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMaxItems2() {
		RollingStatistics illegalRS = new RollingStatistics(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeEndLessThanStart() {
		rs.queryRange(10, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeNegative() {
		rs.queryRange(-10, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeEndPointTooBig() {
		rs.queryRange(1, 11);
	}

}
