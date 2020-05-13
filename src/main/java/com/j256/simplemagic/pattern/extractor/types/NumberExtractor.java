package com.j256.simplemagic.pattern.extractor.types;

import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.pattern.extractor.MagicExtractor;

/**
 * Extracts numbers from binary data.
 */
public class NumberExtractor implements MagicExtractor<Number> {

	private final EndianType endianness;
	private final int byteLength;

	/**
	 * An extractor, that extracts numbers from binary data.
	 *
	 * @param endianness The endianness of the data, that shall be read.
	 * @param byteLength The number of bytes, that shall be extracted.
	 */
	public NumberExtractor(EndianType endianness, int byteLength) {
		this.endianness = endianness;
		this.byteLength = byteLength;
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
	 * @return The byte length of the value, that shall be read.
	 */
	public int getByteLength() {
		return byteLength;
	}

	/**
	 * Extracts a number from the given data and offset.
	 *
	 * @param data              The binary data a value shall be extracted from.
	 * @param currentReadOffset The offset the value shall be read from.
	 * @return The number, that has been extracted, or null if the extraction failed.
	 */
	@Override
	public Number extractValue(byte[] data, int currentReadOffset) {
		return EndianConverterFactory.createEndianConverter(getEndianness())
				.convertNumber(data, currentReadOffset, getByteLength());
	}
}
