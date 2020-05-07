package com.j256.simplemagic.pattern.components.criterion.types;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.criterion.AbstractMagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.criterion.operator.StringOperator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a String criterion from a line in magic (5) format.
 * <p>
 * From the magic(5) man page: A string of bytes. The string type specification can be optionally followed by /[Bbc]*.
 * The ``B'' flag compacts whitespace in the target, which must contain at least one whitespace character. If the magic
 * has n consecutive blanks, the target needs at least n consecutive blanks to match. The ``b'' flag treats every blank
 * in the target as an optional blank. Finally the ``c'' flag, specifies case insensitive matching: lower-case
 * characters in the magic match both lower and upper case characters in the target, whereas upper case characters in
 * the magic only match upper-case characters in the target.
 * </p>
 *
 * @author graywatson
 */
public class StringCriterion extends AbstractMagicCriterion<String, StringOperator> {

	private static final Pattern TYPE_PATTERN = Pattern.compile("[^/]+(/\\d+)?(/[BbcwWt]*)?");

	private String testValue = "";
	private int maxSearchOffset = 0;
	private boolean compactWhiteSpace = false;
	private boolean optionalWhiteSpace = false;
	private boolean caseInsensitive = false;

	/**
	 * Creates a new {@link StringCriterion} as found in a {@link MagicPattern}. The criterion shall define one String
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public StringCriterion() throws MagicPatternException {
		super(StringOperator.EQUALS);
	}

	/**
	 * Returns the maximum offset for String extraction.
	 *
	 * @return The maximum offset for String extraction.
	 */
	public int getMaxSearchOffset() {
		return maxSearchOffset;
	}

	/**
	 * Whether whitespaces shall be compacted, when searching data for a match.
	 *
	 * @return True, if whitespaces shall be compacted, when searching data for a match.
	 */
	public boolean isCompactWhiteSpace() {
		return compactWhiteSpace;
	}

	/**
	 * Whether whitespaces are treated as optional, when searching data for a match.
	 *
	 * @return True, if whitespaces shall be treated as optional, when searching data for a match.
	 */
	public boolean isOptionalWhiteSpace() {
		return optionalWhiteSpace;
	}

	/**
	 * Whether the search for a match is case sensitive.
	 *
	 * @return True, if the search for a match is case sensitive.
	 */
	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	/**
	 * Returns a String value, that must be found in binary data to match this criterion.
	 *
	 * @return The String value, that must be matched.
	 */
	@Override
	public String getTestValue() {
		return testValue;
	}

	/**
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed (possibly due to a malformed criterion.)
	 */
	@Override
	public MagicCriterionResult<String, StringOperator> isMatch(byte[] data, int currentReadOffset)
			throws MagicPatternException {
		return findOffsetMatch(data, null, currentReadOffset, data.length);
	}

	/**
	 * Searches for a match in either the given byte or char arrays (selecting the array, that is not 'null').
	 * Automatically fails, when both the data and the char array are set to 'null'.
	 *
	 * @param data              The byte array, that shall be searched for a match. (When set to 'null' chars shall be
	 *                          searched instead.)
	 * @param chars             The char array, that shall be searched for a match. (When set to 'null' data shall be
	 *                          searched instead. If data is not 'null' the chars array will be ignored.)
	 * @param currentReadOffset The search offset.
	 * @param terminalOffset    The terminal search offset.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 */
	protected MagicCriterionResult<String, StringOperator> findOffsetMatch(
			byte[] data, final char[] chars, int currentReadOffset, int terminalOffset
	) {
		// verify the starting offset
		if (currentReadOffset < 0 || (data == null && chars == null)) {
			return new MagicCriterionResult<String, StringOperator>(this, currentReadOffset);
		}

		int targetPos = currentReadOffset;
		boolean lastMagicCompactWhitespace = false;
		for (int magicPos = 0; magicPos < getTestValue().length(); magicPos++) {
			char magicCh = getTestValue().charAt(magicPos);
			boolean lastChar = (magicPos == getTestValue().length() - 1);
			// did we reach the end?
			if (targetPos >= terminalOffset) {
				return new MagicCriterionResult<String, StringOperator>(this, currentReadOffset);
			}
			char targetCh;
			if (data == null) {
				targetCh = chars[targetPos];
			} else if (chars == null) {
				targetCh = (char) (data[targetPos] & 0xFF);
			} else {
				return new MagicCriterionResult<String, StringOperator>(this, currentReadOffset);
			}
			targetPos++;

			// if it matches, we can continue
			if (matchCharacter(targetCh, magicCh, lastChar)) {
				if (isCompactWhiteSpace()) {
					lastMagicCompactWhitespace = Character.isWhitespace(magicCh);
				}
				continue;
			}

			// if it doesn't match, maybe the target is a whitespace
			if ((lastMagicCompactWhitespace || isOptionalWhiteSpace()) && Character.isWhitespace(targetCh)) {
				do {
					if (targetPos >= terminalOffset) {
						break;
					}
					if (data == null) {
						targetCh = chars[targetPos];
					} else {
						targetCh = (char) (data[targetPos] & 0xFF);
					}
					targetPos++;
				} while (Character.isWhitespace(targetCh));
				// now that we get to the first non-whitespace, it must match
				if (matchCharacter(targetCh, magicCh, lastChar)) {
					if (isCompactWhiteSpace()) {
						lastMagicCompactWhitespace = Character.isWhitespace(magicCh);
					}
					continue;
				}
				// if it doesn't match, check the case insensitive
			}

			// maybe it doesn't match because of case insensitive handling and magic-char is lowercase
			if (isCaseInsensitive() && Character.isLowerCase(magicCh)) {
				if (matchCharacter(Character.toLowerCase(targetCh), magicCh, lastChar)) {
					// matches
					continue;
				}
				// upper-case characters must match
			}

			return new MagicCriterionResult<String, StringOperator>(this, currentReadOffset);
		}

		char[] resultChars;
		if (data == null) {
			resultChars = Arrays.copyOfRange(chars, currentReadOffset, targetPos);
		} else {
			resultChars = new char[targetPos - currentReadOffset];
			for (int i = 0; i < resultChars.length; i++) {
				resultChars[i] = (char) (data[currentReadOffset + i] & 0xFF);
			}
		}

		String extractedValue = new String(resultChars);
		return new MagicCriterionResult<String, StringOperator>(this, targetPos, extractedValue);
	}

	/**
	 * Evaluates a specific {@link StringOperator} for a selected extracted character and the test character, that shall
	 * be matched.
	 *
	 * @param extractedChar The extracted character, that shall match.
	 * @param testChar      The test character, that shall be matched.
	 * @param lastChar      True, if the character is the last character to be compared.
	 * @return True, if the extracted character matches.
	 */
	protected boolean matchCharacter(char extractedChar, char testChar, boolean lastChar) {
		switch (getOperator()) {
			case NOT_EQUALS:
				return extractedChar != testChar;
			case GREATER_THAN:
				if (lastChar) {
					return extractedChar > testChar;
				} else {
					return extractedChar >= testChar;
				}
			case LESS_THAN:
				if (lastChar) {
					return extractedChar < testChar;
				} else {
					return extractedChar <= testChar;
				}
			case EQUALS:
			default:
				return extractedChar == testChar;
		}
	}

	/**
	 * Parse the given raw definition to initialize this {@link MagicCriterion} instance.
	 *
	 * @param magicPattern  The pattern, this criterion is defined for. (For reflective access.) A 'null' value will
	 *                      be treated as invalid.
	 * @param rawDefinition The raw definition of the {@link MagicCriterion} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	@Override
	public void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		super.parse(magicPattern, rawDefinition);
		if (rawDefinition == null || rawDefinition.length() == 0) {
			throw new MagicPatternException("Criterion definition is empty.");
		}

		// Try to find additional flags and patterns for the String type.
		Matcher matcher = TYPE_PATTERN.matcher(getMagicPattern().getType().getTypeString());
		// If no additional flags and patterns are contained, the rawDefinition value is the complete test String.
		this.testValue = rawDefinition;
		if (!matcher.matches()) {
			return;
		}

		// parse maxSearchOffset.
		String lengthStr = matcher.group(1);
		if (lengthStr != null && lengthStr.length() > 1) {
			try {
				// skip the '/'.
				this.maxSearchOffset = Integer.decode(lengthStr.substring(1));
			} catch (NumberFormatException e) {
				// may not be able to get here.
				throw new MagicPatternException(
						String.format("Invalid format for search length: '%s'", lengthStr.substring(1))
				);
			}
		}

		// parse flags.
		String flagsStr = matcher.group(2);
		if (flagsStr != null) {
			// look at flags/modifiers.
			for (char ch : flagsStr.toCharArray()) {
				switch (ch) {
					case 'B':
						this.compactWhiteSpace = true;
						break;
					case 'b':
						this.optionalWhiteSpace = true;
						break;
					case 'c':
						this.caseInsensitive = true;
						break;
					case 't':
					case 'w':
					case 'W':
						// XXX: no idea what these do.
						break;
					default:
						break;
				}
			}
		}

		// Parse operator.
		if (this.testValue == null || this.testValue.length() == 0) {
			return;
		}
		StringOperator operator = StringOperator.forOperator(this.testValue.charAt(0));
		if (operator != null) {
			setOperator(operator);
			this.testValue = this.testValue.substring(1);
		}

		// Preprocess the String value.
		this.testValue = PatternUtils.unescapePattern(this.testValue);
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() {
		String pattern = getTestValue();
		if (pattern == null || pattern.length() < 4) {
			return null;
		} else {
			return new byte[]{(byte) pattern.charAt(0), (byte) pattern.charAt(1), (byte) pattern.charAt(2),
					(byte) pattern.charAt(3)};
		}
	}
}
