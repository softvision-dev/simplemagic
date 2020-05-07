package com.j256.simplemagic.pattern.components.criterion.types;

/**
 * The various types which correspond to the "type" part of the magic (5) format.
 *
 * @author graywatson
 */
public enum CriterionType {

	/**
	 * Single byte value.
	 */
	BYTE("byte"),
	/**
	 * 2 byte short integer in native-endian byte order.
	 */
	SHORT("short"),
	/**
	 * 4 byte "long" integer in native-endian byte order. This is C language long.
	 */
	INTEGER("long"),
	/**
	 * 8 byte long integer in native-endian byte order.
	 */
	LONG("quad"),
	/**
	 * 4 byte floating point number in native-endian byte order.
	 */
	FLOAT("float"),
	/**
	 * 8 byte floating point number in native-endian byte order.
	 */
	DOUBLE("double"),
	/**
	 * Special string matching that supports white-space and case handling.
	 */
	STRING("string"),
	/**
	 * Strings that are encoded with the first byte being the length of the string.
	 */
	PSTRING("pstring"),
	/**
	 * 4 byte value in native=endian byte order, interpreted as a Unix date using UTC time zone.
	 */
	DATE("date"),
	/**
	 * 8 byte value in native-endian byte order, interpreted as a Unix date using UTC time zone.
	 */
	LONG_DATE("qdate"),
	/**
	 * 4 byte value in native-endian byte order, interpreted as a Unix date using the local time zone.
	 */
	LOCAL_DATE("ldate"),
	/**
	 * 8 byte value in native-endian byte order, interpreted as a Unix date using the local time zone.
	 */
	LONG_LOCAL_DATE("qldate"),

	/**
	 * 4 byte integer with each byte using lower 7-bits in big-endian byte order.
	 */
	BIG_ENDIAN_ID3("beid3"),
	/**
	 * 2 byte short integer in big-endian byte order.
	 */
	BIG_ENDIAN_SHORT("beshort"),
	/**
	 * 4 byte "long" integer in big-endian byte order. This is C language long (shudder).
	 */
	BIG_ENDIAN_INTEGER("belong"),
	/**
	 * 8 byte long integer in big-endian byte order.
	 */
	BIG_ENDIAN_LONG("bequad"),
	/**
	 * 4 byte floating point number in big-endian byte order.
	 */
	BIG_ENDIAN_FLOAT("befloat"),
	/**
	 * 8 byte floating point number in big-endian byte order.
	 */
	BIG_ENDIAN_DOUBLE("bedouble"),
	/**
	 * 4 byte value in big-endian byte order, interpreted as a Unix date using UTC time zone.
	 */
	BIG_ENDIAN_DATE("bedate"),
	/**
	 * 8 byte value in big-endian byte order, interpreted as a Unix date using UTC time zone.
	 */
	BIG_ENDIAN_LONG_DATE("beqdate"),
	/**
	 * 4 byte value big-endian byte order, interpreted as a Unix date using the local time zone.
	 */
	BIG_ENDIAN_LOCAL_DATE("beldate"),
	/**
	 * 8 byte value in big-endian byte order, interpreted as a Unix date using the local time zone.
	 */
	BIG_ENDIAN_LONG_LOCAL_DATE("beqldate"),
	/**
	 * String made up of 2-byte characters in big-endian byte order.
	 */
	BIG_ENDIAN_TWO_BYTE_STRING("bestring16"),

	/**
	 * 4 byte integer with each byte using lower 7-bits in little-endian byte order.
	 */
	LITTLE_ENDIAN_ID3("leid3"),
	/**
	 * 2 byte short integer in little-endian byte order.
	 */
	LITTLE_ENDIAN_SHORT("leshort"),
	/**
	 * 4 byte "long" integer in little-endian byte order. This is C language long (shudder).
	 */
	LITTLE_ENDIAN_INTEGER("lelong"),
	/**
	 * 8 byte long integer in little-endian byte order.
	 */
	LITTLE_ENDIAN_LONG("lequad"),
	/**
	 * 4 byte floating point number in little-endian byte order.
	 */
	LITTLE_ENDIAN_FLOAT("lefloat"),
	/**
	 * 8 byte floating point number in little-endian byte order.
	 */
	LITTLE_ENDIAN_DOUBLE("ledouble"),
	/**
	 * 4 byte value in little-endian byte order, interpreted as a Unix date using UTC time zone.
	 */
	LITTLE_ENDIAN_DATE("ledate"),
	/**
	 * 8 byte value in little-endian byte order, interpreted as a Unix date using UTC time zone.
	 */
	LITTLE_ENDIAN_LONG_DATE("leqdate"),
	/**
	 * 4 byte value little-endian byte order, interpreted as a Unix date using the local time zone.
	 */
	LITTLE_ENDIAN_LOCAL_DATE("leldate"),
	/**
	 * 8 byte value in little-endian byte order, interpreted as a Unix date using the local time zone.
	 */
	LITTLE_ENDIAN_LONG_LOCAL_DATE("leqldate"),
	/**
	 * String made up of 2-byte characters in little-endian byte order.
	 */
	LITTLE_ENDIAN_TWO_BYTE_STRING("lestring16"),

	// indirect -- special

	/**
	 * Regex line search looking for compiled patterns.
	 */
	REGEX("regex"),
	/**
	 * String line search looking for sub-strings.
	 */
	SEARCH("search"),

	/**
	 * 4 byte "long" integer in middle-endian byte order. This is C language long (shudder).
	 */
	MIDDLE_ENDIAN_INTEGER("melong"),
	/**
	 * 4 byte value in middle-endian byte order, interpreted as a Unix date using UTC time zone.
	 */
	MIDDLE_ENDIAN_DATE("medate"),
	/**
	 * 4 byte value middle-endian byte order, interpreted as a Unix date using the local time zone.
	 */
	MIDDLE_ENDIAN_LOCAL_DATE("meldate"),
	/**
	 * Default criterion type (always returining true).
	 */
	DEFAULT("default");

	private final String name;

	/**
	 * Instantiates an enum {@link CriterionType} value.
	 *
	 * @param name The magic pattern name for this {@link CriterionType}.
	 */
	CriterionType(String name) {
		this.name = name;
	}

	/**
	 * Returns the magic pattern name for this {@link CriterionType}.
	 *
	 * @return The magic pattern name for this {@link CriterionType}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a matching {@link CriterionType} for the given definition value.
	 *
	 * @param name The definition value a {@link CriterionType} shall be found for.
	 * @return The {@link CriterionType} matching the given definition value.
	 */
	public static CriterionType forName(String name) {
		for (CriterionType criterionType : values()) {
			if (criterionType.getName().equals(name)) {
				return criterionType;
			}
		}
		return null;
	}
}
