package rolling.stats.test;

import static org.junit.Assert.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import rolling.stats.DataItem;
import rolling.stats.RollingStatisticsTimestamped;

public class RollingStatisticsTimestampedTest {

	/*
	 * decided not to test calculation methods as they are identical to the
	 * RollingStatistics class
	 */

	// long enough to not remove the item created 2020 when checking
	RollingStatisticsTimestamped rst = new RollingStatisticsTimestamped(Duration.ofDays(700));

	// short enough to make sure that the full list is cleared before assertion
	RollingStatisticsTimestamped rst0 = new RollingStatisticsTimestamped(Duration.ofNanos(0));

	@Before
	public void setUp() throws Exception {
		rst0.add(new DataItem(13.346));
		rst0.add(new DataItem(15.4));
		rst0.add(new DataItem(25.00));
		rst0.add(new DataItem(3.14));
		rst0.add(new DataItem(22.98));
		rst0.add(new DataItem(14.11));
		rst0.add(new DataItem(19.89));
		rst0.add(new DataItem(13.22));
		rst0.add(new DataItem(9.79));
		rst0.add(new DataItem(27.45));

		rst.add(new DataItem(13.346));
		rst.add(new DataItem(15.4));
		rst.add(new DataItem(25.00));
		rst.add(new DataItem(3.14, LocalDateTime.of(2020, 12, 6, 6, 6, 6)));
		rst.add(new DataItem(22.98, LocalDateTime.of(2022, 1, 6, 6, 6, 6)));
		rst.add(new DataItem(14.11));
		rst.add(new DataItem(19.89));
		rst.add(new DataItem(13.22));
		rst.add(new DataItem(9.79));
		rst.add(new DataItem(27.45));

		rst0.checkList();
		rst.checkList();
	}

	@Test
	public void testAdd() {
		rst.add(new DataItem(10.10));
		assertEquals(Integer.valueOf(11), rst.getNumberOfItems());
	}

	@Test
	public void testCheckList() {
		rst0.checkList();
		rst.checkList();
		assertEquals(Integer.valueOf(10), rst.getNumberOfItems());
		assertEquals(Integer.valueOf(0), rst0.getNumberOfItems());

	}

	@Test
	public void testQuery() {
		assertEquals("Statistics for the last 60480000 seconds\n"
				+ "Items => [13.35, 15.40, 25.00, 3.14, 22.98, 14.11, 19.89, 13.22, 9.79, 27.45]\n" + "Count => 10\n"
				+ "Min => 3.14\n" + "Max => 27.45\n" + "Newest => Item: 22.98 created: Jan 6, 2022, 6:06:06 AM\n"
				+ "Oldest => Item: 3.14 created: Dec 6, 2020, 6:06:06 AM\n" + "Sum => 164.33\n" + "Mean => 16.43\n"
				+ "Median => 14.75\n" + "Variance => 49.67\n" + "Standard Deviation => 7.05\n", rst.queryAll());
	}

	@Test
	public void testGetOldest() {
		assertEquals("Item: 3.14 created: Dec 6, 2020, 6:06:06 AM", rst.oldestDataItem());
	}

	@Test
	public void testGetNewest() {
		assertEquals("Item: 22.98 created: Jan 6, 2022, 6:06:06 AM", rst.newestDataItem());
	}

	@Test
	public void testToString() {
		rst0.checkList();
		assertEquals("[13.35, 15.40, 25.00, 3.14, 22.98, 14.11, 19.89, 13.22, 9.79, 27.45]", rst.toString());
		assertEquals("[]", rst0.toString());
	}

	// should not be able to query an empty list
	@Test(expected = NoSuchElementException.class)
	public void testQueryFail() {
		RollingStatisticsTimestamped rstEmpty = new RollingStatisticsTimestamped(Duration.ofSeconds(10));
		rstEmpty.queryAll();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testQueryAllFail() {
		rst0.queryAll(); // cannot query it as duration is 0
	}

	@Test(expected = IllegalArgumentException.class)
	public void testQueryRangeFail() {
		rst.queryRange(Duration.ofSeconds(-2)); // cannot query it as duration is negative
	}
}
