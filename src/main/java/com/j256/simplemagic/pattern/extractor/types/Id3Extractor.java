package com.j256.simplemagic.pattern.extractor.types;

import com.j256.simplemagic.endian.EndianConverterFactory;
import com.j256.simplemagic.endian.EndianType;

/**
 * Extracts integers in ID3 order from binary data.
 */
public class Id3Extractor extends NumberExtractor {

	/**
	 * An extractor, that extracts ID3 integer values from binary data.
	 *
	 * @param endianness The endianness of the read data.
	 * @param byteLength The number of bytes, that shall be read.
	 */
	public Id3Extractor(EndianType endianness, int byteLength) {
		super(endianness, byteLength);
	}

	/**
	 * Extracts an ID3 integer from the given data and offset.
	 *
	 * @param data              The binary data a value shall be extracted from.
	 * @param currentReadOffset The offset the value shall be read from.
	 * @return The ID3 integer, that has been extracted, or null if the extraction failed.
	 */
	@Override
	public Number extractValue(byte[] data, int currentReadOffset) {
		// because we only use the lower 7-bits of each byte, we need to copy into a local byte array
		int bytesPerType = getByteLength();
		byte[] sevenBitBytes = new byte[bytesPerType];
		for (int i = 0; i < bytesPerType; i++) {
			sevenBitBytes[i] = (byte) (data[currentReadOffset + i] & 0x7F);
		}
		// because we've copied into a local array, we use the 0 offset
		return EndianConverterFactory.createEndianConverter(getEndianness())
				.convertNumber(sevenBitBytes, 0, bytesPerType);
	}
}
