package com.j256.simplemagic.pattern.components.operation.criterion;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;

import java.util.regex.Pattern;

/**
 * <b>Extending classes represent a criterion definition from a line in magic (5) format.</b>
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
 * Numeric values may be preceded by a character indicating the operation to be performed. It may be =, to specify that
 * the value from the file must equal the specified value, <, to specify that the value from the file must be less than
 * the specified value, >, to specify that the value from the file must be greater than the specified value, &, to
 * specify that the value from the file must have set all of the bits that are set in the specified value, ^, to specify
 * that the value from the file must have clear any of the bits that are set in the specified value, or ~, the value
 * specified after is negated before tested. x, to specify that any value will match. If the character is omitted, it
 * is assumed to be =. Operators &, ^, and ~ don't work with floats and doubles. The operator ! specifies that the line
 * matches if the test does not succeed.
 * </i>
 * </p>
 * <p>
 * <i>
 * Numeric values are specified in C form; e.g. 13 is decimal, 013 is octal, and 0x13 is hexadecimal.
 * </i>
 * </p>
 * <p>
 * <i>
 * Numeric operations are not performed on date types, instead the numeric value is interpreted as an offset.
 * </i>
 * </p>
 * <p>
 * <i>
 * For string values, the string from the file must match the specified string. The operators =, < and > (but not &)
 * can be applied to strings. The length used for matching is that of the string argument in the magic file. This means
 * that a line can match any non-empty string (usually used to then print the string), with >\0 (because all non-empty
 * strings are greater than the empty string).
 * </i>
 * </p>
 * <p>
 * <i>
 * Dates are treated as numerical values in the respective internal representation.
 * </i>
 * </p>
 * <p>
 * <i>
 * The special test x always evaluates to true.
 * </i>
 * </p>
 * <p>
 * The hereby defined tests are named "criteria" for the purposes of this library.
 * </p>
 */
public abstract class AbstractMagicCriterion<VALUE_TYPE>
		implements MagicCriterion<VALUE_TYPE> {

	//TODO: reevaluate Criteria implementation especially for String and REGEX type - reimplement according to Manpage.

	private static final Pattern NOOP_CRITERION = Pattern.compile("^[xX]$");

	private MagicPattern magicPattern;
	private MagicOperator operator;
	private boolean noopCriterion;

	/**
	 * Creates a new {@link MagicCriterion} as found in a {@link MagicPattern}. The criterion shall define one evaluation
	 * contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @param defaultOperator The default {@link MagicOperator} defining the operation, that must be successful, for this
	 *                        criterion	to be met. (might be replaced during parsing and must never be 'null'.)
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public AbstractMagicCriterion(MagicOperator defaultOperator) throws MagicPatternException {
		if (defaultOperator == null) {
			throw new MagicPatternException("Invalid criterion initialization.");
		}
		this.operator = defaultOperator;
	}

	/**
	 * Returns the {@link MagicOperator} defining the operation, that must be successful, for this criterion	to be met.
	 *
	 * @return The {@link MagicOperator} defining the operation, that must be successful, for this criterion	to be met.
	 * (Must never return 'null'.)
	 */
	@Override
	public MagicOperator getOperator() {
		return operator;
	}

	/**
	 * Returns true, if this criterion does not represent a matchable operation and should be skipped during evaluation.
	 *
	 * @return True, this criterion does not represent a matchable operation.
	 */
	@Override
	public boolean isNoopCriterion() {
		return noopCriterion;
	}

	/**
	 * Sets the {@link MagicOperator} defining the operation, that must be successful, for this criterion to be met.
	 *
	 * @param operator The {@link MagicOperator} defining the operation, that must be successful, for this criterion to be met.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public void setOperator(MagicOperator operator) throws MagicPatternException {
		if (operator == null) {
			throw new MagicPatternException("Invalid criterion initialization.");
		}
		this.operator = operator;
	}

	/**
	 * Returns the {@link MagicPattern}, that is defining this {@link MagicCriterion} for reflective access.
	 *
	 * @return The {@link MagicPattern}, that is defining this {@link MagicCriterion} for reflective access.
	 */
	public MagicPattern getMagicPattern() throws MagicPatternException {
		if (this.magicPattern == null) {
			throw new MagicPatternException("missing magic pattern for criterion.");
		}
		return magicPattern;
	}

	/**
	 * Sets the {@link MagicPattern}, that is defining this {@link MagicCriterion}.
	 *
	 * @param magicPattern The {@link MagicPattern}, that is defining this {@link MagicCriterion}. A 'null' value will
	 *                     be treated as invalid.
	 * @throws MagicPatternException Shall be thrown for invalid parameters.
	 */
	protected void setMagicPattern(MagicPattern magicPattern) throws MagicPatternException {
		if (magicPattern == null) {
			throw new MagicPatternException("Invalid criterion initialization.");
		}
		this.magicPattern = magicPattern;
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
	public final void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		setMagicPattern(magicPattern);
		this.noopCriterion = rawDefinition != null && NOOP_CRITERION.matcher(rawDefinition).matches();
		if (!isNoopCriterion()) {
			doParse(magicPattern, rawDefinition);
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
	protected abstract void doParse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException;
}
