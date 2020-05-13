package com.j256.simplemagic.pattern.extractor.types;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.pattern.extractor.MagicExtractor;

/**
 * Extracts a two byte String (16 Bits) from binary data.
 */
public class String16Extractor implements MagicExtractor<char[]> {

	private final EndianType endianness;

	/**
	 * An extractor, that extracts 2 byte Strings from binary data.
	 *
	 * @param endianness The endianness of the data, that shall be read.
	 */
	public String16Extractor(EndianType endianness) {
		this.endianness = endianness;
	}

	/**
	 * Returns the endianness of the data, that shall be read.
	 *
	 * @return The endianness of the data, that shall be read.
	 */
	public EndianType getEndianness() {
		return endianness;
	}

	/**
	 * Returns the byte length of the value, that shall be read.
	 *
	 * @return The byte length of the value, that shall be read. (Always reads 2 bytes)
	 */
	@Override
	public int getByteLength() {
		return 2;
	}

	/**
	 * Extracts a two byte String from the given data and offset.
	 *
	 * @param data              The binary data a value shall be extracted from.
	 * @param currentReadOffset The offset the value shall be read from.
	 * @return The String, that has been extracted, or null if the extraction failed.
	 */
	@Override
	public char[] extractValue(byte[] data, int currentReadOffset) {
		int len;
		// find the 2 '\0' chars, we do the -1 to make sure we don't have odd number of bytes
		for (len = currentReadOffset; len < data.length - 1; len += 2) {
			if (data[len] == 0 && data[len + 1] == 0) {
				break;
			}
		}
		char[] chars = new char[len / 2];
		for (int i = 0; i < chars.length; i++) {
			int firstByte = data[i * 2];
			int secondByte = data[i * 2 + 1];
			if (getEndianness() == EndianType.BIG) {
				chars[i] = (char) ((firstByte << 8) + secondByte);
			} else {
				chars[i] = (char) ((secondByte << 8) + firstByte);
			}
		}

		return chars;
	}
}
