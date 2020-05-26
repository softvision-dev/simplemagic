package com.j256.simplemagic.pattern.extractor.types;

import com.j256.simplemagic.pattern.extractor.MagicExtractor;

/**
 * Default no-op Extractor, always 'extracts' an empty String.
 */
public class DefaultExtractor implements MagicExtractor<String> {

	/**
	 * Creates the default no-op Extractor, that always 'extracts' an empty String.
	 */
	public DefaultExtractor() {
	}

	/**
	 * Returns the byte length of the value, that shall be read.
	 *
	 * @return The byte length of the value, that shall be read. (Always returns -1 as no bytes are actually read.)
	 */
	@Override
	public int getByteLength() {
		return -1;
	}

	/**
	 * The default extractor always returns an empty String.
	 *
	 * @param data              The binary data a value shall be extracted from.
	 * @param currentReadOffset The offset the value shall be read from.
	 * @param invertEndianness  True, if the preset Endianness shall be inverted for this extraction.
	 * @return The default extractor always return an empty String.
	 */
	@Override
	public String extractValue(byte[] data, int currentReadOffset, boolean invertEndianness) {
		return "";
	}
}
