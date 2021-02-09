package rolling.stats;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class RollingStatistics {

	private Integer maxItems;
	/*
	 * chose Double for the data as it can also support integer values and helps for
	 * better accuracy of calculations, BigDecimal can also be used for the best
	 * precision
	 */
	private Queue<Double> items;
	private Double sum = 0.0;
	private Integer numberOfItems;
	DecimalFormat df = new DecimalFormat("##0.00");

	public RollingStatistics(Integer maxItems) throws IllegalArgumentException {
		if(maxItems <= 0) {
			throw new IllegalArgumentException("Number of maximum items must be positive!");
		}
		this.maxItems = maxItems;
		items = new ArrayBlockingQueue<Double>(maxItems);
		numberOfItems = 0;
	}

	// helper method to check if the queue is full
	private boolean isQueueFull() {
		return numberOfItems == maxItems;
	}

	// adding a number to the queue
	public void add(Double stat) {
		if (isQueueFull()) {
			sum -= items.peek();
			items.remove();

			numberOfItems--;
		}
		sum += stat;
		numberOfItems++;
		items.add(stat);
	}

	public Double getMedian() {
		/*
		 * cast the queue to an array that can be sorted and the median can be retrieved
		 */
		ArrayList<Double> sortedItems = new ArrayList<Double>(this.items);
		Collections.sort(sortedItems);
		Double median = null;
		if (sortedItems.size() % 2 == 1) {

			/*
			 * if the ArrayList has an odd size get size/2 element where size/2 will be
			 * floored as we are dividing integers
			 */
			median = sortedItems.get(sortedItems.size() / 2);

		} else {
			/*
			 * if the ArrayList has an even size get the sum of the two middle elements and
			 * divide that sum by two to get the median
			 */
			median = (sortedItems.get(sortedItems.size() / 2 - 1) + sortedItems.get(sortedItems.size() / 2)) / 2;
		}
		return median;
	}

	public Double getMax() {
		return Collections.max(this.items);
	}

	public Double getMin() {
		return Collections.min(this.items);
	}

	public Double getSum() {
		return this.sum;
	}

	public Double getMean() {
		return sum / numberOfItems;
	}

	public Double getVariance() {
		Double variance = 0.0;
		for (Double i : this.items) {
			variance += Math.pow(i - this.getMean(), 2);
		}
		return variance / items.size();
	}

	public Double getStandardDeviation() {
		return Math.sqrt(this.getVariance());
	}
	
	

	/**
	 * Get the statistics (sum, mean, median, variance, standard deviation) for a
	 * specific range
	 * 
	 * @param startingPosition - the start of the range (counting from 1, inclusive)
	 * @param endingPosition   - the end of the range (inclusive)
	 * @return the statistics for the range in a formatted string
	 * @throws IllegalArgumentException
	 */
	public String queryRange(Integer startingPosition, Integer endingPosition) throws IllegalArgumentException {
		StringBuilder statstics = new StringBuilder();
		if(startingPosition == 0 &&  endingPosition == 0) {
			throw new IllegalStateException("Data storage is empty!");
		}
		if (startingPosition < 0 || endingPosition < 0) {
			throw new IllegalArgumentException("Start and end point cannot be negative");
		}
		if (startingPosition > endingPosition) {
			throw new IllegalArgumentException("Start point cannot be greater than end point!");
		}
		if (endingPosition > numberOfItems) {
			throw new IllegalArgumentException("Ending position is greater than number of items!");
		}
		ArrayList<Double> items = new ArrayList<Double>(this.items);
		RollingStatistics rangeItems = new RollingStatistics(endingPosition - startingPosition + 1);
		for (int i = startingPosition - 1; i < endingPosition; i++) {
			rangeItems.add(items.get(i));
		}
		statstics.append("Statistics for the range ").append(startingPosition).append(" - ").append(endingPosition)
				.append("\n");
		statstics.append("Items => ").append(rangeItems.toString()).append("\n");
		statstics.append("Count => ").append(rangeItems.getNumberOfItems()).append("\n");
		statstics.append("Min => ").append(rangeItems.minToString()).append("\n");
		statstics.append("Max => ").append(rangeItems.maxToString()).append("\n");
		statstics.append("Sum => ").append(rangeItems.sumToString()).append("\n");
		statstics.append("Mean => ").append(rangeItems.meanToString()).append("\n");
		statstics.append("Median => ").append(rangeItems.medianToString()).append("\n");
		statstics.append("Variance => ").append(rangeItems.varianceToString()).append("\n");
		statstics.append("Standard Deviation => ").append(rangeItems.SDtoString()).append("\n");
		return statstics.toString();
	}
	
	public String queryAll() {
		return this.queryRange(1, this.numberOfItems);
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

	/*
	 * overriding the toString() method to return the collection of stats
	 */
	@Override
	public String toString() {
		return this.items.toString();
	}

	public Integer getNumberOfItems() {
		return this.numberOfItems;
	}
}