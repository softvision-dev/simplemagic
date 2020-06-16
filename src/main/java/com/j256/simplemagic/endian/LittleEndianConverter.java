package com.j256.simplemagic.endian;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * Converts values in "little" endian-ness where the high-order bytes come _after_ the low-order (DCBA). x86 processors.
 *
 * @author graywatson
 */
public class LittleEndianConverter extends AbstractEndianConverter {

	/**
	 * Convert a number of bytes starting at an offset into a {@link Long}.
	 *
	 * @param data       The byte array from which shall be read.
	 * @param offset     The offset in the byte array from which shall be read.
	 * @param byteLength The number of bytes, that shall be read.
	 * @return The {@link Long} or null if not enough bytes.
	 */
	@Override
	public Long convertNumber(byte[] data, int offset, int byteLength) {
		return convertNumber(data, offset, byteLength, 8, 0xFF);
	}

	/**
	 * Convert a number of bytes starting at an offset into a UTF8 1-byte {@link String}.
	 *
	 * @param data       The byte array from which shall be read.
	 * @param offset     The offset in the byte array from which shall be read.
	 * @param byteLength The number of bytes, that shall be read.
	 * @return The {@link String} or null if not enough bytes.
	 */
	@Override
	public String convertUTF8String(byte[] data, int offset, int byteLength) {
		return convertString(data, offset, byteLength, 1, 8, 0xFF);
	}

	/**
	 * Convert a number of bytes starting at an offset into a UTF16 2-byte {@link String}.
	 *
	 * @param data       The byte array from which shall be read.
	 * @param offset     The offset in the byte array from which shall be read.
	 * @param byteLength The number of bytes, that shall be read.
	 * @return The {@link String} or null if not enough bytes.
	 */
	@Override
	public String convertUTF16String(byte[] data, int offset, int byteLength) {
		return convertString(data, offset, byteLength, 2, 8, 0xFF);
	}

	/**
	 * Convert a number of bytes starting at an offset into a {@link Long} where the high-bit in each byte is always 0.
	 *
	 * @param data       The byte array from which shall be read.
	 * @param offset     The offset in the byte array from which shall be read.
	 * @param byteLength The number of bytes, that shall be read.
	 * @return The {@link Long} or null if not enough bytes.
	 */
	@Override
	public Long convertId3(byte[] data, int offset, int byteLength) {
		return convertNumber(data, offset, byteLength, 7, 0x7F);
	}

	/**
	 * Translate a number into an array of bytes.
	 *
	 * @param value      The value, that shall be translated.
	 * @param byteLength The number of bytes, that shall be translated.
	 * @return The resulting byte array.
	 */
	@Override
	public byte[] convertToByteArray(long value, int byteLength) {
		byte[] result = new byte[byteLength];
		for (int i = 0; i < byteLength; i++) {
			result[i] = (byte) (value & 0xFF);
			value >>= 8;
		}
		return result;
	}

	/**
	 * Convert a number of bytes starting at an offset into a {@link Long}.
	 *
	 * @param data       The byte array from which shall be read.
	 * @param offset     The offset in the byte array from which shall be read.
	 * @param byteLength The number of bytes, that shall be read.
	 * @param shift      An additional byte shift, that shall be applied.
	 * @param mask       An additional byte mask, that shall be applied.
	 * @return The {@link Long} or null if not enough bytes.
	 */
	@Override
	protected Long convertNumber(byte[] data, int offset, int byteLength, int shift, int mask) {
		if (offset < 0 || offset + byteLength > data.length) {
			return null;
		}
		long value = 0;
		for (int i = offset + (byteLength - 1); i >= offset; i--) {
			value = value << shift | (data[i] & mask);
		}
		return value;
	}

	/**
	 * Convert a number of bytes starting at an offset into a {@link String}.
	 *
	 * @param data              The byte array from which shall be read.
	 * @param offset            The offset in the byte array from which shall be read.
	 * @param byteLength        The number of bytes, that shall be read.
	 * @param characterByteSize The number of bytes constituting a character.
	 * @param shift             An additional byte shift, that shall be applied.
	 * @param mask              An additional byte mask, that shall be applied.
	 * @return The {@link String} or null if not enough bytes.
	 */
	@SuppressWarnings("DuplicatedCode")
	@Override
	protected String convertString(byte[] data, int offset, int byteLength, int characterByteSize, int shift, int mask) {
		if (offset < 0 || offset + byteLength > data.length) {
			return null;
		}
		BigInteger value = BigInteger.valueOf(0L);
		for (int i = offset + (byteLength - 1); i >= offset; i--) {
			value = value.shiftLeft(shift).or(BigInteger.valueOf(data[i] & mask));
		}

		String stringValue;
		try {
			switch (characterByteSize) {
				case 1:
					stringValue = new String(value.toByteArray(), "UTF-8");
					break;
				case 2:
					stringValue = new String(value.toByteArray(), "UTF-16");
					break;
				default:
					throw new UnsupportedOperationException();
			}
		} catch (UnsupportedEncodingException ex) {
			throw new UnsupportedOperationException();
		}

		return new StringBuilder(stringValue).reverse().toString();
	}
}
