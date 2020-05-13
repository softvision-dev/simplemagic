package com.j256.simplemagic.pattern.formatter;

/**
 * An implementing class shall provide the means to format values according to the C %0.2f type formats appropriately.
 */
public interface MagicFormatter {

	/**
	 * Formats the given extracted value and returns the formatted string
	 *
	 * @param value The value, that shall be formatted.
	 * @return The formatted String.
	 */
	String format(Object value);
}
