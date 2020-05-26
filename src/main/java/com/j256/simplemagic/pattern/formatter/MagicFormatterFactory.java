package com.j256.simplemagic.pattern.formatter;

import com.j256.simplemagic.pattern.components.operation.OperationType;
import com.j256.simplemagic.pattern.formatter.types.DefaultFormatter;
import com.j256.simplemagic.pattern.formatter.types.IntegerDateFormatter;
import com.j256.simplemagic.pattern.formatter.types.LongDateFormatter;
import com.j256.simplemagic.pattern.matching.MatchingResult;

import java.util.TimeZone;

/**
 * Creates a {@link MagicFormatter}, that is capable of formatting extracted values for a specific {@link OperationType}.
 */
public class MagicFormatterFactory {

	/**
	 * Not instantiatable, use static factory method {@link MagicFormatterFactory#createFormatter(OperationType, String)}
	 * instead.
	 */
	private MagicFormatterFactory() {
	}

	/**
	 * This static factory method produces a {@link MagicFormatter}, that is capable of formatting extracted values for
	 * a specific {@link OperationType} and therefore provides the result Strings, that may be appended to a
	 * {@link MatchingResult}.
	 *
	 * @param criterionType The criterion type, that determines the type of formatter, that should be used.
	 * @param format        The formatting instructions, that shall be applied to extracted values.
	 * @return A formatter fit to format such values. (Must not return null)
	 */
	public static MagicFormatter createFormatter(OperationType criterionType, String format) {
		switch (criterionType) {
			case DATE:
			case LITTLE_ENDIAN_DATE:
			case MIDDLE_ENDIAN_DATE:
			case BIG_ENDIAN_DATE:
				return new IntegerDateFormatter(TimeZone.getTimeZone("UTC"), format);
			case LOCAL_DATE:
			case LITTLE_ENDIAN_LOCAL_DATE:
			case MIDDLE_ENDIAN_LOCAL_DATE:
			case BIG_ENDIAN_LOCAL_DATE:
				return new IntegerDateFormatter(TimeZone.getDefault(), format);
			case LONG_DATE:
			case LITTLE_ENDIAN_LONG_DATE:
			case BIG_ENDIAN_LONG_DATE:
				return new LongDateFormatter(TimeZone.getTimeZone("UTC"), format);
			case LONG_LOCAL_DATE:
			case LITTLE_ENDIAN_LONG_LOCAL_DATE:
			case BIG_ENDIAN_LONG_LOCAL_DATE:
				return new LongDateFormatter(TimeZone.getDefault(), format);
			default:
				return new DefaultFormatter(format);
		}
	}
}
