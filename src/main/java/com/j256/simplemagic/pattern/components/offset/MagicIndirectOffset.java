package com.j256.simplemagic.pattern.components.offset;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.MagicOffset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>An instance of this class represents an indirect offset definition from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * Offsets do not need to be constant, but can also be read from the file being examined. If the first character
 * following the last > is a ( then the string after the parenthesis is interpreted as an indirect offset. That
 * means that the number after the parenthesis is used as an offset in the file. The value at that offset is read,
 * and is used again as an offset in the file. Indirect offsets are of the form: (( x [.[bislBISL]][+-][ y ]).
 * The value of x is used as an offset in the file. A byte, id3 length, short or long is read at that offset
 * depending on the [bislBISLm] type specifier. The capitalized types interpret the number as a big endian value,
 * whereas the small letter versions interpret the number as a little endian value; the m type interprets the
 * number as a middle endian (PDP-11) value. To that number the value of y is added and the result is used as an
 * offset in the file. The default type if one is not specified is long.
 * </i>
 * </p>
 * <p>
 * <i>
 * [...] If this indirect offset cannot be used directly, simple calculations are possible: appending [+-*%/&|^] number
 * inside parentheses allows one to modify the value read from the file before it is used as an offset:
 * </i>
 * </p>
 * <p>
 * <i>
 * [...] Indirect and relative offsets can be combined.
 * </i>
 * </p>
 * <p>
 * Possible combinations of indirect and relative offset definitions are:
 * <ul>
 * <li>&x - offset is relative to parent.</li>
 * <li>(x) - offset is indirect and shall be read from the compared data.</li>
 * <li>&(x) - offset is relative to parent and indirect.</li>
 * <li>(&x) - offset is indirect, indirect offset is relative to parent.</li>
 * <li>&(&x) - offset is relative to parent and indirect, indirect offset is also relative to parent.</li>
 * </ul>
 * </p>
 * <p>
 * Attention: The actual form of an indirect offset is: (( x[.[bislBISLm]][+-*%/&|^][ y ])
 * </p>
 */
public class MagicIndirectOffset {

	private static final Pattern INDIRECT_OFFSET_PATTERN = Pattern.compile(
			// Find inner relativization marker: optional
			"^(&)?" +
					// Find base offset (always a numeric value): must exist
					"(?:(-?[0-9a-fA-FxX]+)|(-?\\d+))" +
					// Find offset type and endianness: optional, default: ".l"
					"(?:.([bislBISLm]))?" +
					// Find offset modification operation: optional
					"(.+)?$");

	private final boolean relative;
	private final MagicOffsetReadType offsetReadType;
	private final MagicOffsetModification offsetModification;
	private final long offset;

	/**
	 * Creates a new {@link MagicIndirectOffset} as found in a {@link MagicOffset}. The real offset shall be read
	 * dynamically from the compared data: The hereby defined indirect offset is serving as an offset to read a
	 * value, that shall serve as the actual offset.
	 *
	 * @param offset     The indirect offset, that shall be read.
	 * @param relative           True if the indirect offset is relative to a current read offset.
	 * @param offsetReadType     Determines the endianness, byte length and read order of the value that shall be extracted
	 *                           as the actual offset. A null value will be treated as invalid.
	 * @param offsetModification The mathematical operation, that shall be applied to the offset, after it has been
	 *                           read. May be set to null, if such a modification is not defined.
	 * @throws MagicPatternException Invalid parameters shall cause this.
	 */
	public MagicIndirectOffset(long offset, boolean relative, MagicOffsetReadType offsetReadType,
			MagicOffsetModification offsetModification) throws MagicPatternException {
		if (offsetReadType == null) {
			throw new MagicPatternException("Invalid magic offset initialization.");
		}
		this.offset = offset;
		this.relative = relative;
		this.offsetReadType = offsetReadType;
		this.offsetModification = offsetModification;
	}

	/**
	 * Returns the indirect offset, that determines the position of the value, that shall be used as the actual offset,
	 * after having been read from compared data.
	 *
	 * @return The indirect offset, that determines the position of the actual offset value.
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Returns true, if the indirect offset shall be relative to the current read position.
	 *
	 * @return True, if the indirect offset shall be relative to the current read position.
	 */
	public boolean isRelative() {
		return relative;
	}

	/**
	 * Returns the {@link MagicOffsetReadType} listing the endianness, byte length and read order of the value that shall be
	 * extracted and used as the actual offset.
	 *
	 * @return Returns the {@link MagicOffsetReadType} of the value, that shall be extracted as the actual read offset.
	 * (Must never return null.)
	 */
	public MagicOffsetReadType getOffsetReadType() {
		return offsetReadType;
	}

	/**
	 * Returns the mathematical operation, that shall be applied to the actual offset, after it has been read.
	 *
	 * @return The mathematical operation, that shall be applied to the actual offset, after it has been read.
	 */
	public MagicOffsetModification getOffsetModification() {
		return offsetModification;
	}

	/**
	 * Determines the offset of a compared value, that shall be read from the given data.
	 *
	 * @param data              The data an read offset shall be found for.
	 * @param currentReadOffset The current read offset in the compared data.
	 * @return The determined offset, that should be read next, to extract a value for the evaluation of the current
	 * {@link MagicPattern}.
	 * @throws MagicPatternException Shall be thrown for negative offsets.
	 */
	public long getReadOffset(byte[] data, int currentReadOffset) throws MagicPatternException {
		// Determine indirect offset (relative or constant).
		long indirectStartOffset = isRelative() ?
				getOffset() + currentReadOffset :
				getOffset();
		// Read actual offset value from data.
		long actualOffset = PatternUtils.readIndirectOffset(data, indirectStartOffset, getOffsetReadType());
		// apply modification
		if (getOffsetModification() != null) {
			actualOffset = getOffsetModification().applyModificationToOffset(data, indirectStartOffset, actualOffset);
		}

		// Return the resolved offset.
		return actualOffset;
	}

	/**
	 * Parse the given raw definition to initialize a {@link MagicIndirectOffset} instance.
	 *
	 * @param rawDefinition The raw definition of the {@link MagicIndirectOffset} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	public static MagicIndirectOffset parse(String rawDefinition) throws MagicPatternException {
		if (rawDefinition == null || rawDefinition.isEmpty()) {
			throw new MagicPatternException("unexpected empty offset operand pattern.");
		}
		Matcher matcher = INDIRECT_OFFSET_PATTERN.matcher(rawDefinition);
		if (!matcher.matches()) {
			throw new MagicPatternException(String.format("Invalid/unknown indirect offset pattern: '%s'", rawDefinition));
		}

		// When starting with '&', it is relative to the current read offset.
		boolean relative = (matcher.group(1) != null);

		// Determine the indirect offset, that shall be read from a data array.
		long indirectOffset;
		try {
			indirectOffset = Long.decode((matcher.group(2) != null ? matcher.group(2) : matcher.group(3)));
		} catch (NumberFormatException ex) {
			throw new MagicPatternException(String.format("Invalid/unknown indirect offset value: '%s'",
					(matcher.group(2) != null ? matcher.group(2) : matcher.group(3))));
		}

		// Determine endianness, byte length and read order of the indirect offset.
		MagicOffsetReadType indirectOffsetReadType = MagicOffsetReadType.parse(matcher.group(4));

		// Determine the optional modification operation.
		MagicOffsetModification offsetModificationOperation = null;
		if (matcher.group(5) != null) {
			offsetModificationOperation = MagicOffsetModification.parse(matcher.group(5));
		}

		// Construct and return the new instance.
		return new MagicIndirectOffset(indirectOffset, relative, indirectOffsetReadType, offsetModificationOperation);
	}
}
