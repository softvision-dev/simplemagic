package com.j256.simplemagic.pattern.components.operation;

/**
 * <b>Enumerates The various operation types which correspond to the "type" part of the magic pattern format.</b>
 *
 * @author graywatson
 */
public enum OperationType {

	/**
	 * <i>A one-byte value</i>
	 */
	BYTE("byte", true),
	/**
	 * <i>A two-byte value in this machine's native byte order.</i>
	 */
	SHORT("short", true),
	/**
	 * <i>A four-byte value in this machine's native byte order.</i>
	 */
	INTEGER("long", true),
	/**
	 * <i>An eight-byte value in this machine's native byte order.</i>
	 */
	LONG("quad", true),
	/**
	 * <i>A 32-bit single precision IEEE floating point number in this machine's native byte order.</i>>
	 */
	FLOAT("float", true),
	/**
	 * <i>A 64-bit double precision IEEE floating point number in this machine's native byte order.</i>
	 */
	DOUBLE("double", true),
	/**
	 * <i>A string of bytes. The string type specification can be optionally followed by /[WwcCtbT]*. The “W” flag
	 * compacts whitespace in the target, which must contain at least one whitespace character. If the magic has n
	 * consecutive blanks, the target needs at least n consecutive blanks to match. The “w” flag treats every blank
	 * in the magic as an optional blank. The “c” flag specifies case insensitive matching: lower case characters in
	 * the magic match both lower and upper case characters in the target, whereas upper case characters in the magic
	 * only match upper case characters in the target. The “C” flag specifies case insensitive matching: upper case
	 * characters in the magic match both lower and upper case characters in the target, whereas lower case characters
	 * in the magic only match upper case characters in the target. To do a complete case insensitive match, specify
	 * both “c” and “C”. The “t” flag forces the test to be done for text files, while the “b” flag forces the test
	 * to be done for binary files. The “T” flag causes the string to be trimmed, i.e. leading and trailing whitespace
	 * is deleted before the string is printed.</i>
	 */
	STRING("string", false),
	/**
	 * <p>
	 * <i>
	 * A Pascal-style string where the first byte/short/int is interpreted as the unsigned length. The length
	 * defaults to byte and can be specified as a modifier. The following modifiers are supported:
	 * </i>
	 * </p>
	 * <ul>
	 * <li><i>B - A byte length (default).</i></li>
	 * <li><i>H - A 2 byte big endian length.</i></li>
	 * <li><i>h - A 2 byte little endian length.</i></li>
	 * <li><i>L - A 4 byte big endian length.</i></li>
	 * <li><i>l - A 4 byte little endian length.</i></li>
	 * <li><i>J - The length includes itself in its count.</i></li>
	 * </ul>
	 * <p>
	 * <i>
	 * The string is not NUL terminated. “J” is used rather than the more valuable “I” because this type of length is a
	 * feature of the JPEG format.
	 * </i>
	 * </p>
	 */
	PSTRING("pstring", false),
	/**
	 * <i>A four-byte value interpreted as a UNIX date.</i>
	 */
	DATE("date", true),
	/**
	 * <i>An eight-byte value interpreted as a UNIX date.</i>
	 */
	LONG_DATE("qdate", true),
	/**
	 * <i>A four-byte value interpreted as a UNIX-style date, but interpreted as local time rather than UTC.</i>
	 */
	LOCAL_DATE("ldate", true),
	/**
	 * <i>An eight-byte value interpreted as a UNIX-style date, but interpreted as local time rather than UTC.</i>
	 */
	LONG_LOCAL_DATE("qldate", true),

	/**
	 * <i>A 32-bit ID3 length in big-endian byte order.</i>
	 */
	BIG_ENDIAN_ID3("beid3", true),
	/**
	 * <i>A two-byte value in big-endian byte order.</i>
	 */
	BIG_ENDIAN_SHORT("beshort", true),
	/**
	 * <i>A four-byte value in big-endian byte order.</i>
	 */
	BIG_ENDIAN_INTEGER("belong", true),
	/**
	 * <i>An eight-byte value in big-endian byte order</i>
	 */
	BIG_ENDIAN_LONG("bequad", true),
	/**
	 * <i>A 32-bit single precision IEEE floating point number in big-endian byte order.</i>
	 */
	BIG_ENDIAN_FLOAT("befloat", true),
	/**
	 * <i>A 64-bit double precision IEEE floating point number in big-endian byte order.</i>
	 */
	BIG_ENDIAN_DOUBLE("bedouble", true),
	/**
	 * <i>A four-byte value in big-endian byte order, interpreted as a Unix date.</i>
	 */
	BIG_ENDIAN_DATE("bedate", true),
	/**
	 * <i>An eight-byte value in big-endian byte order, interpreted as a Unix date.</i>
	 */
	BIG_ENDIAN_LONG_DATE("beqdate", true),
	/**
	 * <i>A four-byte value in big-endian byte order, interpreted as a UNIX-style date, but interpreted as local time
	 * rather than UTC.</i>
	 */
	BIG_ENDIAN_LOCAL_DATE("beldate", true),
	/**
	 * <i>An eight-byte value in big-endian byte order, interpreted as a UNIX-style date, but interpreted as local time
	 * rather than UTC.</i>
	 */
	BIG_ENDIAN_LONG_LOCAL_DATE("beqldate", true),
	/**
	 * <i>A two-byte unicode (UCS16) string in big-endian byte order.</i>
	 */
	BIG_ENDIAN_TWO_BYTE_STRING("bestring16", false),
	/**
	 * <i>An eight-byte value in big-endian byte order, interpreted as a Windows-style date.</i>
	 */
	//TODO: BIG_ENDIAN_WINDOWS_DATE("beqwdate", true),

	/**
	 * <i>A 32-bit ID3 length in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_ID3("leid3", true),
	/**
	 * <i>A two-byte value in little-endian byte order.</i>>
	 */
	LITTLE_ENDIAN_SHORT("leshort", true),
	/**
	 * <i>A four-byte value in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_INTEGER("lelong", true),
	/**
	 * <i>An eight-byte value in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_LONG("lequad", true),
	/**
	 * <i>A 32-bit single precision IEEE floating point number in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_FLOAT("lefloat", true),
	/**
	 * <i>A 64-bit double precision IEEE floating point number in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_DOUBLE("ledouble", true),
	/**
	 * <i>A four-byte value in little-endian byte order, interpreted as a UNIX date.</i>
	 */
	LITTLE_ENDIAN_DATE("ledate", true),
	/**
	 * <i>An eight-byte value in little-endian byte order, interpreted as a UNIX date.</i>
	 */
	LITTLE_ENDIAN_LONG_DATE("leqdate", true),
	/**
	 * <i>A four-byte value in little-endian byte order, interpreted as a UNIX-style date, but interpreted as local
	 * time rather than UTC.</i>
	 */
	LITTLE_ENDIAN_LOCAL_DATE("leldate", true),
	/**
	 * <i>An eight-byte value in little-endian byte order, interpreted as a UNIX-style date, but interpreted as local
	 * time rather than UTC.</i>
	 */
	LITTLE_ENDIAN_LONG_LOCAL_DATE("leqldate", true),
	/**
	 * <i>A two-byte unicode (UCS16) string in little-endian byte order.</i>
	 */
	LITTLE_ENDIAN_TWO_BYTE_STRING("lestring16", false),
	/**
	 * <i>An eight-byte value in little-endian byte order, interpreted as a Windows-style date.</i>
	 */
	//TODO: LITTLE_ENDIAN_WINDOWS_DATE("leqwdate", true),

	/**
	 * <i>A regular expression match in extended POSIX regular expression syntax (like egrep). Regular expressions can
	 * take exponential time to process, and their performance is hard to predict, so their use is discouraged. When
	 * used in production environments, their performance should be carefully checked. The size of the string to search
	 * should also be limited by specifying /<length>, to avoid performance issues scanning long files. The type
	 * specification can also be optionally followed by /[c][s][l]. The “c” flag makes the match case insensitive, while
	 * the “s” flag update the offset to the start offset of the match, rather than the end. The “l” modifier, changes
	 * the limit of length to mean number of lines instead of a byte count. Lines are delimited by the platforms native
	 * line delimiter. When a line count is specified, an implicit byte count also computed assuming each line is 80
	 * characters long. If neither a byte or line count is specified, the search is limited automatically to 8KiB. ^
	 * and $ match the beginning and end of individual lines, respectively, not beginning and end of file.</i>
	 */
	REGEX("regex", false),
	/**
	 * <i>A literal string search starting at the given offset. The same modifier flags can be used as for string
	 * patterns. The search expression must contain the range in the form /number, that is the number of positions at
	 * which the match will be attempted, starting from the start offset. This is suitable for searching larger binary
	 * expressions with variable offsets, using \ escapes for special characters. The order of modifier and number is
	 * not relevant.</i>
	 */
	SEARCH("search", false),

	/**
	 * <i>A four-byte value in middle-endian (PDP-11) byte order.</i>
	 */
	MIDDLE_ENDIAN_INTEGER("melong", true),
	/**
	 * <i>A four-byte value in middle-endian (PDP-11) byte order, interpreted as a UNIX date.</i>
	 */
	MIDDLE_ENDIAN_DATE("medate", true),
	/**
	 * <i>A four-byte value in middle-endian (PDP-11) byte order, interpreted as a UNIX-style date, but interpreted as
	 * local time rather than UTC.</i>>
	 */
	MIDDLE_ENDIAN_LOCAL_DATE("meldate", true),
	/**
	 * <i>Define a “named” magic instance that can be called from another use magic entry, like a subroutine call. Named
	 * instance direct magic offsets are relative to the offset of the previous matched entry, but indirect offsets are
	 * relative to the beginning of the file as usual. Named magic entries always match.</i>
	 */
	NAME("name", false),
	/**
	 * <i>Recursively call the named magic starting from the current offset. If the name of the referenced begins with a
	 * ^ then the endianness of the magic is switched; if the magic mentioned leshort for example, it is treated as
	 * beshort and vice versa. This is useful to avoid duplicating the rules for different endianness.</i>
	 */
	USE("use", false),
	/**
	 * <i>This is intended to be used with the test x (which is always true) and it has no type. It matches when no other
	 * test at that continuation level has matched before. Clearing that matched tests for a continuation level, can be
	 * done using the clear test.</i>
	 */
	DEFAULT("default", false),
	/**
	 * <i>Starting at the given offset, consult the magic database again. The offset of the indirect magic is by default
	 * absolute in the file, but one can specify /r to indicate that the offset is relative from the beginning of the
	 * entry.</i>
	 */
	INDIRECT("indirect", false);
	/**
	 * <i>This test is always true and clears the match flag for that continuation level. It is intended to be used with
	 * the default test.</i>
	 */
	//TODO: CLEAR("clear", false),
	/**
	 * <i>Find documentation</i>
	 */
	//TODO: DER("der", false),

	private final String name;
	private final boolean numeric;

	/**
	 * Instantiates an enum {@link OperationType} value.
	 *
	 * @param name The magic pattern name for this {@link OperationType}.
	 */
	OperationType(String name, boolean numeric) {
		this.name = name;
		this.numeric = numeric;
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
