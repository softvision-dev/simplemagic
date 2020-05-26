package com.j256.simplemagic.pattern.components.operation.criterion.numeric;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;

/**
 * <b>Represents a Float criterion from a line in magic (5) format.</b>
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
 * A 32-bit single precision IEEE floating point number [...].
 * </i>
 * </p>
 */
public class FloatCriterion extends AbstractNumberCriterion {

	/**
	 * Creates a new {@link FloatCriterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * Float evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public FloatCriterion(EndianType endianness) throws MagicPatternException {
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
		return extractedValue.longValue() & operand.longValue();
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
		return extractedValue.longValue() | operand.longValue();
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
		return extractedValue.floatValue() + operand.floatValue();
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
		return extractedValue.floatValue() - operand.floatValue();
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
		return extractedValue.floatValue() * operand.floatValue();
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
		return extractedValue.floatValue() / operand.floatValue();
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
		return extractedValue.floatValue() % operand.floatValue();
	}

	/**
	 * Parses the test value of a {@link FloatCriterion} to the according numeric type {@link Float}.
	 *
	 * @param valueString The value, that shall be parsed.
	 * @return A typed numeric value, according to {@link FloatCriterion}.
	 * @throws MagicPatternException Shall be thrown, if the test value could not be parsed.
	 */
	@Override
	protected Number decodeValueString(String valueString) throws MagicPatternException {
		try {
			return Float.parseFloat(valueString);
		} catch (NumberFormatException ex) {
			throw new MagicPatternException(String.format("Could not parse float from: '%s'", valueString));
		}
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
				extractedValue.floatValue() == getTestValue().floatValue();
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
				extractedValue.floatValue() != getTestValue().floatValue();
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
				extractedValue.floatValue() > getTestValue().floatValue();
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
				extractedValue.floatValue() < getTestValue().floatValue();
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
	 * Evaluates a {@link MagicOperator#CONJUNCTION} for this criterion.
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

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() {
		return null;
	}
}
