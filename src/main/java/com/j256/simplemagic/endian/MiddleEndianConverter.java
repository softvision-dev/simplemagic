package com.j256.simplemagic.endian;

/**
 * A four-byte value in middle-endian PDP-11 byte order.
 *
 * @author graywatson
 */
public class MiddleEndianConverter extends AbstractEndianConverter {

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
		if (byteLength == 4) {
			// BADC again
			return new byte[]{(byte) ((value >> 16) & 0XFF), (byte) ((value >> 24) & 0XFF),
					(byte) ((value) & 0XFF), (byte) ((value >> 8) & 0XFF)};
		} else {
			return null;
		}
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
		if (byteLength != 4) {
			throw new UnsupportedOperationException("Middle-endian only supports 4-byte integers");
		}
		if (offset < 0 || offset + byteLength > data.length) {
			return null;
		}
		long value = 0;
		// BADC
		value = (value << shift) | (data[offset + 1] & mask);
		value = (value << shift) | (data[offset] & mask);
		value = (value << shift) | (data[offset + 3] & mask);
		value = (value << shift) | (data[offset + 2] & mask);
		return value;
	}
}
