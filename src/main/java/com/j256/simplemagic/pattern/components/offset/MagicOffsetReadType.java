package com.j256.simplemagic.pattern.components.offset;

import com.j256.simplemagic.endian.*;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.components.MagicOffset;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;

/**
 * <b>An instance of this class represents The endianness and value type as defined by an indirect offset in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * Indirect offsets are of the form: (( x [[.,][bBcCeEfFgGhHiIlmsSqQ]][+-][ y ]). The value of x is used as an offset
 * in the file. A byte, id3 length, short or long is read at that offset depending on the [bBcCeEfFgGhHiIlmsSqQ] type
 * specifier. The value is treated as signed if “”, is specified or unsigned if “”. is specified. The capitalized types
 * interpret the number as a big endian value, whereas the small letter versions interpret the number as a little endian
 * value; the m type interprets the number as a middle endian (PDP-11) value.
 * </i>
 * </p>
 * <p>
 * <i>[...] The default type if one is not specified is long.</i>
 * </p>
 * <p>
 * Attention: The "long" type defined here, has a length of 4 bytes. An 8 byte value is named "quad".
 * </p>
 */
public class MagicOffsetReadType {

	private final EndianType endianType;
	private final int valueByteLength;
	private final boolean readID3Length;

	/**
	 * Creates a new {@link MagicOffsetReadType} as found in a {@link MagicOffset}. The endian type shall influence the
	 * assumed endianness, read order and byte length of a value, that is read from binary data.
	 *
	 * @param endianType      The {@link EndianType} of data, that shall be read. A 'null' value will be treated as invalid.
	 * @param valueByteLength The byte length of data, that shall be read. A negative value will be treated as invalid.
	 * @param readID3Length   Whether the binary data are stored in ID3 order.
	 * @throws MagicPatternException Invalid parameters shall cause this.
	 */
	public MagicOffsetReadType(EndianType endianType, int valueByteLength, boolean readID3Length) throws MagicPatternException {
		if (endianType == null || valueByteLength < 0) {
			throw new MagicPatternException("Invalid magic endian type initialization.");
		}
		this.endianType = endianType;
		this.valueByteLength = valueByteLength;
		this.readID3Length = readID3Length;
	}

	/**
	 * Returns the {@link EndianType}, that shall be used to read compared byte values.
	 *
	 * @return The {@link EndianType}, that shall be used to read compared byte values.
	 */
	@SuppressWarnings("unused")
	public EndianType getEndianType() {
		return endianType;
	}

	/**
	 * Returns the number of bytes, that shall be read to match a {@link MagicCriterion}.
	 *
	 * @return The number of bytes, that shall be read to match a {@link MagicCriterion}.
	 */
	public int getValueByteLength() {
		return valueByteLength;
	}

	/**
	 * Returns true, if the bytes shall be read in ID3 order.
	 *
	 * @return True, if the bytes shall be read in ID3 order.
	 */
	public boolean isReadID3Length() {
		return readID3Length;
	}


	/**
	 * Returns a matching endian converter, that shall be used to read raw binary data.
	 *
	 * @return A matching endian converter.
	 */
	public EndianConverter getEndianConverter() {
		return EndianConverterFactory.createEndianConverter(this.endianType);
	}

	/**
	 * Parse the given raw definition to initialize the {@link MagicOffsetReadType} instance.
	 *
	 * @param rawDefinition The raw definition of this {@link MagicOffsetReadType} as a String.
	 */
	public static MagicOffsetReadType parse(String rawDefinition) throws MagicPatternException {
		char endianType = '\0';
		if (rawDefinition != null && rawDefinition.length() == 1) {
			endianType = rawDefinition.charAt(0);
		}
		EndianType magicEndianType = EndianType.forName(endianType);
		boolean readID3Length;
		int valueByteLength;

		switch (endianType) {
			// byte
			case 'b':
			case 'B':
			case 'c':
			case 'C':
				readID3Length = false;
				valueByteLength = 1;
				break;
			// short
			case 's':
			case 'S':
			case 'h':
			case 'H':
				readID3Length = false;
				valueByteLength = 2;
				break;
			// id3 integer - 4 bytes
			case 'i':
			case 'I':
				readID3Length = true;
				valueByteLength = 4;
				break;
			// quad/double - 8 bytes
			case 'e':
			case 'E':
			case 'f':
			case 'F':
			case 'g':
			case 'G':
			case 'q':
			case 'Q':
				readID3Length = false;
				valueByteLength = 8;
				break;
			// integer - 4 bytes ("long" types when the spec was written)
			case 'm':
			case 'L':
			case 'l':
			default:
				readID3Length = false;
				valueByteLength = 4;
				break;
		}

		return new MagicOffsetReadType(magicEndianType, valueByteLength, readID3Length);
	}
}
