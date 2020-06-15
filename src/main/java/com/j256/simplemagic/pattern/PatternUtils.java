package com.j256.simplemagic.pattern;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.components.offset.MagicOffsetReadType;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides static utility methods, that can be used as a basic toolkit to parse magic patterns or parts of such
 * patterns.
 */
public class PatternUtils {

	public static final Pattern HEX_PATTERN = Pattern.compile("0[xX]([0-9a-fA-F]+).*");

	/**
	 * As methods shall be provided as static utility methods, the instantiation of this class is forbidden.
	 */
	private PatternUtils() {
	}

	/**
	 * Finds the first non whitespace character in the given value, following the given startPos.
	 *
	 * @param value    The value, that shall be searched.
	 * @param startPos The offset of the search.
	 * @return The index of the first non whitespace character following the startPos. Will return -1 if none
	 * has been found.
	 */
	public static int findNonWhitespace(String value, int startPos) {
		if (value == null) {
			return -1;
		}
		for (int pos = startPos; pos < value.length(); pos++) {
			if (!Character.isWhitespace(value.charAt(pos))) {
				return pos;
			}
		}
		return -1;
	}

	/**
	 * Finds the first whitespace character for the given value, that is not escaped (such as tabs), following the given
	 * startPos.
	 *
	 * @param value    The value, that shall be searched.
	 * @param startPos The offset of the search.
	 * @return The index of the first not escaped whitespace character following the startPos. Will return -1 if none
	 * has been found.
	 */
	public static int findWhitespaceWithoutEscape(String value, int startPos) {
		if (value == null) {
			return -1;
		}
		boolean lastEscape = false;
		for (int pos = startPos; pos < value.length(); pos++) {
			char ch = value.charAt(pos);
			if (ch == ' ') {
				if (!lastEscape) {
					return pos;
				}
				lastEscape = false;
			} else if (Character.isWhitespace(value.charAt(pos))) {
				return pos;
			} else lastEscape = ch == '\\';
		}
		return -1;
	}

	/**
	 * Translates the given String to a {@link BigInteger} instance. This does include values in hexadecimal
	 * '0[xX]([0-9a-fA-F]+)' format.
	 *
	 * @param valueString The value, that shall be translated.
	 * @return The resulting {@link BigInteger}
	 * @throws MagicPatternException Shall be thrown, if the given value does not contain a number.
	 */
	public static BigInteger parseNumericValue(String valueString) throws MagicPatternException {
		try {
			Matcher matcher = HEX_PATTERN.matcher(valueString);
			return matcher.matches() ? new BigInteger(matcher.group(1), 16) : new BigInteger(valueString);
		} catch (NumberFormatException ex) {
			throw new MagicPatternException(String.format("Could not parse number from: '%s'", valueString));
		}
	}

	/**
	 * Read a value from the given offset, using the given {@link MagicOffsetReadType} from the given byte array.
	 *
	 * @param data           The byte array, that shall be read from.
	 * @param offset         The offset from which shall be read.
	 * @param offsetReadType The {@link MagicOffsetReadType} containing further read parameters, such as endianness byte
	 *                       length etc.
	 * @return The read long value. (must never return null)
	 * @throws MagicPatternException Shall be thrown for negative offsets.
	 */
	public static long readIndirectOffset(byte[] data, long offset, MagicOffsetReadType offsetReadType)
			throws MagicPatternException {
		if (offset < 0) {
			throw new MagicPatternException("Erroneous indirect offset construction.");
		}
		Long readOffset = 0L;
		if (offsetReadType == null || data == null) {
			return readOffset;
		}
		if (offsetReadType.isReadID3Length()) {
			readOffset = offsetReadType.getEndianConverter().convertId3(
					data, (int) offset, offsetReadType.getValueByteLength()
			);
		} else {
			readOffset = offsetReadType.getEndianConverter().convertNumber(
					data, (int) offset, offsetReadType.getValueByteLength()
			);
		}

		return readOffset == null ? 0 : readOffset;
	}

	/**
	 * Pre-processes and escapes a C-String based pattern.
	 *
	 * @param pattern The pattern, that shall be pre-processed.
	 * @return The processed pattern as a String.
	 */
	public static String escapePattern(String pattern) {
		int index = pattern.indexOf('\\');
		if (index < 0) {
			return pattern;
		}

		StringBuilder sb = new StringBuilder();
		for (int pos = 0; pos < pattern.length(); ) {
			char ch = pattern.charAt(pos);
			if (ch != '\\') {
				sb.append(ch);
				pos++;
				continue;
			}
			if (pos + 1 >= pattern.length()) {
				// we'll end the pattern with a '\\' char
				sb.append(ch);
				break;
			}
			ch = pattern.charAt(++pos);
			switch (ch) {
				case 'b':
					sb.append('\b');
					pos++;
					break;
				case 'f':
					sb.append('\f');
					pos++;
					break;
				case 'n':
					sb.append('\n');
					pos++;
					break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7': {
					// 1-3 octal characters: \1 \01 or \017
					pos += radixCharsToChar(sb, pattern, pos, 3, 8);
					break;
				}
				case 'r':
					sb.append('\r');
					pos++;
					break;
				case 't':
					sb.append('\t');
					pos++;
					break;
				case 'x': {
					// 1-2 hex characters: \xD or \xD9
					int adjust = radixCharsToChar(sb, pattern, pos + 1, 2, 16);
					if (adjust > 0) {
						// adjust by 1 for the x
						pos += 1 + adjust;
					} else {
						sb.append(ch);
						pos++;
					}
					break;
				}
				case ' ':
				case '\\':
				default:
					sb.append(ch);
					pos++;
					break;
			}
		}
		return sb.toString();
	}

	/**
	 * Translates embedded numeric (octal/hexadecimal) values to decimal characters.
	 *
	 * @param sb      The String builder the values shall be written to.
	 * @param pattern The processed pattern.
	 * @param pos     The current position in the pattern.
	 * @param maxLen  Length of the expected embedded numeric value.
	 * @param radix   The radix of the embedded value.
	 * @return The new position, after reading the numeric value.
	 */
	private static int radixCharsToChar(StringBuilder sb, String pattern, int pos, int maxLen, int radix) {
		int val = 0;
		int i = 0;
		for (; i < maxLen; i++) {
			if (pos + i >= pattern.length()) {
				break;
			}
			int digit = Character.digit(pattern.charAt(pos + i), radix);
			if (digit < 0) {
				break;
			}
			val = val * radix + digit;
		}
		if (i > 0) {
			sb.append((char) val);
		}
		return i;
	}
}
