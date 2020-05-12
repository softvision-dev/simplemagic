package com.j256.simplemagic.pattern.components.criterion.types;

import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.criterion.AbstractMagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.criterion.operator.NumericOperator;
import com.j256.simplemagic.pattern.components.extractor.MagicExtractor;
import com.j256.simplemagic.pattern.components.extractor.types.NumberExtractor;

import java.math.BigInteger;

/**
 * Represents a numeric criterion from a line in magic (5) format.
 *
 * @author graywatson
 */
public abstract class AbstractNumberCriterion extends AbstractMagicCriterion<Number, NumericOperator> {

	private final EndianType endianness;
	private Number testValue;

	/**
	 * Creates a new {@link AbstractNumberCriterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * numeric evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public AbstractNumberCriterion(EndianType endianness) throws MagicPatternException {
		super(NumericOperator.EQUALS);
		if (endianness == null) {
			throw new MagicPatternException("Invalid criterion initialization.");
		}
		this.endianness = endianness;
	}

	/**
	 * Returns the expected endianness of read binary data.
	 *
	 * @return The expected endianness of read binary data.
	 */
	public EndianType getEndianness() {
		return endianness;
	}

	/**
	 * Returns a numeric value, that must be found in binary data to match this criterion.
	 *
	 * @return The numeric value, that must be matched.
	 */
	@Override
	public Number getTestValue() {
		return testValue;
	}

	/**
	 * Evaluates a {@link NumericOperator#EQUALS} for this criterion.
	 *
	 * @param extractedValue The value, that shall be equal to the expected test value.
	 * @return True, if the given value is equal to the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testEqual(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link NumericOperator#NOT_EQUALS} for this criterion.
	 *
	 * @param extractedValue The value, that shall not be equal to the expected test value.
	 * @return True, if the given value is not equal to the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testNotEqual(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link NumericOperator#GREATER_THAN} for this criterion.
	 *
	 * @param extractedValue The value, that shall be great than the expected test value.
	 * @return True, if the given value is greater than the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testGreaterThan(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link NumericOperator#LESS_THAN} for this criterion.
	 *
	 * @param extractedValue The value, that shall be less than the expected test value.
	 * @return True, if the given value is less than the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testLessThan(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link NumericOperator#AND_ALL_SET} for this criterion.
	 *
	 * @param extractedValue The value, that shall have set all the same bits as the expected test value.
	 * @return True, if the given value has the same bits set, as the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testAndAllSet(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link NumericOperator#AND_ALL_CLEARED} for this criterion.
	 *
	 * @param extractedValue The value, that shall have cleared all bits set in the expected test value.
	 * @return True, if the given value has cleared all bits set in the expected test value.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testAndAllCleared(Number extractedValue) throws MagicPatternException;

	/**
	 * Evaluates a {@link NumericOperator#NEGATE} for this criterion.
	 *
	 * @param extractedValue The value, that shall have set all the same bits as the expected test value, after being
	 *                       negated.
	 * @return True, if the given value has the same bits set, as the expected test value, after being negated.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed with an exception.
	 */
	protected abstract boolean testNegate(Number extractedValue) throws MagicPatternException;

	/**
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed (possibly due to a malformed criterion.)
	 */
	@Override
	public MagicCriterionResult<Number, NumericOperator> isMatch(byte[] data, int currentReadOffset)
			throws MagicPatternException {
		boolean matches;
		Number extractedValue = 0;
		MagicExtractor<?> extractor = getMagicPattern().getType().getExtractor();
		if (extractor instanceof NumberExtractor) {
			extractedValue = ((NumberExtractor) extractor).extractValue(data, currentReadOffset);
		}
		if (extractedValue == null) {
			return new MagicCriterionResult<Number, NumericOperator>(this, currentReadOffset);
		}
		BigInteger andValue = getMagicPattern().getType().getAndValue();
		if (getMagicPattern().getType().getAndValue() != null) {
			extractedValue = BigInteger.valueOf(extractedValue.longValue()).and(andValue);
		}
		switch (getOperator()) {
			case NOT_EQUALS:
				matches = testNotEqual(extractedValue);
				break;
			case GREATER_THAN:
				matches = testGreaterThan(extractedValue);
				break;
			case LESS_THAN:
				matches = testLessThan(extractedValue);
				break;
			case AND_ALL_SET:
				// NOTE: we assume that we are dealing with decimal numbers here
				matches = testAndAllSet(extractedValue);
				break;
			case AND_ALL_CLEARED:
				// NOTE: we assume that we are dealing with decimal numbers here
				matches = testAndAllCleared(extractedValue);
				break;
			case NEGATE:
				// we need the mask because we are using bit negation but testing only a portion of the long
				// NOTE: we assume that we are dealing with decimal numbers here
				matches = testNegate(extractedValue);
				break;
			case EQUALS:
			default:
				matches = testEqual(extractedValue);
				break;
		}

		if (matches) {
			return new MagicCriterionResult<Number, NumericOperator>(this,
					currentReadOffset + getMagicPattern().getType().getByteLength(),
					extractedValue);
		} else {
			return new MagicCriterionResult<Number, NumericOperator>(this, currentReadOffset);
		}
	}

	/**
	 * Parses the test value of a {@link AbstractNumberCriterion} to the according numeric type {@link Number}.
	 * Override in extending classes to return a more specific numeric type.
	 *
	 * @param valueString The value, that shall be parsed.
	 * @return A typed numeric value, according to {@link AbstractNumberCriterion}.
	 * @throws MagicPatternException Shall be thrown, if the test value could not be parsed.
	 */
	protected Number decodeValueString(String valueString) throws MagicPatternException {
		return PatternUtils.parseNumericValue(valueString);
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
	public void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		super.parse(magicPattern, rawDefinition);
		if (rawDefinition == null || rawDefinition.length() == 0) {
			throw new MagicPatternException("Criterion definition is empty.");
		}

		NumericOperator operator = NumericOperator.forOperator(rawDefinition.charAt(0));
		String valueString;
		if (operator == null) {
			valueString = rawDefinition;
		} else {
			setOperator(operator);
			valueString = rawDefinition.substring(1).trim();
		}

		this.testValue = decodeValueString(valueString);
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() throws MagicPatternException {
		return EndianConverterFactory.createEndianConverter(getEndianness()).convertToByteArray(
				getTestValue().longValue(), getMagicPattern().getType().getByteLength()
		);
	}
}
