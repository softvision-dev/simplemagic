package com.j256.simplemagic.pattern.formatter.types;

import com.j256.simplemagic.pattern.formatter.AbstractMagicFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Formatter for Long based date values.
 */
public class LongDateFormatter extends AbstractMagicFormatter {

	private final TimeZone timeZone;

	/**
	 * Applies date formatting instructions to a given value to an extracted Long value.
	 *
	 * @param timeZone     The timezone of the date.
	 * @param formatString The formatting instructions of this formatter.
	 */
	public LongDateFormatter(TimeZone timeZone, String formatString) {
		super(formatString);
		this.timeZone = timeZone;
	}

	/**
	 * Formats the given extracted value and returns the formatted string. Shall return an empty String for objects, that
	 * are not an instance of {@link Number}.
	 *
	 * @param value The value, that shall be formatted.
	 * @return The formatted String.
	 */
	@Override
	public String format(Object value) {
		if (value instanceof Number) {
			Date date = new Date(((Number) value).intValue());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
			format.setTimeZone(this.timeZone);

			return super.format(format.format(date));
		}
		return "";
	}
}
