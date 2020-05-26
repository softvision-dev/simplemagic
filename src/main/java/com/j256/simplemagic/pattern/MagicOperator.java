package com.j256.simplemagic.pattern;

import java.util.Arrays;

/**
 * <b>Represents an operator as commonly defined for different parts of the magic pattern format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * Numeric criteria:
 * </p>
 * <p>
 * <i>
 * Numeric values may be preceded by a character indicating the operation to be performed. It may be =, to specify
 * that the value from the file must equal the specified value, <, to specify that the value from the file must be less
 * than the specified value, >, to specify that the value from the file must be greater than the specified value, &, to
 * specify that the value from the file must have set all of the bits that are set in the specified value, ^, to specify
 * that the value from the file must have clear any of the bits that are set in the specified value, or ~, the value
 * specified after is negated before tested. x, to specify that any value will match. If the character is omitted, it
 * is assumed to be =. Operators &, ^, and ~ don't work with floats and doubles. The operator ! specifies that the
 * line matches if the test does not succeed.
 * </i>
 * </p>
 * <p>
 * String criteria:
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
 * Numeric type modifiers:
 * </p>
 * <p>
 * <i>
 * The numeric types may optionally be followed by & and a numeric value, to specify that the value is to be AND'ed with
 * the numeric value before any comparisons are done. Prepending a u to the type indicates that ordered comparisons
 * should be unsigned.
 * </i>
 * </p>
 * <p>
 * Indirect offsets:
 * </p>
 * <p>
 * <i>
 * If this indirect offset cannot be used directly, simple calculations are possible: appending [+-*%/&|^]number
 * inside parentheses allows one to modify the value read from the file before it is used as an offset:
 * </i>
 * </p>
 * Attention: Not all hereby defined operators may be valid for all parts of the magic (5) format. This is a central
 * definition preventing redundancies, determined Operators must still be handled defensively.
 */
public enum MagicOperator {

	// arithmetic operators
	ADD('+'),
	SUBTRACT('-'),
	MULTIPLY('*'),
	DIVIDE('/'),
	MODULO('%'),

	// comparison operators
	EQUALS('='),
	NOT_EQUALS('!'),
	GREATER_THAN('>'),
	LESS_THAN('<'),

	// bitwise operators
	CONJUNCTION('&'),
	DISJUNCTION('|'),
	CONTRAVALENCE('^'),
	COMPLEMENT('~');

	private final char name;

	/**
	 * Instantiates an enum {@link MagicOperator} value.
	 *
	 * @param name The magic pattern name for this {@link MagicOperator}.
	 */
	MagicOperator(char name) {
		this.name = name;
	}

	/**
	 * Returns the matching operator for the given char.
	 *
	 * @param name           The magic pattern name that shall be found.
	 * @param knownOperators The found operator must be one of the defined known operators.
	 * @return A matching operator or null, if no match has been found.
	 */
	public static MagicOperator forOperator(char name, MagicOperator... knownOperators) {
		for (MagicOperator operator : values()) {
			if (operator.name != name || (knownOperators != null &&
					knownOperators.length > 0 && !Arrays.asList(knownOperators).contains(operator))) {
				continue;
			}
			return operator;
		}
		return null;
	}

	/**
	 * Returns the matching operator for the first non whitespace character in the given String.
	 *
	 * @param pattern        The magic pattern an operator shall be found for.
	 * @param knownOperators The found operator must be one of the defined known operators.
	 * @return A matching operator or null, if no match has been found.
	 */
	public static MagicOperator forPattern(String pattern, MagicOperator... knownOperators) {
		return pattern == null ? null : pattern.trim().isEmpty() ? null : forOperator(pattern.trim().charAt(0), knownOperators);
	}
}
