package com.j256.simplemagic.pattern.components.formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formatter that handles the C %0.2f type formats appropriately.
 *
 * @author graywatson
 */
public abstract class AbstractMagicFormatter implements MagicFormatter {

	public final static String FINAL_PATTERN_CHARS = "%bcdeEfFgGiosuxX";
	public final static String PATTERN_MODIFIERS = "lqh";
	// NOTE: the backspace is taken care of by checking the format string prefix above
	private final static Pattern FORMAT_PATTERN =
			Pattern.compile("([^%]*)(%[-+0-9# ." + PATTERN_MODIFIERS + "]*[" + FINAL_PATTERN_CHARS + "])?(.*)");

	private String prefix;
	private PercentExpression percentExpression;
	private String suffix;

	/**
	 * This takes a format string, breaks it up into prefix, %-thang, and suffix.
	 *
	 * @param formatString The format string, that shall be represented by this.
	 */
	public AbstractMagicFormatter(String formatString) {
		parse(formatString);
	}

	/**
	 * Formats the given extracted value and returns the formatted string
	 *
	 * @param value The value, that shall be formatted.
	 * @return The formatted String.
	 */
	public String format(Object value) {
		StringBuilder sb = new StringBuilder();
		if (prefix != null) {
			sb.append(prefix);
		}
		if (percentExpression != null && value != null) {
			percentExpression.append(value, sb);
		}
		if (suffix != null) {
			sb.append(suffix);
		}
		return sb.toString();
	}

	/**
	 * Returns the String representation of this formatting instructions.
	 *
	 * @return The String representation of this formatting instructions.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (prefix != null) {
			sb.append(prefix);
		}
		if (percentExpression != null) {
			sb.append(percentExpression);
		}
		if (suffix != null) {
			sb.append(suffix);
		}
		return sb.toString();
	}

	/**
	 * Parses the given format String and initializes this {@link AbstractMagicFormatter} by identifying it's prefix,
	 * placeholders and suffix.
	 *
	 * @param formatString The format String, that shall be parsed.
	 */
	protected void parse(String formatString) {
		if (formatString == null) {
			prefix = null;
			percentExpression = null;
			suffix = null;
			return;
		}

		Matcher matcher = FORMAT_PATTERN.matcher(formatString);
		if (!matcher.matches()) {
			prefix = prefix != null ? prefix + formatString : formatString;
			percentExpression = null;
			suffix = null;
			return;
		}

		String prefixMatch = matcher.group(1);
		String percentMatch = matcher.group(2);
		String suffixMatch = matcher.group(3);

		if (percentMatch != null && percentMatch.equals("%%")) {
			// we go recursive trying to find the first true % pattern
			if (prefixMatch != null) {
				prefix = prefix != null ? prefix + prefixMatch : prefixMatch;
			}
			prefix = prefix != null ? prefix + "%" : "%";
			parse(suffixMatch);
			return;
		}

		if (prefixMatch != null && prefixMatch.length() != 0) {
			prefix = prefix != null ? prefix + prefixMatch : prefixMatch;
		}
		if (percentMatch == null || percentMatch.length() == 0) {
			percentExpression = null;
		} else {
			percentExpression = new PercentExpression(percentMatch);
		}
		if (suffixMatch == null || suffixMatch.length() == 0) {
			suffix = null;
		} else {
			suffix = suffixMatch.replace("%%", "%");
		}
	}
}
