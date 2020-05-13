package com.j256.simplemagic.pattern.components;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;

import java.io.IOException;

/**
 * <b>An implementing class represents a test definition from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * The value to be compared with the value from the file. If the type is numeric, this value is specified in C form; if
 * it is a string, it is specified as a C string with the usual escapes permitted (e.g. \n for new-line).
 * </p>
 * </i>
 * <p>
 * <i>
 * Numeric values may be preceded by a character indicating the operation to be performed. It may be = to specify that
 * the value from the file must equal the specified value, < to specify that the value from the file must be less than
 * the specified value, > to specify that the value from the file must be greater than the specified value, & to specify
 * that the value from the file must have set all of the bits that are set in the specified value, ^ to specify that the
 * value from the file must have clear any of the bits that are set in the specified value, or ~ the value specified
 * after is negated before tested. x to specify that any value will match. If the character is omitted, it is assumed
 * to be = Operators & ^ and ~ don't work with floats and doubles. The operator ! specifies that the line matches if
 * the test does not succeed.
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
 * For string values, the string from the file must match the specified string. The operators = < and > (but not & can
 * be applied to strings. The length used for matching is that of the string argument in the magic file. This means
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
 */
public interface MagicCriterion<VALUE_TYPE> {

	/**
	 * Returns a value, that must be found in binary data to match this criterion.
	 *
	 * @return The value, that must be matched.
	 */
	VALUE_TYPE getTestValue();

	/**
	 * The operator, that shall compare the predefined test value ({@link MagicCriterion#getTestValue()}) to an extracted
	 * value.
	 *
	 * @return The operator, that defines the matching operation.
	 */
	MagicOperator getOperator();

	/**
	 * Returns true, if the current criterion does not define a matching operation and instead shall be skipped during
	 * the evaluation. (This represents the criterion: 'x')
	 *
	 * @return True, if this criterion is not defining a matchable operation.
	 */
	boolean isNoopCriterion();

	/**
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 * @throws IOException           Shall be thrown, if accessing the data failed.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed (possibly due to a malformed criterion.)
	 */
	MagicCriterionResult<VALUE_TYPE> isMatch(byte[] data, int currentReadOffset) throws IOException,
			MagicPatternException;

	/**
	 * Parse the given raw definition to initialize this {@link MagicCriterion} instance.
	 *
	 * @param magicPattern  The pattern, this criterion is defined for. (For reflective access.) A 'null' value shall
	 *                      be treated as invalid.
	 * @param rawDefinition The raw definition of the {@link MagicCriterion} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException;

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 * @throws MagicPatternException Shall be thrown, when accessing pattern information failed.
	 */
	byte[] getStartingBytes() throws MagicPatternException;
}
