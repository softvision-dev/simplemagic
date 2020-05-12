package com.j256.simplemagic.pattern.components;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.offset.MagicIndirectOffset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>An instance of this class represents an offset definition from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * Some file formats contain additional information which is to be printed along with the file type or need additional
 * tests to determine the true file type. These additional tests are introduced by one or more > characters preceding
 * the offset.
 * </i>
 * </p>
 * <p>
 * <i>
 * [...] Offsets do not need to be constant, but can also be read from the file being examined. If the first character
 * following the last > is a ( then the string after the parenthesis is interpreted as an indirect offset. That means
 * that the number after the parenthesis is used as an offset in the file. The value at that offset is read, and is
 * used again as an offset in the file. Indirect offsets are of the form: (( x [.[bislBISL]][+-][ y ]). The value of x
 * is used as an offset in the file. A byte, id3 length, short or long is read at that offset depending on the
 * [bislBISLm] type specifier. The capitalized types interpret the number as a big endian value, whereas the small
 * letter versions interpret the number as a little endian value; the m type interprets the number as a middle
 * endian (PDP-11) value. To that number the value of y is added and the result is used as an offset in the file.
 * The default type if one is not specified is long.
 * </p>
 * </i>
 * <p>
 * Attention: The actual form of an indirect offset is: (( x[.[bislBISLm]][+-*%/&|^][ y ])
 * </p>
 */
public class MagicOffset {

	private static final Pattern BASE_OFFSET_PATTERN = Pattern.compile("^(&)?(?:(?:\\((.+)\\))|(.+))$");

	private final long baseOffset;
	private final boolean relative;
	private final MagicIndirectOffset indirectOffset;

	/**
	 * Creates a new {@link MagicOffset} as found in a {@link MagicPattern}. Represents the offset for the extraction of
	 * compared values from compared data.
	 * <p>
	 * The binary data must contain a matching value at the hereby defined position, to match the pattern.
	 * </p>
	 *
	 * @param baseOffset     The simple offset, that defines a constant offset in the compared data. Will be ignored,
	 *                       if an indirect offset has been defined.
	 * @param relative       Set to true, if the Offset is defined relative to a current read offset in the compared data.
	 * @param indirectOffset When set, this offset is defined as an {@link MagicIndirectOffset} and shall be read from
	 *                       the compared data. When set, the baseOffset is ignored. Set to null for constant offsets.
	 */
	public MagicOffset(long baseOffset, boolean relative, MagicIndirectOffset indirectOffset) {
		this.baseOffset = baseOffset;
		this.relative = relative;
		this.indirectOffset = indirectOffset;
	}

	/**
	 * Returns the simple constant offset, that defines a read position in compared data. This value shall be ignored if
	 * this offset is defined to contain a {@link MagicIndirectOffset}.
	 *
	 * @return The simple constant offset, that defines a read position in compated data.
	 */
	public long getBaseOffset() {
		return baseOffset;
	}

	/**
	 * Returns true, if this offset is relative to a current read offset in the compared data.
	 *
	 * @return True, if this offset is relative to a current read offset in the compared data.
	 */
	public boolean isRelative() {
		return relative;
	}

	/**
	 * Returns true, if this offset is defined as an indirect offset, that shall be read from compared data.
	 *
	 * @return True if this offset is defined as an indirect offset.
	 */
	public boolean isIndirect() {
		return indirectOffset != null;
	}

	/**
	 * Returns the {@link MagicIndirectOffset}, if this offset is defined as an indirect offset and shall be read from
	 * the compared data. When set, the baseOffset is ignored. Will return null, if this is offset is not indirect.
	 *
	 * @return The {@link MagicIndirectOffset}, if this offset is defined as an indirect offset. Null if it is not.
	 */
	public MagicIndirectOffset getIndirectOffset() {
		return indirectOffset;
	}

	/**
	 * Determines the offset of a compared value, that shall be read from the given data.
	 *
	 * @param data              The data a read offset shall be found for.
	 * @param currentReadOffset The current read offset in the compared data.
	 * @return The determined offset, that should be read next, to extract a value for the evaluation of the current
	 * {@link MagicPattern}.
	 * @throws MagicPatternException Shall be thrown for negative indirect offsets.
	 */
	public long getReadOffset(byte[] data, int currentReadOffset) throws MagicPatternException {
		long offset = getBaseOffset();
		// If it is an indirect offset, read the value from data.
		if (isIndirect()) {
			offset = getIndirectOffset().getReadOffset(data, currentReadOffset);
		}

		// If it is a relative offset, apply the current read offset.
		if (isRelative()) {
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
		String processedString;
		if (rawDefinition == null || (processedString = rawDefinition.replaceAll("\\s", "")).isEmpty()) {
			throw new MagicPatternException("Magic pattern offset is empty.");
		}
		Matcher matcher = BASE_OFFSET_PATTERN.matcher(processedString);
		if (!matcher.matches()) {
			throw new MagicPatternException(String.format("Invalid/unknown offset pattern: '%s'", rawDefinition));
		}

		// check for relativization and indirect offset.
		boolean relative = matcher.group(1) != null;
		boolean indirect = matcher.group(2) != null;
		processedString = indirect ? matcher.group(2) : matcher.group(3);
		if (processedString == null) {
			throw new MagicPatternException(String.format("Invalid/unknown offset pattern: '%s'", rawDefinition));
		}

		// Resolve indirect offset.
		long baseOffset = 0;
		MagicIndirectOffset indirectOffset = null;
		if (indirect) {
			indirectOffset = MagicIndirectOffset.parse(processedString);
		}
		// Resolve simple constant offset.
		else {
			try {
				baseOffset = Long.decode(processedString);
			} catch (NumberFormatException ex) {
				throw new MagicPatternException(String.format("Invalid/unknown offset pattern: '%s'", rawDefinition));
			}
		}

		return new MagicOffset(baseOffset, relative, indirectOffset);
	}
}
