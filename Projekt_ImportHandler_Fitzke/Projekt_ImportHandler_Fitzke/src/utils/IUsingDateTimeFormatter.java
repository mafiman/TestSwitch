/**
 * 
 */
package utils;

import java.time.format.DateTimeFormatter;

/**
 * @author martin
 *
 */
public interface IUsingDateTimeFormatter {

	
	/**
	 * @return Our standard formatter, so we are using the same one in the whole programm.
	 */
	static DateTimeFormatter GetFormatter() {
		return  DateTimeFormatter.ofPattern("\"yyyy-MM-dd HH:mm:ss.SSSSSS\"");
	}
	/*
	 * @return Our standard formatter for FilenameParts, so we are using the same one in the whole programm.
	 */
	static DateTimeFormatter GetFilePartFormatter() {
		return  DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss.SSS");
	}

}
