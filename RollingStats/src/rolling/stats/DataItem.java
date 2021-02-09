package rolling.stats;

import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DataItem {
	private final DecimalFormat df = new DecimalFormat("##0.00");
	/*
	 * chose Double for the data value as it can also support integer values and helps for
	 * better accuracy of calculations, BigDecimal can also be used for the best
	 * precision
	 */
	private Double value;
	private LocalDateTime start;

	/**
	 * Creates a DataItem object with a specified value
	 * start time is equal to the time at the moment of creation
	 * 
	 * @param value
	 */
	public DataItem(Double value) {
		this.value = value;
		this.start = LocalDateTime.now();
	}

	/**
	 * Creates a DataItem object with a specified value and start time
	 * 
	 * @param value
	 * @param start
	 */
	public DataItem(Double value, LocalDateTime start) {
		this.value = value;
		this.start = start;
	}

	public Double getValue() {
		return this.value;
	}

	public String valueToString() {
		return df.format(this.value);
	}

	/**
	 * @return the date that the DataItem was created/added in this format: "Jan 5, 2021, 2:48:40 PM"
	 */
	public String startToString() {
		return start.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
	}

	public LocalDateTime getStart() {
		return this.start;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Item: ").append(this.valueToString()).append(" created: ").append(this.startToString());
		return output.toString();
	}
}
