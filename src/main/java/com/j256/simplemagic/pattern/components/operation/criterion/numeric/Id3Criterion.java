package com.j256.simplemagic.pattern.components.operation.criterion.numeric;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;

/**
 * <b>Represents an Id3 Integer criterion from a line in magic (5) format.</b>
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
 * A four-byte integer value where the high bit of each byte is ignored.
 * </i>
 * </p>
 */
public class Id3Criterion extends AbstractNumberCriterion {

	/**
	 * Creates a new {@link Id3Criterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * Id3 Integer evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public Id3Criterion(EndianType endianness) throws MagicPatternException {
		super(endianness);
	}

	/**
	 * Evaluates a {@link MagicOperator#CONJUNCTION} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyConjunction(Number extractedValue, Number operand) {
		return extractedValue.intValue() & operand.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#DISJUNCTION} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyDisjunction(Number extractedValue, Number operand) {
		return extractedValue.intValue() | operand.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#CONTRAVALENCE} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyContravalence(Number extractedValue, Number operand) {
		return extractedValue.intValue() ^ operand.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#COMPLEMENT} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyComplement(Number extractedValue) {
		return ~extractedValue.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#ADD} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyAddition(Number extractedValue, Number operand) {
		return extractedValue.intValue() + operand.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#SUBTRACT} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applySubtraction(Number extractedValue, Number operand) {
		return extractedValue.intValue() - operand.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#MULTIPLY} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyMultiplication(Number extractedValue, Number operand) {
		return extractedValue.intValue() * operand.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#DIVIDE} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyDivision(Number extractedValue, Number operand) {
		return extractedValue.intValue() / operand.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#MODULO} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyModulo(Number extractedValue, Number operand) {
		return extractedValue.intValue() % operand.intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#EQUALS} for this criterion.
	 *
	 * @param extractedValue The value, that shall be equal to the expected test value.
	 * @return True, if the given value is equal to the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	@Override
	protected boolean testEqual(Number extractedValue) throws MagicPatternException {
		if (getMagicPattern().getType().isUnsigned()) {
			return getTestValue() != null && extractedValue != null &&
					extractedValue.longValue() == getTestValue().longValue();
		}
		return getTestValue() != null && extractedValue != null &&
				extractedValue.intValue() == getTestValue().intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#NOT_EQUALS} for this criterion.
	 *
	 * @param extractedValue The value, that shall not be equal to the expected test value.
	 * @return True, if the given value is not equal to the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	@Override
	protected boolean testNotEqual(Number extractedValue) throws MagicPatternException {
		if (getMagicPattern().getType().isUnsigned()) {
			return getTestValue() != null && extractedValue != null &&
					extractedValue.longValue() != getTestValue().longValue();
		}
		return getTestValue() != null && extractedValue != null &&
				extractedValue.intValue() != getTestValue().intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#GREATER_THAN} for this criterion.
	 *
	 * @param extractedValue The value, that shall be great than the expected test value.
	 * @return True, if the given value is greater than the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	@Override
	protected boolean testGreaterThan(Number extractedValue) throws MagicPatternException {
		if (getMagicPattern().getType().isUnsigned()) {
			return getTestValue() != null && extractedValue != null &&
					extractedValue.longValue() > getTestValue().longValue();
		}
		return getTestValue() != null && extractedValue != null &&
				extractedValue.intValue() > getTestValue().intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#LESS_THAN} for this criterion.
	 *
	 * @param extractedValue The value, that shall be less than the expected test value.
	 * @return True, if the given value is less than the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	@Override
	protected boolean testLessThan(Number extractedValue) throws MagicPatternException {
		if (getMagicPattern().getType().isUnsigned()) {
			return getTestValue() != null && extractedValue != null &&
					extractedValue.longValue() < getTestValue().longValue();
		}
		return getTestValue() != null && extractedValue != null &&
				extractedValue.intValue() < getTestValue().intValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#CONJUNCTION} for this criterion.
	 *
	 * @param extractedValue The value, that shall have set all the same bits as the expected test value.
	 * @return True, if the given value has the same bits set, as the expected test value.
	 */
	@Override
	protected boolean testAnd(Number extractedValue) {
		return getTestValue() != null && extractedValue != null &&
				((extractedValue.longValue() & getTestValue().longValue()) == getTestValue().longValue());
	}

	/**
	 * Evaluates a {@link MagicOperator#CONTRAVALENCE} for this criterion.
	 *
	 * @param extractedValue The value, that shall have cleared all bits set in the expected test value.
	 * @return True, if the given value has cleared all bits set in the expected test value.
	 */
	@Override
	protected boolean testXor(Number extractedValue) {
		return getTestValue() != null && extractedValue != null &&
				((extractedValue.longValue() & getTestValue().longValue()) == 0);
	}

	/**
	 * Evaluates a {@link MagicOperator#COMPLEMENT} for this criterion.
	 *
	 * @param extractedValue The value, that shall have set all the same bits as the expected test value, after being
	 *                       negated.
	 * @return True, if the given value has the same bits set, as the expected test value, after being negated.
	 */
	@Override
	protected boolean testComplement(Number extractedValue) {
		return (extractedValue.longValue() == (~getTestValue().longValue() & 0xFFFFFFFFL));
	}
}
