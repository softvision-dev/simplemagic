package com.j256.simplemagic.pattern.components.criterion;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.criterion.operator.Operator;

import java.io.IOException;

/**
 * Implementing classes represent a criterion definition from a line in magic (5) format.
 */
public interface MagicCriterion<VALUE_TYPE, OPERATOR_TYPE extends Operator> {

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
	OPERATOR_TYPE getOperator();

	/**
	 * Returns true, if the current criterion does not define a matching operation and instead shall be skipped during
	 * the evaluation.
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
	MagicCriterionResult<VALUE_TYPE, OPERATOR_TYPE> isMatch(byte[] data, int currentReadOffset) throws IOException,
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
