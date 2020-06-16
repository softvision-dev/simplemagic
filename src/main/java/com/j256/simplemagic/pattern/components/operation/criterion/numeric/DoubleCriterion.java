package com.j256.simplemagic.pattern.components.operation.criterion.numeric;

import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.ExtractedValue;

/**
 * <b>Represents a Double criterion from a line in magic (5) format.</b>
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
 * A 64-bit double precision IEEE floating point number [...].
 * </i>
 * </p>
 */
public class DoubleCriterion extends AbstractNumericCriterion {

	/**
	 * Creates a new {@link DoubleCriterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * Double evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public DoubleCriterion(EndianType endianness) throws MagicPatternException {
		super(endianness);
	}

	/**
	 * Returns the value, that is actually found in the data at the expected position. May not return null directly,
	 * wrap 'null' value using {@link ExtractedValue} instead.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @param length            The value length in bytes. (-1 if no length shall be given.)
	 * @param invertEndianness  Whether the currently determined endianness shall be inverted.
	 * @return The {@link ExtractedValue}, that shall match the criterion.
	 */
	@SuppressWarnings("DuplicatedCode")
	@Override
	public ExtractedValue<Number> getActualValue(byte[] data, int currentReadOffset, int length, boolean invertEndianness) {
		if (length < 0 || (currentReadOffset + length) > data.length) {
			return new ExtractedValue<Number>(null, currentReadOffset);
		}

		Long longValue = EndianConverterFactory.createEndianConverter(
				invertEndianness ? getEndianness().getInvertedEndianType() : getEndianness()
		).convertNumber(data, currentReadOffset, length);
		return new ExtractedValue<Number>(longValue == null ? null : Double.longBitsToDouble(longValue),
				currentReadOffset + length);
	}

	/**
	 * Applies the found type appended modifier to an extracted value, using the given operator.
	 * <p>
	 * According to the Magic(5) Manpage:
	 * </p>
	 *
	 * @param extractedValue The first operand of the current modification operation.
	 * @param operator       The operator of the current modification operation.
	 * @param modifier       The second operand of the current modification operation.
	 * @return The resulting modified {@link Number}.
	 * @throws MagicPatternException Shall be thrown, if adapting the modifier failed. Always fail for unknown/failed
	 *                               modification operations, instead of risking to evaluate erroneous values,
	 *                               or worse : To not report definition gaps/syntax errors.
	 */
	@Override
	protected Number applyModifier(Number extractedValue, MagicOperator operator, Number modifier)
			throws MagicPatternException {
		//noinspection DuplicatedCode
		switch (operator) {
			// The numeric types may optionally be followed by & and a numeric value, to specify that the value is to be
			// AND'ed with the numeric value before any comparisons are done.
			case CONJUNCTION:
				return extractedValue.longValue() & modifier.longValue();
			// No direct documentation for this modifier could be found - may be part of file(1) >v.35 "data arithmetic"
			case SUBTRACT:
				return extractedValue.longValue() - modifier.longValue();
			default:
				throw new MagicPatternException(
						String.format("Unknown modification operation: '%s %s'", operator.name(), modifier)
				);
		}
	}

	/**
	 * Evaluates the current {@link AbstractNumericCriterion}, comparing the current test value to the given extracted
	 * value, using the given operator.
	 *
	 * @param actualValue The first operand of the current comparison operation.
	 * @param operator    The operator of the current comparison operation.
	 * @param expectedValue   The second operand of the current comparison operation.
	 * @return True, if the comparison operation is matching.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed or the operator is unknown. Always Fail
	 *                               for unknown/failed evaluation operations, instead of risking to create false
	 *                               positives, or worse : To not report definition gaps/syntax errors.
	 */
	@Override
	protected boolean evaluate(Number actualValue, MagicOperator operator, Number expectedValue)
			throws MagicPatternException {
		switch (operator) {
			case EQUALS:
				return expectedValue != null && actualValue != null &&
						actualValue.doubleValue() == expectedValue.doubleValue();
			case NOT_EQUALS:
				return expectedValue != null && actualValue != null &&
						actualValue.doubleValue() != expectedValue.doubleValue();
			case GREATER_THAN:
				return expectedValue != null && actualValue != null &&
						actualValue.doubleValue() > expectedValue.doubleValue();
			case LESS_THAN:
				return expectedValue != null && actualValue != null &&
						actualValue.doubleValue() < expectedValue.doubleValue();
			// Operators &, ^, and ~ don't work with floats and doubles.
			case CONJUNCTION:
			case CONTRAVALENCE:
			case COMPLEMENT:
				throw new MagicPatternException(
						String.format("Invalid comparison operation for double type: '%s %s'", operator.name(), expectedValue)
				);
			default:
				throw new MagicPatternException(
						String.format("Unknown comparison operation: '%s %s'", operator.name(), expectedValue)
				);
		}
	}

	/**
	 * Parses the test value of a {@link DoubleCriterion} to the according numeric type {@link Double}.
	 *
	 * @param valueString The value, that shall be parsed.
	 * @return A typed numeric value, according to {@link DoubleCriterion}.
	 * @throws MagicPatternException Shall be thrown, if the test value could not be parsed.
	 */
	@Override
	protected Number decodeValueString(String valueString) throws MagicPatternException {
		try {
			return Double.parseDouble(valueString);
		} catch (NumberFormatException ex) {
			throw new MagicPatternException(String.format("Could not parse double from: '%s'", valueString));
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
		return null;
	}
}
