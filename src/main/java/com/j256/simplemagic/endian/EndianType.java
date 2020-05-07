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
	BIG('B', 'S', 'I', 'L'),
	/**
	 * little endian (x86)
	 */
	LITTLE('b', 's', 'i', 'l'),
	/**
	 * old PDP11 byte order
	 */
	MIDDLE('m'),
	/**
	 * uses the byte order of the current system
	 */
	NATIVE();

	private final char[] names;

	/**
	 * Instantiates an enum {@link EndianType} value.
	 *
	 * @param names The magic pattern names for this {@link EndianType}.
	 */
	EndianType(char... names) {
		this.names = names;
	}

	/**
	 * Returns the magic pattern names for this {@link EndianType}.
	 *
	 * @return The magic pattern names for this {@link EndianType}.
	 */
	public char[] getNames() {
		return names;
	}

	/**
	 * Returns a matching {@link EndianType} for the given definition value.
	 *
	 * @param name The definition value an {@link EndianType} shall be found for.
	 * @return The {@link EndianType} matching the given definition value.
	 */
	public static EndianType forName(char name) {
		for (EndianType endianType : values()) {
			for (char endianTypeName : endianType.getNames()) {
				if (endianTypeName == name) {
					return endianType;
				}
			}
		}

		return LITTLE;
	}
}
