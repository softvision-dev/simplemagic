package com.j256.simplemagic.pattern.components.offset;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.PatternUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>An instance of this class represents an offset modification operation for an indirect offset in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * If this indirect offset cannot be used directly, simple calculations are possible: appending [+-*%/&|^] number
 * inside parentheses allows one to modify the value read from the file before it is used as an offset:
 * </i>
 * </p>
 * <p><i>
 * [...] Finally, if you have to deal with offset/length pairs in your file, even the second value in a parenthesized
 * expression can be taken from the file itself, using another set of parentheses. Note that this additional indirect
 * offset is always relative to the start of the main indirect offset.
 * </i></p>
 * <p>
 * The "second value" following the operator shall be named "operand" for the purposes of this library.
 * </p>
 */
public class MagicOffsetModification {

	public static final MagicOperator[] OFFSET_MODIFIERS = new MagicOperator[]{
			MagicOperator.ADD, MagicOperator.SUBTRACT, MagicOperator.MULTIPLY, MagicOperator.DIVIDE,
			MagicOperator.MODULO, MagicOperator.CONJUNCTION, MagicOperator.DISJUNCTION, MagicOperator.CONTRAVALENCE
	};

	private static final Pattern OFFSET_MODIFICATION_PATTERN = Pattern.compile(
			// Find offset modification operator: if it does exist, an operand must also exist
			"([+\\-*%/&|^])(?:" +
					// Find offset modification operand as a numeric value.
					"(?:(-?[0-9a-fA-FxX]+)|(-?\\d+))" +
					// Alternatively Find indirect offset marker: optional
					"|(?:(\\()" +
					// Find operand offset (always a numeric value): must exist
					// Is always relative to the start offset of the outer indirect offset.
					"(-?[0-9a-fA-FxX]+)|(-?\\d+))" +
					// Find offset type and endianness: optional, default: .l
					"(?:.([bsilmBSIL]))?(\\)" +
					"))$");

	private final MagicOperator operator;
	private final long operand;
	private final boolean operandIndirect;
	private final MagicOffsetReadType operandReadType;

	/**
	 * Creates a new {@link MagicOffsetModification} as found in a {@link MagicIndirectOffset}. This operation shall be
	 * applied to an extracted indirect offset, before it is returned as the actual offset.
	 *
	 * @param operator        The operator defining the operation. A null value will be treated as invalid.
	 * @param operand         The Operand, that shall be applied through the operation.
	 * @param operandIndirect True, if the operand is defined indirect and shall be read from compared data.
	 * @param operandReadType Determines the endianness, byte length and read order of the indirect operand that shall
	 *                        be extracted. A null value will be valid for constant operands.
	 * @throws MagicPatternException Invalid parameters shall cause this.
	 */
	public MagicOffsetModification(MagicOperator operator, long operand, boolean operandIndirect,
			MagicOffsetReadType operandReadType) throws MagicPatternException {
		if (operator == null || (operandIndirect && operandReadType == null)) {
			throw new MagicPatternException("Invalid offset operation initialization.");
		}
		this.operator = operator;
		this.operand = operand;
		this.operandIndirect = operandIndirect;
		this.operandReadType = operandReadType;
	}

	/**
	 * Returns the operator defining the operation.
	 *
	 * @return The operator defining the operation. (Must never return null.)
	 */
	public MagicOperator getOperator() {
		return operator;
	}

	/**
	 * Returns the Operand, that shall be applied through the operation.
	 *
	 * @return The Operand, that shall be applied through the operation.
	 */
	public long getOperand() {
		return operand;
	}

	/**
	 * Returns true, if the operand is defined indirect and shall be read from compared data.
	 *
	 * @return True, if the operand is defined indirect and shall be read from compared data.
	 */
	public boolean isOperandIndirect() {
		return operandIndirect;
	}

	/**
	 * Returns the {@link MagicOffsetReadType}, that determines the endianness, byte length and read order of the indirect
	 * operand that shall be extracted.
	 *
	 * @return The {@link MagicOffsetReadType}, that determines the endianness, byte length and read order of the indirect
	 * operand that shall be extracted. (Returns null for constant operands.)
	 */
	public MagicOffsetReadType getOperandReadType() {
		return operandReadType;
	}

	/**
	 * Modifies the given actual offset value, by applying the hereby called operation.
	 *
	 * @param data                The data an read offset shall be found for.
	 * @param indirectStartOffset The start offset of the wrapping {@link MagicIndirectOffset}.
	 * @param actualOffset        The actual offset, that has been read by the wrapping {@link MagicIndirectOffset}.
	 * @return The determined offset, that should be read next, to extract a value for the evaluation of the current
	 * {@link MagicPattern}.
	 * @throws MagicPatternException Shall be thrown for negative offsets.
	 */
	public long applyModificationToOffset(byte[] data, long indirectStartOffset, long actualOffset)
			throws MagicPatternException {
		long modifiedOffset = actualOffset;
		// Apply modification operation, if necessary.
		if (getOperator() != null) {
			long operand = getOperand();
			// If the operand itself is indirect collect the operand from data.
			if (isOperandIndirect() && getOperandReadType() != null) {
				// Read the real operand from data. It's offset is always relative to the main indirect offset.
				operand = PatternUtils.readIndirectOffset(
						data,
						indirectStartOffset + operand,
						getOperandReadType()
				);
			}

			// Apply the modification operation to the read value.
			switch (getOperator()) {
				case ADD:
					modifiedOffset += operand;
					break;
				case SUBTRACT:
					modifiedOffset -= operand;
					break;
				case MULTIPLY:
					modifiedOffset *= operand;
					break;
				case DIVIDE:
					modifiedOffset /= operand;
					break;
				case MODULO:
					modifiedOffset %= operand;
					break;
				case CONJUNCTION:
					modifiedOffset &= operand;
					break;
				case DISJUNCTION:
					modifiedOffset |= operand;
					break;
				case CONTRAVALENCE:
					modifiedOffset ^= operand;
					break;
				default:
			}
		}

		return modifiedOffset;
	}

	/**
	 * Parse the given raw definition to initialize a {@link MagicOffsetModification} instance.
	 *
	 * @param rawDefinition The raw definition of the {@link MagicOffsetModification} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	public static MagicOffsetModification parse(String rawDefinition) throws MagicPatternException {
		if (rawDefinition == null || rawDefinition.isEmpty()) {
			return null;
		}
		Matcher matcher = OFFSET_MODIFICATION_PATTERN.matcher(rawDefinition);
		if (!matcher.matches()) {
			throw new MagicPatternException(
					String.format("Invalid/unknown offset modification pattern: '%s'", rawDefinition)
			);
		}

		//Determine the offset modification operator.
		MagicOperator operator = MagicOperator.forPattern(matcher.group(1), OFFSET_MODIFIERS);
		if (operator == null) {
			throw new MagicPatternException(
					String.format("Invalid/unknown operator for modification pattern: '%s'", rawDefinition)
			);
		}

		// When enclosed in '(' and ')' this operand is indirect and shall be read from data.
		// The read offset of the operand, is always relative to the start offset of the containing indirect offset.
		boolean operandIndirect = matcher.group(4) != null && matcher.group(8) != null;

		// Read the operand.
		long operand;
		MagicOffsetReadType operandReadType;
		try {
			if (operandIndirect) {
				operand = Long.decode((matcher.group(5) != null ? matcher.group(5) : matcher.group(6)));
			} else {
				operand = Long.decode((matcher.group(2) != null ? matcher.group(2) : matcher.group(3)));
			}
			// Determine endianness, byte length and read order of an indirect operand.
			operandReadType = MagicOffsetReadType.parse(matcher.group(7));
		} catch (NumberFormatException ex) {
			throw new MagicPatternException(
					String.format("Invalid/unknown offset modification operand: '%s'", rawDefinition)
			);
		}

		// Construct and return the new instance.
		return new MagicOffsetModification(operator, operand, operandIndirect, operandReadType);
	}
}
