package com.j256.simplemagic.pattern.extractor;

/**
 * An implementing class shall provide the means to extract a value of a specific length and type from binary data.
 *
 * @param <T> The type of value, that shall be extracted.
 */
public interface MagicExtractor<T> {

	/**
	 * Extracts a value of type <T> beginning at the given offset from the given binary data.
	 *
	 * @param data              The binary data a value shall be extracted from.
	 * @param currentReadOffset The offset the value shall be read from.
	 * @param invertEndianness True, if the preset Endianness shall be inverted for this extraction.
	 * @return The extracted value, or null if the extraction failed.
	 */
	T extractValue(byte[] data, int currentReadOffset, boolean invertEndianness);

	/**
	 * Returns the byte length of the value, that shall be read.
	 *
	 * @return The byte length of the value, that shall be read. (May return -1, if the read length is not predictable.)
	 */
	int getByteLength();
}
