package rolling.stats;

import java.text.DecimalFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class RollingStatisticsTimestamped {
	private final DecimalFormat df = new DecimalFormat("##0.00");
	private Duration statLife;
	private LinkedList<DataItem> items;
	private Double sum = 0.0;
	private Integer numberOfItems;

	public RollingStatisticsTimestamped(Duration maxStatLife) {
		items = new LinkedList<DataItem>();
		statLife = maxStatLife;
		numberOfItems = 0;
	}

	/*
	 * check the list for items that are too old to be in it and remove them
	 */
	public void checkList() {
		System.out.println();
		System.out.println("Checking list...");
		// using iterator to avoid Concurrent Modification Exception.
		for (Iterator<DataItem> iterator = this.items.iterator(); iterator.hasNext();) {
			DataItem item = iterator.next();
			if (Duration.between(item.getStart(), LocalDateTime.now()).compareTo(statLife) > 0) {
				sum -= item.getValue();
				System.out.println(item.toString() + " was removed from the list!");
				iterator.remove();
				numberOfItems--;
			}
		}
		System.out.println("Done checking list!");
		System.out.println("Remaining items: " + this.toString() + "\n");
	}

	/*
	 * adding a DataItem
	 */
	public void add(DataItem stat) {
		sum += stat.getValue();
		numberOfItems++;
		items.add(stat);
	}

	public Double getMedian() {
		/*
		 * cast the queue to an array that can be sorted by value and the median can be
		 * retrieved
		 */
		ArrayList<DataItem> sortedItems = new ArrayList<DataItem>(this.items);
		Collections.sort(sortedItems, Comparator.comparing(DataItem::getValue));
		Double median;
		if (sortedItems.size() % 2 == 1) {

			/*
			 * if the ArrayList has an odd size get size/2 element where size/2 will be
			 * floored as we are dividing integers
			 */
			median = sortedItems.get(sortedItems.size() / 2).getValue();

		} else {
			/*
			 * if the ArrayList has an even size get the sum of the two middle elements and
			 * divide that sum by two to get the median
			 */
			median = (sortedItems.get(sortedItems.size() / 2 - 1).getValue()
					+ sortedItems.get(sortedItems.size() / 2).getValue()) / 2;
		}
		return median;
	}

	/**
	 * @return the maximum value of the items in the list
	 */
	public Double getMax() {
		return Collections.max(this.items, Comparator.comparing(DataItem::getValue)).getValue();
	}

	/**
	 * @return the minimum value of the items in the list
	 */
	public Double getMin() {
		return Collections.min(this.items, Comparator.comparing(DataItem::getValue)).getValue();
	}

	/**
	 * @return the mean value of the items in the list
	 */
	public Double getMean() {
		return this.sum / this.numberOfItems;
	}

	/**
	 * @return the variance of the items in the list
	 */
	public Double getVariance() {
		Double variance = 0.0;
		for (DataItem i : this.items) {
			variance += Math.pow(i.getValue() - this.getMean(), 2);
		}
		return variance / items.size();
	}

	/**
	 * @return the standard deviation of the items in the list
	 */
	public Double getStandardDeviation() {
		return Math.sqrt(this.getVariance());
	}

	/**
	 * @return the oldest of the items in the list
	 */
	public DataItem getOldest() {
		return Collections.min(this.items, Comparator.comparing(DataItem::getStart));
	}

	/**
	 * @return the newest of the items in the list
	 */
	public DataItem getNewest() {
		return Collections.max(this.items, Comparator.comparing(DataItem::getStart));
	}

	/**
	 * @return value and timestamp of the newest item in the list
	 */
	public String newestDataItem() {
		return this.getNewest().toString();
	}

	/**
	 * @return value and timestamp of the oldest item in the list
	 */
	public String oldestDataItem() {
		return this.getOldest().toString();
	}

	/*
	 * overriding the toString() method to return the collection of data
	 */
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("[");
		for (DataItem item : items) {
			if (item != items.getLast()) {
				output.append(item.valueToString()).append(", ");
			} else {
				output.append(item.valueToString());
			}
		}
		output.append("]");
		return output.toString();
	}

	/**
	 * Get the all the statistics for the items added in the last given duration
	 * specific range
	 * 
	 * @param duration - the duration for which we are querying (items older than
	 *                 this will not be considered)
	 * @return the statistics for the range in a formatted string
	 * @throws IllegalArgumentException
	 */
	
	public String queryAll() throws IllegalArgumentException {
		return this.queryRange(statLife);
	}
	
	public String queryRange(Duration duration) throws IllegalArgumentException {
		if(duration.isNegative() || duration.isZero()) {
			throw new IllegalArgumentException("The duration you are querying for cannot be negative or zero");
		}
		this.checkList(); //check the list before query
		StringBuilder statstics = new StringBuilder();
		// duration here really does not matter, as we do not check this list
		RollingStatisticsTimestamped rangeItems = new RollingStatisticsTimestamped(Duration.ofSeconds(1));
		for (DataItem item : this.items) {
			if (Duration.between(item.getStart(), LocalDateTime.now()).compareTo(duration) < 0) {
				rangeItems.add(item);
			}
		}
		// time is in seconds for easier testing but can also be configured to minutes,
		// hours, etc.
		statstics.append("Statistics for the last ").append(duration.toSeconds()).append(" seconds\n");
		statstics.append("Items => ").append(rangeItems.toString()).append("\n");
		statstics.append("Count => ").append(rangeItems.getNumberOfItems()).append("\n");
		statstics.append("Min => ").append(rangeItems.minToString()).append("\n");
		statstics.append("Max => ").append(rangeItems.maxToString()).append("\n");
		statstics.append("Newest => ").append(rangeItems.newestDataItem()).append("\n");
		statstics.append("Oldest => ").append(rangeItems.oldestDataItem()).append("\n");
		statstics.append("Sum => ").append(rangeItems.sumToString()).append("\n");
		statstics.append("Mean => ").append(rangeItems.meanToString()).append("\n");
		statstics.append("Median => ").append(rangeItems.medianToString()).append("\n");
		statstics.append("Variance => ").append(rangeItems.varianceToString()).append("\n");
		statstics.append("Standard Deviation => ").append(rangeItems.SDtoString()).append("\n");
		return statstics.toString();
	}

	/*
	 * Methods to return the statistics values rounded to the nearest hundredth as a
	 * String
	 */
	public String medianToString() {
		return df.format(this.getMedian());
	}

	public String SDtoString() {
		return df.format(this.getStandardDeviation());
	}

	public String meanToString() {
		return df.format(this.getMean());
	}

	public String varianceToString() {
		return df.format(this.getVariance());
	}

	public String sumToString() {
		return df.format(this.getSum());
	}

	public String minToString() {
		return df.format(this.getMin());
	}

	public String maxToString() {
		return df.format(this.getMax());
	}

	public Double getSum() {
		return sum;
	}

	public Integer getNumberOfItems() {
		return numberOfItems;
	}
}
