package com.j256.simplemagic.endian;

/**
 * Class which converts from a particular machine byte representation into values appropriate for Java.
 *
 * @author graywatson
 */
public interface EndianConverter {

	/**
	 * Convert a number of bytes starting at an offset into a {@link Long}.
	 *
	 * @param data   The byte array from which shall be read.
	 * @param offset The offset in the byte array from which shall be read.
	 * @param byteLength   The number of bytes, that shall be read.
	 * @return The {@link Long} or null if not enough bytes.
	 */
	Long convertNumber(byte[] data, int offset, int byteLength);

	/**
	 * Convert a number of bytes starting at an offset into a {@link Long} where the high-bit in each byte is always 0.
	 *
	 * @param data   The byte array from which shall be read.
	 * @param offset The offset in the byte array from which shall be read.
	 * @param byteLength   The number of bytes, that shall be read.
	 * @return The {@link Long} or null if not enough bytes.
	 */
	Long convertId3(byte[] data, int offset, int byteLength);

	/**
	 * Translate a number into an array of bytes.
	 *
	 * @param value The value, that shall be translated.
	 * @param byteLength  The number of bytes, that shall be translated.
	 * @return The resulting byte array.
	 */
	byte[] convertToByteArray(long value, int byteLength);
}
