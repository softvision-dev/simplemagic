package com.j256.simplemagic.pattern.matching;

import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.PatternUtils;

import static com.j256.simplemagic.pattern.PatternUtils.UNKNOWN_TYPE_NAME;

/**
 * The result summary of a {@link MagicPattern#isMatch(byte[])} call.
 */
public class MatchingResult {
	private final StringBuilder formattedResult = new StringBuilder();
	private MatchingState matchingState = MatchingState.NO_MATCH;
	private String typeName;
	private String mimeType;
	private int matchingLevel = 0;

	/**
	 * An instance collects and provides evaluation results of a {@link MagicPattern#isMatch(byte[])} call.
	 *
	 * @param typeName The file type name, that has been found. (Setting to 'null' will result in a value of:
	 *                 {@link PatternUtils#UNKNOWN_TYPE_NAME})
	 * @param mimeType The mimeType, that has been found. (May be set to 'null')
	 */
	public MatchingResult(String typeName, String mimeType) {
		this.typeName = typeName == null ? UNKNOWN_TYPE_NAME : typeName;
		this.mimeType = mimeType;
	}

	/**
	 * Returns the determined file type name, that has been found.
	 *
	 * @return The file type name, that has been found. (Must never return 'null', but may return
	 * {@link PatternUtils#UNKNOWN_TYPE_NAME})
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Sets the determined file type name, that has been found.
	 *
	 * @param typeName The file type name, that has been found. (Setting to 'null' will result in a value of:
	 *                 {@link PatternUtils#UNKNOWN_TYPE_NAME})
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName == null ? UNKNOWN_TYPE_NAME : typeName;
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
	 * @return The String representation of this evaluation result. (Must never return 'null')
	 */
	@Override
	public String toString() {
		return formattedResult.toString();
	}
}
