package com.j256.simplemagic.pattern.matching;

import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.PatternUtils;

import static com.j256.simplemagic.pattern.PatternUtils.UNKNOWN_TYPE_NAME;

/**
 * <b>An instance of this class represents a result line from the magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * Each line of a fragment file specifies a test to be performed. A test compares the data starting at a particular
 * offset in the file with a byte value, a string or a numeric value. If the test succeeds, a message is printed.
 * </i>
 * </p>
 * <p>
 * <i>
 * The message to be printed if the comparison succeeds. If the string contains a printf(3) format specification, the
 * value from the file (with any specified masking performed) is printed using the message as the format string.
 * If the string begins with ``\b'' the message printed is the remainder of the string with no whitespace added before
 * it: multiple matches are normally separated by a single space.
 * </i>
 * </p>
 * The result summary of a {@link MagicPattern#isMatch(byte[])} call.
 */
public class MatchingResult {
	private final StringBuilder formattedResult = new StringBuilder();
	private MatchingState matchingState = MatchingState.NO_MATCH;
	private String message;
	private String mimeType;
	private int matchingLevel = 0;

	/**
	 * An instance collects and provides evaluation results of a {@link MagicPattern#isMatch(byte[])} call.
	 *
	 * @param message  The file type description, that has been found. (Setting to 'null' will result in a value of:
	 *                 {@link PatternUtils#UNKNOWN_TYPE_NAME})
	 * @param mimeType The mimeType, that has been found. (May be set to 'null')
	 */
	public MatchingResult(String message, String mimeType) {
		this.message = message == null ? UNKNOWN_TYPE_NAME : message;
		this.mimeType = mimeType;
	}

	/**
	 * Returns the determined file type name, that has been found.
	 *
	 * @return The file type name, that has been found. (Must never return 'null', but may return
	 * {@link PatternUtils#UNKNOWN_TYPE_NAME})
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the determined file type name, that has been found.
	 *
	 * @param message The file type name, that has been found. (Setting to 'null' will result in a value of:
	 *                {@link PatternUtils#UNKNOWN_TYPE_NAME})
	 */
	public void setMessage(String message) {
		this.message = message == null ? UNKNOWN_TYPE_NAME : message;
	}

	/**
	 * The MimeType, that has been found.
	 *
	 * @return The mimeType, that has been found. (May return 'null', when unspecified.)
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Sets the MimeType, that has been found.
	 *
	 * @param mimeType The MimeType, that has been found. (May be set to 'null', when unspecified)
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Returns the level of the deepest matching {@link MagicPattern}.
	 *
	 * @return The level of the deepest matching {@link MagicPattern}.
	 */
	public int getMatchingLevel() {
		return matchingLevel;
	}

	/**
	 * Sets the level of the deepest matching {@link MagicPattern}.
	 *
	 * @param matchingLevel The level of the deepest matching {@link MagicPattern}.
	 */
	public void setMatchingLevel(int matchingLevel) {
		this.matchingLevel = matchingLevel;
	}

	/**
	 * Returns the {@link MatchingState} of the evaluation.
	 *
	 * @return The {@link MatchingState} of the evaluation. (Defaults to {@link MatchingState#NO_MATCH}, when not
	 * actively changed.)
	 */
	public MatchingState getMatchingState() {
		return matchingState;
	}

	/**
	 * Sets the {@link MatchingState} of the evaluation.
	 *
	 * @param matchingState The {@link MatchingState} of the evaluation.
	 */
	public void setMatchingState(MatchingState matchingState) {
		this.matchingState = matchingState;
	}

	/**
	 * Appends the given String to the String representation of this evaluation result. Said representation shall be
	 * returned by a call to {@link MatchingResult#toString()}.
	 *
	 * @param value The value, that shall be appended. (Appending 'null' will be ignored.)
	 */
	public void append(String value) {
		if (value != null) {
			formattedResult.append(value);
		}
	}

	/**
	 * Clears the String representation of this evaluation result. Said representation shall be returned by a call
	 * to {@link MatchingResult#toString()}.
	 */
	public void clear() {
		formattedResult.setLength(0);
	}

	/**
	 * Returns the character length of the String representation of this evaluation result. Said representation shall
	 * be returned by a call to {@link MatchingResult#toString()}.
	 *
	 * @return The length of the String representation of this evaluation result.
	 */
	public int length() {
		return formattedResult.length();
	}

	/**
	 * Returns the String representation of this evaluation result. This shall list all file type information, that has
	 * been collected.
	 *
	 * @return The String representation of this evaluation result. (Must never return 'null')
	 */
	@Override
	public String toString() {
		return formattedResult.toString();
	}
}
