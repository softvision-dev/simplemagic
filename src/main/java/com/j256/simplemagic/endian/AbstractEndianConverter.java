package com.j256.simplemagic.endian;

public abstract class AbstractEndianConverter implements EndianConverter {

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
	protected abstract Long convertNumber(byte[] data, int offset, int byteLength, int shift, int mask);
}
