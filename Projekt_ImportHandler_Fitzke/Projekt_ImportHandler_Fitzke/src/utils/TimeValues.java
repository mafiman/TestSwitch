/**
 * 
 */
package utils;

import java.time.LocalDateTime;


/**
 * Helperclass, holding min and max values for time.
 * 
 * @author martin
 *
 */
public class TimeValues {
	private LocalDateTime minTime;
	private LocalDateTime maxTime;

	/**
	 * @return the minTime
	 */
	public LocalDateTime getMinTime() {
		return this.minTime;
	}

	/**
	 * @return the minTime in formatted String
	 */
	public String getMinTimeFormattedString() {
		return this.minTime.format(IUsingDateTimeFormatter.GetFormatter());
	}

	/**
	 * @param minTime the minTime to set
	 */
	public void setMinTime(LocalDateTime minTime) {
		this.minTime = minTime;
	}

	/**
	 * @return the maxTime
	 */
	public LocalDateTime getMaxTime() {
		return this.maxTime;
	}

	/**
	 * @return the maxTime in formatted String
	 */
	public String getMaxTimeFormattedString() {
		return this.maxTime.format(IUsingDateTimeFormatter.GetFormatter());
	}

	/**
	 * @param maxTime the maxTime to set
	 */
	public void setMaxTime(LocalDateTime maxTime) {
		this.maxTime = maxTime;
	}

	/**
	 * Constructor, sets the min and maxvalues to this 
	 * min and max Timestamps.
	 */
	public TimeValues() {
		this.minTime = LocalDateTime.MAX;
		this.maxTime = LocalDateTime.MIN;

	}

}