package com.j256.simplemagic.pattern.components.operation;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.MagicOperation;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.numeric.*;
import com.j256.simplemagic.pattern.components.operation.criterion.text.*;
import com.j256.simplemagic.pattern.components.operation.instruction.*;
import com.j256.simplemagic.pattern.components.operation.instruction.types.DefaultInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.types.IndirectInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.types.NameInstruction;
import com.j256.simplemagic.pattern.components.operation.instruction.types.UseInstruction;

/**
 * Creates an operation, that is capable of evaluating values of a specific {@link OperationType}.
 */
public class MagicOperationFactory {

	/**
	 * Not instantiatable, use static factory method {@link MagicOperationFactory#createOperation(OperationType, String)}
	 * instead.
	 */
	private MagicOperationFactory() {
	}

	/**
	 * <p>
	 * This static factory method either produces a {@link MagicCriterion}, that is capable of evaluating values from
	 * data for a specific {@link OperationType} and therefore provides one evaluation result, that influences the
	 * outcome of {@link MagicPattern#isMatch(byte[], int, MagicEntries)}.
	 * </p>
	 * <p>
	 * Or it produces a {@link MagicInstruction}, that is defining a more complex instruction, such as the creation or
	 * indirect call of a named pattern.
	 * </p>
	 * <p>
	 * Both types shall be summarized as: {@link MagicOperation}
	 * </p>
	 *
	 * @param operationType The criterion type, that determines the type of criterion, that should be used.
	 * @return A criterion fit to evaluate such values. (Must not return null, but may return a no-op criterion
	 * {@link MagicCriterion#isNoopCriterion()})
	 */
	public static MagicOperation createOperation(OperationType operationType, @SuppressWarnings("unused") String rawDefinition)
			throws MagicPatternException {
		switch (operationType) {
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

			case LITTLE_ENDIAN_UTF16_STRING:
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

			case BIG_ENDIAN_UTF16_STRING:
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

			case NAME:
				return new NameInstruction();
			case USE:
				return new UseInstruction();
			case INDIRECT:
				return new IndirectInstruction();
			case DEFAULT:
				return new DefaultInstruction();
			default:
				throw new MagicPatternException(String.format("Unknown magic pattern type: '%s'", rawDefinition));
		}
	}
}
