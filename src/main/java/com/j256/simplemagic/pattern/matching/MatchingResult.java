package com.j256.simplemagic.pattern.matching;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.pattern.MagicPattern;

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
 * The result summary of a {@link MagicPattern#isMatch(byte[], int, MagicEntries)} call.
 */
public class MatchingResult {

	public static final String UNKNOWN_TYPE_NAME = "unknown";

	private final StringBuilder formattedResult = new StringBuilder();
	private MatchingState matchingState = MatchingState.NO_MATCH;
	private String rawMessage;
	private String mimeType;
	private int matchingLevel = 0;

	/**
	 * An instance collects and provides evaluation results of a {@link MagicPattern#isMatch(byte[], int, MagicEntries)}
	 * call.
	 *
	 * @param rawMessage The raw and unformated file type description, that has been found. (Setting to 'null' or empty
	 *                   String, will result in a value of: {@link #UNKNOWN_TYPE_NAME})
	 * @param mimeType   The mimeType, that has been found. (May be set to 'null', when unspecified.)
	 */
	public MatchingResult(String rawMessage, String mimeType) {
		this.rawMessage = rawMessage == null || rawMessage.trim().isEmpty() ? UNKNOWN_TYPE_NAME : rawMessage;
		this.mimeType = mimeType;
	}

	/**
	 * Returns the determined file type name, that has been found.
	 *
	 * @return The file type name, that has been found. (Must never return 'null', but may return {@link #UNKNOWN_TYPE_NAME})
	 */
	public String getRawMessage() {
		return rawMessage;
	}

	/**
	 * Sets the determined file type name, that has been found.
	 *
	 * @param rawMessage The file type name, that has been found. (Setting to 'null' or empty String, will result in a
	 *                   value of: {@link #UNKNOWN_TYPE_NAME})
	 */
	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage == null || rawMessage.trim().isEmpty() ? UNKNOWN_TYPE_NAME : rawMessage;
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
		return this.matchingState;
	}

	/**
	 * Returns true, if the given matching state equals the current matching state.
	 *
	 * @param matchingState The matching state, that shall be checked.
	 * @return True, if the given matching state equals the current matching state.
	 */
	public boolean isMatchingState(MatchingState matchingState) {
		return this.matchingState.equals(matchingState);
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
	 * Returns true if the type name is currently {@link #UNKNOWN_TYPE_NAME}.
	 *
	 * @return True if the type name is currently {@link #UNKNOWN_TYPE_NAME}.
	 */
	public boolean isUnknownTypeName() {
		return UNKNOWN_TYPE_NAME.equals(this.rawMessage);
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
