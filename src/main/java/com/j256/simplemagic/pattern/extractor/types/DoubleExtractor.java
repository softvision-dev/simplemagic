package com.j256.simplemagic.pattern.extractor.types;

import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;

/**
 * Extracts double values from binary data.
 */
public class DoubleExtractor extends NumberExtractor {

	/**
	 * An extractor, that extracts double values from binary data.
	 *
	 * @param endianness The endianness of the read data.
	 * @param byteLength The number of bytes, that shall be read.
	 */
	public DoubleExtractor(EndianType endianness, int byteLength) {
		super(endianness, byteLength);
	}

	/**
	 * Extracts a double from the given data and offset.
	 *
	 * @param data              The binary data a value shall be extracted from.
	 * @param currentReadOffset The offset the value shall be read from.
	 * @return The double, that has been extracted, or null if the extraction failed.
	 */
	@Override
	public Number extractValue(byte[] data, int currentReadOffset) {
		Long longValue = EndianConverterFactory.createEndianConverter(getEndianness())
				.convertNumber(data, currentReadOffset, getByteLength());
		return longValue == null ? null : Double.longBitsToDouble(longValue);
	}
}
