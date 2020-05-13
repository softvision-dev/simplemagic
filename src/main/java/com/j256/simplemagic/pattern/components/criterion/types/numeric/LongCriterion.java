package com.j256.simplemagic.pattern.components.criterion.types.numeric;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.MagicCriterion;

/**
 * Represents a Long criterion from a line in magic (5) format.
 * <p>
 * Eight-byte numeric values were constituted as "quad" when the magic file spec was written.
 * </p>
 *
 * @author graywatson
 */
public class LongCriterion extends AbstractNumberCriterion {

	/**
	 * Creates a new {@link LongCriterion} as found in a {@link MagicPattern}. The criterion shall define one Long
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public LongCriterion(EndianType endianness) throws MagicPatternException {
		super(endianness);
	}

	/**
	 * Evaluates a {@link MagicOperator#AND} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyConjunction(Number extractedValue, Number operand) {
		return extractedValue.longValue() & operand.longValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#OR} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyDisjunction(Number extractedValue, Number operand) {
		return extractedValue.longValue() | operand.longValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#XOR} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @param operand        The operand, that shall be applied.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyContravalence(Number extractedValue, Number operand) {
		return extractedValue.longValue() ^ operand.longValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#COMPLEMENT} modifier for this criterion.
	 *
	 * @param extractedValue The extracted value that shall be modified.
	 * @return The resulting value.
	 */
	@Override
	protected Number applyComplement(Number extractedValue) {
		return ~extractedValue.longValue();
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
		return extractedValue.longValue() + operand.longValue();
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
		return extractedValue.longValue() - operand.longValue();
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
		return extractedValue.longValue() * operand.longValue();
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
		return extractedValue.longValue() / operand.longValue();
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
		return extractedValue.longValue() % operand.longValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#EQUALS} for this criterion.
	 *
	 * @param extractedValue The value, that shall be equal to the expected test value.
	 * @return True, if the given value is equal to the expected test value.
	 */
	@Override
	protected boolean testEqual(Number extractedValue) {
		return getTestValue() != null && extractedValue != null &&
				extractedValue.longValue() == getTestValue().longValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#NOT_EQUALS} for this criterion.
	 *
	 * @param extractedValue The value, that shall not be equal to the expected test value.
	 * @return True, if the given value is not equal to the expected test value.
	 */
	@Override
	protected boolean testNotEqual(Number extractedValue) {
		return getTestValue() != null && extractedValue != null &&
				extractedValue.longValue() != getTestValue().longValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#GREATER_THAN} for this criterion.
	 *
	 * @param extractedValue The value, that shall be great than the expected test value.
	 * @return True, if the given value is greater than the expected test value.
	 */
	@Override
	protected boolean testGreaterThan(Number extractedValue) {
		return getTestValue() != null && extractedValue != null &&
				extractedValue.longValue() > getTestValue().longValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#LESS_THAN} for this criterion.
	 *
	 * @param extractedValue The value, that shall be less than the expected test value.
	 * @return True, if the given value is less than the expected test value.
	 */
	@Override
	protected boolean testLessThan(Number extractedValue) {
		return getTestValue() != null && extractedValue != null &&
				extractedValue.longValue() < getTestValue().longValue();
	}

	/**
	 * Evaluates a {@link MagicOperator#AND} for this criterion.
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
	 * Evaluates a {@link MagicOperator#XOR} for this criterion.
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
		return (extractedValue.longValue() == (~getTestValue().longValue()));
	}
}