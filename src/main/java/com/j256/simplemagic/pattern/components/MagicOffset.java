package com.j256.simplemagic.pattern.components;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The offset definition from a line in magic (5) format.
 */
public class MagicOffset {

	private static final Pattern OFFSET_PATTERN =
			Pattern.compile("\\(([0-9a-fA-Fx]+)\\.?([bsilBSILm]?)([*+\\-]?)([0-9a-fA-Fx]*)\\)");

	private final int offset;
	private final int incrementalOffset;
	private final boolean addOffset;
	private final MagicEndianType endianType;

	/**
	 * Creates a new {@link MagicOffset} as found in a {@link MagicPattern}. The type shall influence the offset for the
	 * extraction of compared values from compared data.
	 * <p>
	 * The binary data must contain a matching value at the hereby defined position, to match the pattern.
	 * </p>
	 *
	 * @param offset            The offset from which a compared value shall be extracted. The binary data must contain a
	 *                          matching value at that position to match the pattern.
	 * @param incrementalOffset This value shall be added to the defined offset as an increment.
	 * @param addOffset         When set to true, the hereby defined offset shall not indicate a fixed position in the
	 *                          binary data, but shall rather be added on top of a current read offset.
	 * @param endianType        Determines the endianness of the data values that shall be extracted
	 *                          {@link MagicEndianType}. This may be set to 'null', if such a definition is not given
	 *                          by the pattern.
	 */
	public MagicOffset(int offset, int incrementalOffset, boolean addOffset, MagicEndianType endianType) {
		this.offset = offset;
		this.incrementalOffset = incrementalOffset;
		this.addOffset = addOffset;
		this.endianType = endianType;
	}

	/**
	 * Returns the byte offset from which bytes shall be read to evaluate a criterion.
	 *
	 * @return The byte offset.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns the incremental offset that shall be added to the byte offset.
	 *
	 * @return The incremental offset value.
	 */
	public int getIncrementalOffset() {
		return incrementalOffset;
	}

	/**
	 * Returns true, if the offset is not an absolute value, and shall be determined relative to a current read offset.
	 *
	 * @return True, if the offset is not an absolute value, and shall be determined relative to a current read offset.
	 */
	public boolean isAddOffset() {
		return addOffset;
	}

	/**
	 * Returns the endianness of the value, that shall be extracted and evaluated.
	 *
	 * @return The endianness of the value, that shall be extracted and evaluated.
	 */
	public MagicEndianType getEndianType() {
		return endianType;
	}

	/**
	 * Determines the offset of a compared value, that shall be read from the given data.
	 * <p>
	 * According to the magic (5) pattern, this will automatically calculate an offset relative to the current read
	 * offset if required, or an absolute value otherwise.
	 * It will also adapt the endianness of such offsets, as defined by the pattern and will lastly add all defined
	 * offset increments.
	 * </p>
	 *
	 * @param data              The data an read offset shall be found for.
	 * @param currentReadOffset The current read offset in the given data.
	 * @return The determined offset, that should be read next, to extract a value for the evaluation of the current
	 * {@link MagicPattern}.
	 */
	public int getReadOffset(byte[] data, int currentReadOffset) {
		int offset = getOffset();
		if (getEndianType() != null) {
			Long longOffset;
			if (getEndianType().isReadID3Length()) {
				longOffset = getEndianType().getEndianConverter().convertId3(
						data, getOffset(), getEndianType().getValueByteLength()
				);
			} else {
				longOffset = getEndianType().getEndianConverter().convertNumber(
						data, getOffset(), getEndianType().getValueByteLength()
				);
			}
			if (longOffset != null) {
				offset = (int) (longOffset + getIncrementalOffset());
			}
		}

		if (isAddOffset()) {
			offset += currentReadOffset;
		}
		return offset;
	}

	/**
	 * Parse the given raw definition to initialize a {@link MagicOffset} instance.
	 *
	 * @param rawDefinition The raw definition of the {@link MagicOffset} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	public static MagicOffset parse(String rawDefinition) throws MagicPatternException {
		// Filter invalid offset patterns.
		if (rawDefinition == null || rawDefinition.isEmpty()) {
			throw new MagicPatternException("Magic pattern offset is empty.");
		}

		boolean addOffset = false;
		String offsetString = rawDefinition;
		// Test for leading '&' add-offset operator.
		if (offsetString.charAt(0) == '&') {
			if (offsetString.length() == 1) {
				throw new MagicPatternException(String.format("Invalid/unknown offset pattern: '%s'", offsetString));
			}
			addOffset = true;
			offsetString = offsetString.substring(1);
		}

		int offset;
		int incrementalOffset = 0;
		MagicEndianType endianType = null;
		// Parse complex offset pattern.
		if (offsetString.charAt(0) == '(') {
			// Filter invalid offset patterns.
			Matcher matcher = OFFSET_PATTERN.matcher(offsetString);
			if (!matcher.matches()) {
				throw new MagicPatternException(String.format("Invalid/unknown offset pattern: '%s'", offsetString));
			}
			if (matcher.group(2) == null) {
				throw new MagicPatternException(String.format("Invalid offset type: %s", offsetString));
			}

			// Determine base offset value.
			try {
				offset = Integer.decode(matcher.group(1));
			} catch (NumberFormatException ex) {
				throw new MagicPatternException(String.format("Invalid offset number: %s", offsetString), ex);
			}

			// Determine endian type.
			endianType = MagicEndianType.parse(matcher.group(2));

			// Evaluate the optional offset operator.
			if (matcher.group(4) != null && matcher.group(4).length() > 0) {
				try {
					/*
					 * From manual: The + offset operator specifies an incremental offset, based upon the value of the last
					 * offset. Thus, +15 indicates that the offset value is 15 bytes from the last specified offset.
					 *
					 * From manual: An offset operator of the form (l+R) specifies an offset that is the total of the value
					 * of memory location specified by l and the value R.
					 */
					incrementalOffset = Integer.decode(matcher.group(4));
				} catch (NumberFormatException ex) {
					throw new MagicPatternException(String.format("invalid long add value: %s", matcher.group(4)), ex);
				}
				String offsetOperator = matcher.group(3);
				if ("-".equals(offsetOperator)) {
					/*
					 * From manual: An offset operator of the form (l-R) specifies an offset that is calculated by
					 * subtracting the value R from the value of memory location specified by l.
					 */
					incrementalOffset = -incrementalOffset;
				} else if ("*".equals(offsetOperator)) {
					/*
					 * From manual: '*' offset operator specifies that the value located at the memory location following
					 * the operator be used as the offset. Thus, *0x3C indicates that the value contained in 0x3C should be
					 * used as the offset.
					 */
					offset = incrementalOffset;
					incrementalOffset = 0;
				}
			}
		} else {
			// Parse simple offset.
			try {
				offset = Integer.decode(offsetString);
			} catch (NumberFormatException e) {
				throw new MagicPatternException(String.format("Invalid/unknown offset pattern: '%s'", offsetString));
			}
		}

		return new MagicOffset(offset, incrementalOffset, addOffset, endianType);
	}
}
