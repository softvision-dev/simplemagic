package com.j256.simplemagic.pattern.components.criterion.types;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.operator.NumericOperator;

/**
 * Represents an Id3 Integer criterion from a line in magic (5) format.
 * <p>
 * A four-byte integer value where the high bit of each byte is ignored.
 * </p>
 *
 * @author graywatson
 */
public class Id3Criterion extends AbstractNumberCriterion {

	/**
	 * Creates a new {@link Id3Criterion} as found in a {@link MagicPattern}. The criterion shall define one
	 * Id3 Integer evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @param endianness The expected endianness of compared binary data.
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public Id3Criterion(EndianType endianness) throws MagicPatternException {
		super(endianness);
	}

	/**
	 * Evaluates a {@link NumericOperator#EQUALS} for this criterion.
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
	 * Evaluates a {@link NumericOperator#NOT_EQUALS} for this criterion.
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
	 * Evaluates a {@link NumericOperator#GREATER_THAN} for this criterion.
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
	 * Evaluates a {@link NumericOperator#LESS_THAN} for this criterion.
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
	 * Evaluates a {@link NumericOperator#AND_ALL_SET} for this criterion.
	 *
	 * @param extractedValue The value, that shall have set all the same bits as the expected test value.
	 * @return True, if the given value has the same bits set, as the expected test value.
	 */
	@Override
	protected boolean testAndAllSet(Number extractedValue) {
		return getTestValue() != null && extractedValue != null &&
				((extractedValue.longValue() & getTestValue().longValue()) == getTestValue().longValue());
	}

	/**
	 * Evaluates a {@link NumericOperator#AND_ALL_CLEARED} for this criterion.
	 *
	 * @param extractedValue The value, that shall have cleared all bits set in the expected test value.
	 * @return True, if the given value has cleared all bits set in the expected test value.
	 */
	@Override
	protected boolean testAndAllCleared(Number extractedValue) {
		return getTestValue() != null && extractedValue != null &&
				((extractedValue.longValue() & getTestValue().longValue()) == 0);
	}

	/**
	 * Evaluates a {@link NumericOperator#NEGATE} for this criterion.
	 *
	 * @param extractedValue The value, that shall have set all the same bits as the expected test value, after being
	 *                       negated.
	 * @return True, if the given value has the same bits set, as the expected test value, after being negated.
	 */
	@Override
	protected boolean testNegate(Number extractedValue) {
		return (extractedValue.longValue() == (~getTestValue().longValue() & 0xFFFFFFFFL));
	}
}
