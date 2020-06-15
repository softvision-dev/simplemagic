package com.j256.simplemagic.pattern.components.operation.criterion.text;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.operation.criterion.AbstractMagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.numeric.AbstractNumericCriterion;

/**
 * <b>Represents a text criterion from a line in magic (5) format.</b>
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
 * <i>
 * Each top-level magic pattern (see below for an explanation of levels) is classified as text or binary according to
 * the types used. Types “regex” and “search” are classified as text tests, unless non-printable characters are used
 * in the pattern. All other tests are classified as binary. A top-level pattern is considered to be a test text when
 * all its patterns are text patterns; otherwise, it is considered to be a binary pattern. When matching a file, binary
 * patterns are tried first; if no match is found, and the file looks like text, then its encoding is determined and the
 * text patterns are tried.
 * </i>
 * </p>
 * <p>
 * <i>
 * The numeric types may optionally be followed by & and a numeric value, to specify that the value is to be AND'ed with
 * the numeric value before any comparisons are done. Prepending a u to the type indicates that ordered comparisons
 * should be unsigned.
 * </i>
 * <p>
 * Attention: The hereby defined "binary" test types shall be named "numeric" criteria for the purposes of this library
 * ({@link AbstractNumericCriterion}), additionally String, PascalString and String16 criteria are also counted as "text"
 * based criteria. ({@link AbstractTextCriterion})
 * </p>
 * <p>
 * When later on "binary" criteria shall be executed first to optimize performance - as suggested by the manpage - then
 * this shall be represented by a boolean field: "binaryCriterion".
 * </p>
 */
public abstract class AbstractTextCriterion extends AbstractMagicCriterion<String> {

	private String expectedValue;

	public static final MagicOperator[] TEXT_OPERATORS = new MagicOperator[]{
			MagicOperator.GREATER_THAN, MagicOperator.LESS_THAN, MagicOperator.EQUALS
	};

	/**
	 * Creates a new {@link AbstractTextCriterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @param defaultOperator The default {@link MagicOperator} defining the operation, that must be successful, for this
	 *                        criterion	to be met. (might be replaced during parsing and must never be 'null'.)
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public AbstractTextCriterion(MagicOperator defaultOperator) throws MagicPatternException {
		super(defaultOperator);
	}

	/**
	 * Returns a String value, that must be found in binary data to match this criterion.
	 *
	 * @return The String value, that must be matched.
	 */
	@Override
	public String getExpectedValue() {
		return expectedValue;
	}

	/**
	 * Sets a String value, that must be found in binary data to match this criterion.
	 *
	 * @param expectedValue The String value, that must be matched.
	 */
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
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
	public void doParse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		if (rawDefinition == null || rawDefinition.isEmpty()) {
			throw new MagicPatternException("Criterion definition is empty.");
		}

		// Try to find additional flags and patterns for the String type.
		parseTypeAppendedModifiers(getMagicPattern().getType().getModifiers());

		// Parse operator and test value.
		MagicOperator operator = MagicOperator.forOperator(rawDefinition.charAt(0), TEXT_OPERATORS);
		setOperator(operator);
		setExpectedValue(PatternUtils.escapePattern(operator != null ? rawDefinition.substring(1) : rawDefinition));
	}

	/**
	 * Evaluates a specific {@link MagicOperator} for a selected extracted character and the test character, that shall
	 * be matched.
	 *
	 * @param actualCharacter      The extracted character, that shall match.
	 * @param expectedCharacter    The test character, that shall be matched.
	 * @param operator             The comparison operator.
	 * @param lowerCaseInsensitive Case insensitivity for lower case characters.
	 * @param upperCaseInsensitive Case insensitivity for upper case characters.
	 * @param lastCharacter        True, if the character is the last character to be compared.
	 * @return True, if the extracted character matches.
	 */
	protected boolean isCharacterMatching(char actualCharacter, MagicOperator operator, char expectedCharacter,
			boolean lastCharacter, boolean lowerCaseInsensitive, boolean upperCaseInsensitive) {
		// Handle case sensitivity.
		if (lowerCaseInsensitive && Character.isLowerCase(expectedCharacter) && Character.isUpperCase(actualCharacter)) {
			return isCharacterMatching(Character.toLowerCase(actualCharacter), operator, expectedCharacter,
					lastCharacter, false, upperCaseInsensitive);
		}
		if (upperCaseInsensitive && Character.isUpperCase(expectedCharacter) && Character.isLowerCase(actualCharacter)) {
			return isCharacterMatching(Character.toUpperCase(actualCharacter), operator, expectedCharacter,
					lastCharacter, lowerCaseInsensitive, false);
		}

		// Match actual and expected characters.
		boolean matches;
		switch (operator) {
			case NOT_EQUALS:
				matches = actualCharacter != expectedCharacter;
				break;
			case GREATER_THAN:
				matches = lastCharacter ? actualCharacter > expectedCharacter : actualCharacter >= expectedCharacter;
				break;
			case LESS_THAN:
				matches = lastCharacter ? actualCharacter < expectedCharacter : actualCharacter <= expectedCharacter;
				break;
			case EQUALS:
			default:
				matches = actualCharacter == expectedCharacter;
		}

		return matches;
	}
}
