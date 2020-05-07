package com.j256.simplemagic.pattern.components.extractor.types;

import com.j256.simplemagic.pattern.components.extractor.MagicExtractor;

/**
 * Extracts a Pascal-style string where the first byte is interpreted as the an unsigned length from binary data.
 * The string is not '\0' terminated.
 */
public class PascalStringExtractor implements MagicExtractor<String> {

	/**
	 * An extractor, that extracts Pascal Strings from binary data.
	 */
	public PascalStringExtractor() {
	}

	/**
	 * Returns the number of bytes, that shall be read.
	 *
	 * @return The number of bytes, that shall be read. (Always returning -1, as the length is not predictable)
	 */
	@Override
	public int getByteLength() {
		return -1;
	}

	/**
	 * Extracts a Pascal style String from the given data and offset.
	 *
	 * @param data              The binary data a value shall be extracted from.
	 * @param currentReadOffset The offset the value shall be read from.
	 * @return The String, that has been extracted, or null if the extraction failed.
	 */
	@Override
	public String extractValue(byte[] data, int currentReadOffset) {
		if (currentReadOffset >= data.length) {
			return null;
		}
		// length is from the first byte of the string
		int len = (data[currentReadOffset] & 0xFF);
		int left = data.length - currentReadOffset - 1;
		if (len > left) {
			len = left;
		}
		char[] chars = new char[len];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) (data[currentReadOffset + 1 + i] & 0xFF);
		}
		/*
		 * NOTE: we need to make a new string because it might be returned if we don't match below.
		 */
		return new String(chars);
	}
}
