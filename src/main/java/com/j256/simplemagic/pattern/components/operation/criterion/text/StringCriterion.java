package com.j256.simplemagic.pattern.components.operation.criterion.text;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.operation.criterion.ExtractedValue;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.operation.criterion.modifiers.TextCriterionModifiers;

/**
 * <b>Represents a String criterion from a line in magic (5) format.</b>
 * <blockquote>
 * <b>Caveat: </b>
 * The herby implemented version 5.38 of Magic(5) is partially incompatible with previous versions. For example the
 * manpage for Magic(5) version 5.04 states that:
 * </blockquote>
 * <blockquote>
 * <i>
 * The string type specification can be optionally followed by /[Bbc]*. The ''B'' flag compacts whitespace in the
 * target, which must contain at least one whitespace character. If the magic has n consecutive blanks, the target
 * needs at least n consecutive blanks to match. The ''b'' flag treats every blank in the target as an optional blank.
 * Finally the ''c'' flag, specifies case insensitive matching: lowercase characters in the magic match both lower and
 * upper case characters in the target, whereas upper case characters in the magic only match uppercase characters in
 * the target.
 * </i>
 * </blockquote>
 * <blockquote>
 * This contradicts the 'b' flag definition of version 5.38 directly. Also 5.04 contradicts 5.38 by defining some flags
 * to influence the target (actual value) instead of the magic (expected value) and vice versa. Therefore 5.04 flags can
 * not be supported. Without allowing to select a specific Magic version first.
 * </blockquote>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * The format of the source fragment files that are used to build this database is as follows: Each line of a fragment
 * file specifies a test to be performed. A test compares the data starting at a particular offset in the file with a
 * byte value, a string or a numeric value. If the test succeeds, a message is printed.
 * </i>
 * </p>
 * <p>
 * This criterion represents such a test. This test checks:
 * </p>
 * <p>
 * <i>
 * A string of bytes. The string type specification can be optionally followed by /[WwcCtbT]*. The “W” flag compacts
 * whitespace in the target, which must contain at least one whitespace character. If the magic has n consecutive
 * blanks, the target needs at least n consecutive blanks to match. The “w” flag treats every blank in the magic as
 * an optional blank. The “c” flag specifies case insensitive matching: lower case characters in the magic match both
 * lower and upper case characters in the target, whereas upper case characters in the magic only match upper case
 * characters in the target. The “C” flag specifies case insensitive matching: upper case characters in the magic match
 * both lower and upper case characters in the target, whereas lower case characters in the magic only match upper case
 * characters in the target. To do a complete case insensitive match, specify both “c” and “C”. The “t” flag forces the
 * test to be done for text files, while the “b” flag forces the test to be done for binary files. The “T” flag causes
 * the string to be trimmed, i.e. leading and trailing whitespace is deleted before the string is printed.
 * </i>
 * </p>
 * <p>
 * <i>
 * For string values, the string from the file must match the specified string. The operators =, < and > (but not &) can
 * be applied to strings. The length used for matching is that of the string argument in the magic file. This means that
 * a line can match any non-empty string (usually used to then print the string), with >\0 (because all non-empty
 * strings are greater than the empty string).
 * </i>
 * </p>
 */
public class StringCriterion extends AbstractTextCriterion {

	private boolean compactWhiteSpace = false;
	private boolean optionalWhiteSpace = false;
	private boolean lowerCaseInsensitivity = false;
	private boolean upperCaseInsensitivity = false;
	private boolean printTrimmed = false;

	/**
	 * Creates a new {@link StringCriterion} as found in a {@link MagicPattern}. The criterion shall define one String
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public StringCriterion() throws MagicPatternException {
		super(MagicOperator.EQUALS);
	}

	/**
	 * Returns true, if whitespaces shall be compacted in the compared String.
	 * <p>
	 * <i>
	 * The “W” flag compacts whitespace in the target, which must contain at least one whitespace character. If the
	 * magic has n consecutive blanks, the target needs at least n consecutive blanks to match.
	 * </i>
	 * </p>
	 *
	 * @return True, if whitespaces shall be compacted in the compared String.
	 */
	public boolean isCompactWhiteSpaces() {
		return compactWhiteSpace;
	}

	/**
	 * Returns true, if whitespaces are optional in the compared String.
	 * <p>
	 * <i>
	 * The “w” flag treats every blank in the magic as
	 * an optional blank.
	 * </i>
	 * </p>
	 *
	 * @return True, if whitespaces are optional in the compared String.
	 */
	public boolean isOptionalWhiteSpace() {
		return optionalWhiteSpace;
	}

	/**
	 * Returns true, if lower case characters in the search String shall be case insensitive.
	 * <p>
	 * <i>
	 * The “c” flag specifies case insensitive matching: lower case characters in the magic match both
	 * lower and upper case characters in the target, whereas upper case characters in the magic only match upper case
	 * characters in the target.
	 * </i>
	 * </p>
	 *
	 * @return True, if lower case characters in the search String shall be case insensitive.
	 */
	public boolean isLowerCaseInsensitivity() {
		return lowerCaseInsensitivity;
	}

	/**
	 * Returns true, if upper case characters in the search String shall be case insensitive.
	 * <p>
	 * <i>
	 * The “C” flag specifies case insensitive matching: upper case characters in the magic match both lower and upper
	 * case characters in the target, whereas lower case characters in the magic only match upper case characters in
	 * the target.
	 * </i>
	 * </p>
	 *
	 * @return True, if upper case characters in the search String shall be case insensitive.
	 */
	public boolean isUpperCaseInsensitivity() {
		return upperCaseInsensitivity;
	}

	/**
	 * Returns true, if the matching String shall be trimmed before returning it as a matching value.
	 * <p>
	 * <i>
	 * The “T” flag causes the string to be trimmed, i.e. leading and trailing whitespace is deleted before the string
	 * is printed.
	 * </i>
	 * </p>
	 *
	 * @return True, if upper case characters in the search String shall be case insensitive.
	 */
	public boolean isPrintTrimmed() {
		return printTrimmed;
	}

	/**
	 * Returns the value, that is actually found in the data at the expected position. Length is depending on expected
	 * value for String criteria - length is therefore always ignored. May not return null directly,
	 * wrap 'null' value using {@link ExtractedValue} instead.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @param length            Does not influence the actual value for String criteria - value is irrelevant.
	 * @param invertEndianness  Whether the currently determined endianness shall be inverted.
	 * @return The {@link ExtractedValue}, that shall match the criterion.
	 */
	@Override
	public ExtractedValue<String> getActualValue(byte[] data, int currentReadOffset, int length, boolean invertEndianness) {
		String expectedString = getExpectedValue();
		if (expectedString == null) {
			return new ExtractedValue<String>(null, currentReadOffset);
		}
		int expectationLength = expectedString.length();
		int actualCharacterOffset = currentReadOffset;
		int expectedCharacterOffset = 0;
		StringBuilder resultValueBuilder = new StringBuilder();

		for (; expectedCharacterOffset < expectationLength; expectedCharacterOffset++) {
			// Collect the next expected character.
			char expectedCharacter = expectedString.charAt(expectedCharacterOffset);
			boolean expectedIsWhitespace = Character.isWhitespace(expectedCharacter);

			// If we reached the end of our data, without matching our expectations, the match failed.
			if (actualCharacterOffset >= data.length) {
				// Except: all characters left to expect were optional whitespaces.
				if (isOptionalWhiteSpace()) {
					for (; expectedCharacterOffset < expectationLength; expectedCharacterOffset++) {
						if (!Character.isWhitespace(expectedString.charAt(expectedCharacterOffset))) {
							return new ExtractedValue<String>(null, currentReadOffset);
						}
						resultValueBuilder.append(' ');
					}
					break;
				} else {
					return new ExtractedValue<String>(null, currentReadOffset);
				}
			}

			// Collect the compared actual character.
			char actualCharacter = (char) (data[actualCharacterOffset] & 0xFF);
			boolean actualIsWhitespace = Character.isWhitespace(actualCharacter);

			// Skip optional whitespaces.
			if (isOptionalWhiteSpace() && expectedIsWhitespace && !actualIsWhitespace) {
				resultValueBuilder.append(' ');
				continue;
			}

			// Compact whitespaces?
			if (isCompactWhiteSpaces() && expectedIsWhitespace) {
				// Collect number of expected whitespaces.
				int expectedWhitespaces = 0;
				//noinspection ConstantConditions
				while (expectedIsWhitespace && expectedCharacterOffset < expectationLength) {
					resultValueBuilder.append(' ');
					expectedWhitespaces++;
					expectedCharacterOffset++;
					if (!(expectedIsWhitespace = Character.isWhitespace(expectedString.charAt(expectedCharacterOffset)))) {
						expectedCharacterOffset--;
					}
				}

				// Collect number of actual whitespaces.
				int actualWhitespaces = 0;
				//noinspection ConstantConditions
				while (actualIsWhitespace && actualCharacterOffset < data.length) {
					actualWhitespaces++;
					actualCharacterOffset++;
					actualIsWhitespace = Character.isWhitespace((char) (data[actualCharacterOffset] & 0xFF));
				}

				// If not enough actual whitespaces are found, the match failed.
				if (actualWhitespaces < expectedWhitespaces && !isOptionalWhiteSpace()) {
					return new ExtractedValue<String>(null, currentReadOffset);
				}

				continue;
			}
			// Actual character is processed and must be collected. Prepare to read next character.
			resultValueBuilder.append(actualCharacter);
			actualCharacterOffset++;
		}

		return new ExtractedValue<String>(resultValueBuilder.toString(), actualCharacterOffset);
	}

	/**
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @param invertEndianness  True, if the endianness of extracted data shall be inverted for this test.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 */
	@Override
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset, boolean invertEndianness) {
		// verify the starting offset
		if (currentReadOffset < 0 || (data == null)) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}

		// Collect actual and expected value.
		ExtractedValue<String> actualValue = getActualValue(data, currentReadOffset, -1, invertEndianness);
		String expectedValue = getExpectedValue();
		if (actualValue.getValue() == null || expectedValue == null ||
				actualValue.getValue().length() != expectedValue.length()) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}

		// Compare expected and actual value.
		for (int index = 0; index < expectedValue.length(); index++) {
			// Collect next actual and expected character.
			char actualCharacter = actualValue.getValue().charAt(index);
			char expectedCharacter = expectedValue.charAt(index);
			boolean lastCharacter = (index == expectedValue.length() - 1);

			// If it does not match return with failure.
			if (!isCharacterMatching(actualCharacter, getOperator(), expectedCharacter,
					lastCharacter, isLowerCaseInsensitivity(), isUpperCaseInsensitivity())) {
				return new MagicCriterionResult<String>(false, this, currentReadOffset);
			}
		}

		// When reaching this, everything matches. Collect The matching characters and return the result.
		return new MagicCriterionResult<String>(true, this, actualValue.getSuggestedNextReadOffset(),
				isPrintTrimmed() ? actualValue.getValue().trim() : actualValue.getValue());
	}

	/**
	 * Parse the type appended modifiers of this {@link MagicCriterion}.
	 *
	 * @param modifiers The type appended modifiers.
	 */
	@Override
	protected void parseTypeAppendedModifiers(String modifiers) {
		TextCriterionModifiers textFlagsAndModifiers = new TextCriterionModifiers(modifiers);
		if (textFlagsAndModifiers.isEmpty()) {
			return;
		}

		// The string type specification can be optionally followed by /[WwcCtbT]*.
		for (char ch : textFlagsAndModifiers.getCharacterModifiers()) {
			switch (ch) {
				// The “W” flag compacts whitespace in the target, which must contain at least one whitespace
				// character. If the magic has n consecutive blanks, the target needs at least n consecutive
				// blanks to match.
				case 'W':
					this.compactWhiteSpace = true;
					break;
				// The “w” flag treats every blank in the magic as an optional blank.
				case 'w':
					this.optionalWhiteSpace = true;
					break;
				// The “c” flag specifies case insensitive matching: lower case characters in the magic match
				// both lower and upper case characters in the target, whereas upper case characters in the
				// magic only match upper case characters in the target.
				case 'c':
					this.lowerCaseInsensitivity = true;
					break;
				// The “C” flag specifies case insensitive matching: upper case characters in the magic match
				// both lower and upper case characters in the target, whereas lower case characters in the
				// magic only match upper case characters in the target.
				case 'C':
					this.upperCaseInsensitivity = true;
					break;
				// The “T” flag causes the string to be trimmed, i.e. leading and trailing whitespace is deleted
				// before the string is printed.
				case 'T':
					this.printTrimmed = true;
					break;
				// The “t” flag forces the test to be done for text files, while the “b” flag forces the test to
				// be done for binary files.
				case 't':
				case 'b':
					// differentiation of text and binary files is currently ignored.
					break;
			}
		}
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() {
		String pattern = getExpectedValue();
		if (pattern == null || pattern.length() < 4) {
			return null;
		} else {
			return new byte[]{(byte) pattern.charAt(0), (byte) pattern.charAt(1), (byte) pattern.charAt(2),
					(byte) pattern.charAt(3)};
		}
	}
}
