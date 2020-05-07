package com.j256.simplemagic.pattern.components.extractor;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.criterion.types.CriterionType;
import com.j256.simplemagic.pattern.components.extractor.types.*;

/**
 * Creates a {@link MagicExtractor}, that is capable of extracting values for a specific {@link CriterionType}.
 */
public class MagicExtractorFactory {

	/**
	 * Not instantiatable, use static factory method {@link MagicExtractorFactory#createExtractor(CriterionType)}
	 * instead.
	 */
	private MagicExtractorFactory() {
	}

	/**
	 * This static factory method produces a {@link MagicExtractor}, that is capable of extracting values from data for
	 * a specific {@link CriterionType} and therefore provides the values, that may be compared, by a call to
	 * {@link MagicPattern#isMatch(byte[])}.
	 *
	 * @param criterionType The criterion type, that determines the type of extractor, that should be used.
	 * @return An extractor fit to extract such values. (Must not return null)
	 */
	public static MagicExtractor<?> createExtractor(CriterionType criterionType) {
		switch (criterionType) {
			case PSTRING:
				return new PascalStringExtractor();
			case BYTE:
				return new NumberExtractor(EndianType.NATIVE, 1);
			case SHORT:
				return new NumberExtractor(EndianType.NATIVE, 2);
			case INTEGER:
			case DATE:
			case LOCAL_DATE:
				return new NumberExtractor(EndianType.NATIVE, 4);
			case FLOAT:
				return new FloatExtractor(EndianType.NATIVE, 4);
			case DOUBLE:
				return new DoubleExtractor(EndianType.NATIVE, 8);
			case LONG:
			case LONG_DATE:
			case LONG_LOCAL_DATE:
				return new NumberExtractor(EndianType.NATIVE, 8);

			case LITTLE_ENDIAN_TWO_BYTE_STRING:
				return new String16Extractor(EndianType.LITTLE);
			case LITTLE_ENDIAN_SHORT:
				return new NumberExtractor(EndianType.LITTLE, 2);
			case LITTLE_ENDIAN_INTEGER:
			case LITTLE_ENDIAN_DATE:
			case LITTLE_ENDIAN_LOCAL_DATE:
				return new NumberExtractor(EndianType.LITTLE, 4);
			case LITTLE_ENDIAN_ID3:
				return new Id3Extractor(EndianType.LITTLE, 4);
			case LITTLE_ENDIAN_FLOAT:
				return new FloatExtractor(EndianType.LITTLE, 4);
			case LITTLE_ENDIAN_DOUBLE:
				return new DoubleExtractor(EndianType.LITTLE, 8);
			case LITTLE_ENDIAN_LONG:
			case LITTLE_ENDIAN_LONG_DATE:
			case LITTLE_ENDIAN_LONG_LOCAL_DATE:
				return new NumberExtractor(EndianType.LITTLE, 8);

			case MIDDLE_ENDIAN_INTEGER:
			case MIDDLE_ENDIAN_DATE:
			case MIDDLE_ENDIAN_LOCAL_DATE:
				return new NumberExtractor(EndianType.MIDDLE, 4);

			case BIG_ENDIAN_TWO_BYTE_STRING:
				return new String16Extractor(EndianType.BIG);
			case BIG_ENDIAN_SHORT:
				return new NumberExtractor(EndianType.BIG, 2);
			case BIG_ENDIAN_INTEGER:
			case BIG_ENDIAN_LOCAL_DATE:
			case BIG_ENDIAN_DATE:
				return new NumberExtractor(EndianType.BIG, 4);
			case BIG_ENDIAN_ID3:
				return new Id3Extractor(EndianType.BIG, 4);
			case BIG_ENDIAN_FLOAT:
				return new FloatExtractor(EndianType.BIG, 4);
			case BIG_ENDIAN_DOUBLE:
				return new DoubleExtractor(EndianType.BIG, 8);
			case BIG_ENDIAN_LONG:
			case BIG_ENDIAN_LONG_DATE:
			case BIG_ENDIAN_LONG_LOCAL_DATE:
				return new NumberExtractor(EndianType.BIG, 8);

			// Many String based types implement a more complex and specialized extraction method, therefore we are using
			// The default no-op extractor in the following cases:
			case STRING:
			case SEARCH:
			case REGEX:
			default:
				// Otherwise use the no-op DefaultExtractor.
				return new DefaultExtractor();
		}
	}
}
