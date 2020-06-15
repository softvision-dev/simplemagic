package com.j256.simplemagic.pattern.components.operation;

import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;

/**
 * <b>Enumerates The various operation types which correspond to the "type" part of a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * The type of the data to be tested. The possible values are:
 * </i>
 * </p>
 */
public enum OperationType {

	/**
	 * <i>A one-byte value</i>
	 */
	BYTE("byte", true, 1),
	/**
	 * <i>A two-byte value in this machine's native byte order.</i>
	 */
	SHORT("short", true, 2),
	/**
	 * <i>A four-byte value in this machine's native byte order.</i>
	 */
	INTEGER("long", true, 4),
	/**
	 * <i>An eight-byte value in this machine's native byte order.</i>
	 */
	LONG("quad", true, 8),
	/**
	 * <i>A 32-bit single precision IEEE floating point number in this machine's native byte order.</i>>
	 */
	FLOAT("float", true, 4),
	/**
	 * <i>A 64-bit double precision IEEE floating point number in this machine's native byte order.</i>
	 */
	DOUBLE("double", true, 8),
	/**
	 * <i>A string of bytes.</i>
	 */
	STRING("string", false, -1),
	/**
	 * <p>
	 * <i>
	 * A Pascal-style string.
	 * </i>
	 * </p>
	 */
	PSTRING("pstring", false, -1),
	/**
	 * <i>A four-byte value interpreted as a UNIX date.</i>
	 */
	DATE("date", true, 4),
	/**
	 * <i>An eight-byte value interpreted as a UNIX date.</i>
	 */
	LONG_DATE("qdate", true, 8),
	/**
	 * <i>A four-byte value interpreted as a UNIX-style date, but interpreted as local time rather than UTC.</i>
	 */
	LOCAL_DATE("ldate", true, 4),
	/**
	 * <i>An eight-byte value interpreted as a UNIX-style date, but interpreted as local time rather than UTC.</i>
	 */
	LONG_LOCAL_DATE("qldate", true, 8),

	/**
	 * <i>A 32-bit ID3 length in big-endian byte order.</i>
	 */
	BIG_ENDIAN_ID3("beid3", true, 4),
	/**
	 * <i>A two-byte value in big-endian byte order.</i>
	 */
	BIG_ENDIAN_SHORT("beshort", true, 2),
	/**
	 * <i>A four-byte value in big-endian byte order.</i>
	 */
	BIG_ENDIAN_INTEGER("belong", true, 4),
	/**
	 * <i>An eight-byte value in big-endian byte order</i>
	 */
	BIG_ENDIAN_LONG("bequad", true, 8),
	/**
	 * <i>A 32-bit single precision IEEE floating point number in big-endian byte order.</i>
	 */
	BIG_ENDIAN_FLOAT("befloat", true, 4),
	/**
	 * <i>A 64-bit double precision IEEE floating point number in big-endian byte order.</i>
	 */
	BIG_ENDIAN_DOUBLE("bedouble", true, 8),
	/**
	 * <i>A four-byte value in big-endian byte order, interpreted as a Unix date.</i>
	 */
	BIG_ENDIAN_DATE("bedate", true, 4),
	/**
	 * <i>An eight-byte value in big-endian byte order, interpreted as a Unix date.</i>
	 */
	BIG_ENDIAN_LONG_DATE("beqdate", true, 8),
	/**
	 * <i>A four-byte value in big-endian byte order, interpreted as a UNIX-style date, but interpreted as local time
	 * rather than UTC.</i>
	 */
	BIG_ENDIAN_LOCAL_DATE("beldate", true, 4),
	/**
	 * <i>An eight-byte value in big-endian byte order, interpreted as a UNIX-style date, but interpreted as local time
	 * rather than UTC.</i>
	 */
	BIG_ENDIAN_LONG_LOCAL_DATE("beqldate", true, 8),
	/**
	 * <i>A two-byte unicode (UCS16) string in big-endian byte order.</i>
	 */
	BIG_ENDIAN_UTF16_STRING("bestring16", false, 2),
	/**
	 * <i>An eight-byte value in big-endian byte order, interpreted as a Windows-style date.</i>
	 */
	//TODO: BIG_ENDIAN_WINDOWS_DATE("beqwdate", true, 8),

	/**
	 * <i>A 32-bit ID3 length in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_ID3("leid3", true, 4),
	/**
	 * <i>A two-byte value in little-endian byte order.</i>>
	 */
	LITTLE_ENDIAN_SHORT("leshort", true, 2),
	/**
	 * <i>A four-byte value in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_INTEGER("lelong", true, 4),
	/**
	 * <i>An eight-byte value in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_LONG("lequad", true, 8),
	/**
	 * <i>A 32-bit single precision IEEE floating point number in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_FLOAT("lefloat", true, 4),
	/**
	 * <i>A 64-bit double precision IEEE floating point number in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_DOUBLE("ledouble", true, 8),
	/**
	 * <i>A four-byte value in little-endian byte order, interpreted as a UNIX date.</i>
	 */
	LITTLE_ENDIAN_DATE("ledate", true, 4),
	/**
	 * <i>An eight-byte value in little-endian byte order, interpreted as a UNIX date.</i>
	 */
	LITTLE_ENDIAN_LONG_DATE("leqdate", true, 8),
	/**
	 * <i>A four-byte value in little-endian byte order, interpreted as a UNIX-style date, but interpreted as local
	 * time rather than UTC.</i>
	 */
	LITTLE_ENDIAN_LOCAL_DATE("leldate", true, 4),
	/**
	 * <i>An eight-byte value in little-endian byte order, interpreted as a UNIX-style date, but interpreted as local
	 * time rather than UTC.</i>
	 */
	LITTLE_ENDIAN_LONG_LOCAL_DATE("leqldate", true, 8),
	/**
	 * <i>A two-byte unicode (UCS16) string in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_UTF16_STRING("lestring16", false, 2),
	/**
	 * <i>An eight-byte value in little-endian byte order, interpreted as a Windows-style date.</i>
	 */
	//TODO: LITTLE_ENDIAN_WINDOWS_DATE("leqwdate", true, 8),

	/**
	 * <i>A regular expression match in extended POSIX regular expression syntax (like egrep).</i>
	 */
	REGEX("regex", false, -1),
	/**
	 * <i>A literal string search.</i>
	 */
	SEARCH("search", false, -1),

	/**
	 * <i>A four-byte value in middle-endian (PDP-11) byte order.</i>
	 */
	MIDDLE_ENDIAN_INTEGER("melong", true, 4),
	/**
	 * <i>A four-byte value in middle-endian (PDP-11) byte order, interpreted as a UNIX date.</i>
	 */
	MIDDLE_ENDIAN_DATE("medate", true, 4),
	/**
	 * <i>A four-byte value in middle-endian (PDP-11) byte order, interpreted as a UNIX-style date, but interpreted as
	 * local time rather than UTC.</i>>
	 */
	MIDDLE_ENDIAN_LOCAL_DATE("meldate", true, 4),
	/**
	 * <i>Define a “named” magic instance that can be called from another use magic entry, like a subroutine call. Named
	 * instance direct magic offsets are relative to the offset of the previous matched entry, but indirect offsets are
	 * relative to the beginning of the file as usual. Named magic entries always match.</i>
	 */
	NAME("name", false, -1),
	/**
	 * <i>Recursively call the named magic starting from the current offset. If the name of the referenced begins with a
	 * ^ then the endianness of the magic is switched; if the magic mentioned leshort for example, it is treated as
	 * beshort and vice versa. This is useful to avoid duplicating the rules for different endianness.</i>
	 */
	USE("use", false, -1),
	/**
	 * <i>This is intended to be used with the test x (which is always true) and it has no type. It matches when no other
	 * test at that continuation level has matched before. Clearing that matched tests for a continuation level, can be
	 * done using the clear test.</i>
	 */
	DEFAULT("default", false,-1),
	/**
	 * <i>Starting at the given offset, consult the magic database again. The offset of the indirect magic is by default
	 * absolute in the file, but one can specify /r to indicate that the offset is relative from the beginning of the
	 * entry.</i>
	 */
	INDIRECT("indirect", false,-1);
	/**
	 * <i>This test is always true and clears the match flag for that continuation level. It is intended to be used with
	 * the default test.</i>
	 */
	//TODO: CLEAR("clear", false, -1),
	/**
	 * <i>Find documentation</i>
	 */
	//TODO: DER("der", false, -1),

	private final String name;
	private final boolean numeric;
	private final int byteLength;

	/**
	 * Instantiates an enum {@link OperationType} value.
	 *
	 * @param name       The magic pattern name for this {@link OperationType}.
	 * @param numeric    True, if the OperationType represents a numeric {@link MagicCriterion}.
	 * @param byteLength The byte length of values compared by a {@link MagicCriterion} of this type.
	 *                   -1 if the type does not represent a criterion, or if the byte length is dynamic.
	 */
	OperationType(String name, boolean numeric, int byteLength) {
		this.name = name;
		this.numeric = numeric;
		this.byteLength = byteLength;
	}

	/**
	 * Returns the magic pattern name for this {@link OperationType}.
	 *
	 * @return The magic pattern name for this {@link OperationType}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns true for numeric types.
	 *
	 * @return True for numeric types.
	 */
	public boolean isNumeric() {
		return numeric;
	}

	/**
	 * Returns the byte length of values compared by a {@link MagicCriterion} of this type.
	 * -1 if the type does not represent a criterion, or if the byte length is dynamic.
	 *
	 * @return The byte length of values compared by a {@link MagicCriterion} of this type.
	 */
	public int getByteLength() {
		return byteLength;
	}

	/**
	 * Returns a matching {@link OperationType} for the given definition value.
	 *
	 * @param name The definition value a {@link OperationType} shall be found for.
	 * @return The {@link OperationType} matching the given definition value.
	 */
	public static OperationType forName(String name) {
		for (OperationType criterionType : values()) {
			if (criterionType.getName().equals(name)) {
				return criterionType;
			}
		}
		return null;
	}
}
