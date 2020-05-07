package com.j256.simplemagic.pattern.components.extractor.types;

import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;

/**
 * Extracts float values from binary data.
 */
public class FloatExtractor extends NumberExtractor {

	/**
	 * An extractor, that extracts float values from binary data.
	 *
	 * @param endianness The endianness of the read data.
	 * @param byteLength The number of bytes, that shall be read.
	 */
	public FloatExtractor(EndianType endianness, int byteLength) {
		super(endianness, byteLength);
	}

	/**
	 * Extracts a float from the given data and offset.
	 *
	 * @param data              The binary data a value shall be extracted from.
	 * @param currentReadOffset The offset the value shall be read from.
	 * @return The float, that has been extracted, or null if the extraction failed.
	 */
	@Override
	public Number extractValue(byte[] data, int currentReadOffset) {
		Long longValue = EndianConverterFactory.createEndianConverter(getEndianness())
				.convertNumber(data, currentReadOffset, getByteLength());
		return longValue == null ? null : Float.intBitsToFloat(longValue.intValue());
	}
}
