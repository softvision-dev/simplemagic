package com.j256.simplemagic.pattern.components.operation.criterion;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.MagicOperation;

import java.io.IOException;

/**
 * <b>An implementing class represents a test definition from a line in magic (5) format.</b>
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
public interface MagicCriterion<VALUE_TYPE> extends MagicOperation {

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
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 * @throws IOException           Shall be thrown, if accessing the data failed.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed (possibly due to a malformed criterion.)
	 */
	MagicCriterionResult<VALUE_TYPE> isMatch(byte[] data, int currentReadOffset, boolean invertEndianness)
			throws IOException, MagicPatternException;
}
