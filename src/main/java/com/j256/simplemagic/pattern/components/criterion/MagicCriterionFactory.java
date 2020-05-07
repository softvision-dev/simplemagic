package com.j256.simplemagic.pattern.components.criterion;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.criterion.types.*;

/**
 * Creates a criterion, that is capable of evaluating values of a specific {@link CriterionType}.
 */
public class MagicCriterionFactory {

	/**
	 * Not instantiatable, use static factory method {@link MagicCriterionFactory#createCriterion(CriterionType, String)}
	 * instead.
	 */
	private MagicCriterionFactory() {
	}

	/**
	 * This static factory method produces a {@link MagicCriterion}, that is capable of evaluating values from data for
	 * a specific {@link CriterionType} and therefore provides one evaluation result, that influences the outcome of
	 * {@link MagicPattern#isMatch(byte[])}.
	 *
	 * @param criterionType The criterion type, that determines the type of criterion, that should be used.
	 * @return A criterion fit to evaluate such values. (Must not return null, but may return a no-op criterion
	 * {@link MagicCriterion#isNoopCriterion()})
	 */
	public static MagicCriterion<?, ?> createCriterion(CriterionType criterionType, String rawDefinition)
			throws MagicPatternException {
		if (rawDefinition == null || rawDefinition.equals("x")) {
			return new DefaultCriterion(true);
		}
		switch (criterionType) {
			case STRING:
				return new StringCriterion();
			case PSTRING:
				return new PascalStringCriterion();
			case SEARCH:
				return new SearchCriterion();
			case REGEX:
				return new RegexCriterion();
			case BYTE:
				return new ByteCriterion(EndianType.NATIVE);
			case SHORT:
				return new ShortCriterion(EndianType.NATIVE);
			case INTEGER:
			case DATE:
			case LOCAL_DATE:
				return new IntegerCriterion(EndianType.NATIVE);
			case FLOAT:
				return new FloatCriterion(EndianType.NATIVE);
			case DOUBLE:
				return new DoubleCriterion(EndianType.NATIVE);
			case LONG:
			case LONG_DATE:
			case LONG_LOCAL_DATE:
				return new LongCriterion(EndianType.NATIVE);

			case LITTLE_ENDIAN_TWO_BYTE_STRING:
				return new String16Criterion(EndianType.LITTLE);
			case LITTLE_ENDIAN_SHORT:
				return new ShortCriterion(EndianType.LITTLE);
			case LITTLE_ENDIAN_INTEGER:
			case LITTLE_ENDIAN_DATE:
			case LITTLE_ENDIAN_LOCAL_DATE:
				return new IntegerCriterion(EndianType.LITTLE);
			case LITTLE_ENDIAN_ID3:
				return new Id3Criterion(EndianType.LITTLE);
			case LITTLE_ENDIAN_FLOAT:
				return new FloatCriterion(EndianType.LITTLE);
			case LITTLE_ENDIAN_DOUBLE:
				return new DoubleCriterion(EndianType.LITTLE);
			case LITTLE_ENDIAN_LONG:
			case LITTLE_ENDIAN_LONG_DATE:
			case LITTLE_ENDIAN_LONG_LOCAL_DATE:
				return new LongCriterion(EndianType.LITTLE);

			case MIDDLE_ENDIAN_INTEGER:
			case MIDDLE_ENDIAN_DATE:
			case MIDDLE_ENDIAN_LOCAL_DATE:
				return new IntegerCriterion(EndianType.MIDDLE);

			case BIG_ENDIAN_TWO_BYTE_STRING:
				return new String16Criterion(EndianType.BIG);
			case BIG_ENDIAN_SHORT:
				return new ShortCriterion(EndianType.BIG);
			case BIG_ENDIAN_INTEGER:
			case BIG_ENDIAN_DATE:
			case BIG_ENDIAN_LOCAL_DATE:
				return new IntegerCriterion(EndianType.BIG);
			case BIG_ENDIAN_ID3:
				return new Id3Criterion(EndianType.BIG);
			case BIG_ENDIAN_FLOAT:
				return new FloatCriterion(EndianType.BIG);
			case BIG_ENDIAN_DOUBLE:
				return new DoubleCriterion(EndianType.BIG);
			case BIG_ENDIAN_LONG:
			case BIG_ENDIAN_LONG_DATE:
			case BIG_ENDIAN_LONG_LOCAL_DATE:
				return new LongCriterion(EndianType.BIG);
			default:
				return new DefaultCriterion(false);
		}
	}
}
