package rolling.stats.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DataItemTest.class, RollingStatisticsTest.class, RollingStatisticsTimestampedTest.class })
public class AllTests {

}
