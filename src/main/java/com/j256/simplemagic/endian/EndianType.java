package com.j256.simplemagic.endian;

/**
 * Types of endian-ness supported by the system.
 *
 * @author graywatson
 */
public enum EndianType {

	/**
	 * big endian, also called network byte order (motorola 68k)
	 */
	BIG,
	/**
	 * little endian (x86)
	 */
	LITTLE,
	/**
	 * old PDP11 byte order
	 */
	MIDDLE,
	/**
	 * uses the byte order of the current system
	 */
	NATIVE;

	/**
	 * Instantiates an enum {@link EndianType} value.
	 *
	 * @param names The magic pattern names for this {@link EndianType}.
	 */
	EndianType() {
	}

	/**
	 * Returns the inverted endian type.
	 *
	 * @return The inverted endian type.
	 */
	public EndianType getInvertedEndianType() {
		switch (this) {
			case BIG:
				return LITTLE;
			case LITTLE:
				return BIG;
			default:
				return this;
		}
	}
}
