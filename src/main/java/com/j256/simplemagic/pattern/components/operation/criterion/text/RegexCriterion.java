package com.j256.simplemagic.pattern.components.operation.criterion.text;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.operation.criterion.ExtractedValue;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.operation.criterion.modifiers.TextCriterionModifiers;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <b>Represents a Regex criterion from a line in magic (5) format.</b>
 * <p>
 * As defined in the Magic(5) Manpage:
 * </p>
 * <p>
 * <i>
 * The format of the source fragment files that are used to build this database is as follows: Each line of a fragment
 * file specifies a test to be performed. A test compares the data starting at a particular offset in the file with a
 * byte value, a string or a numeric value. If the test succeeds, a message is printed.
 * </i>
 * </p>
 * <p>
 * This criterion represents such a test. This test checks:
 * </p>
 * <p>
 * <i>
 * A regular expression match in extended POSIX regular expression syntax (like egrep). Regular expressions can take
 * exponential time to process, and their performance is hard to predict, so their use is discouraged. When used in
 * production environments, their performance should be carefully checked. The size of the string to search should also
 * be limited by specifying /<length>, to avoid performance issues scanning long files. The type specification can also
 * be optionally followed by /[c][s][l]. The “c” flag makes the match case insensitive, while the “s” flag update the
 * offset to the start offset of the match, rather than the end. The “l” modifier, changes the limit of length to mean
 * number of lines instead of a byte count. Lines are delimited by the platforms native line delimiter. When a line
 * count is specified, an implicit byte count also computed assuming each line is 80 characters long. If neither a
 * byte or line count is specified, the search is limited automatically to 8KiB. ^ and $ match the beginning and end
 * of individual lines, respectively, not beginning and end of file.
 * </i>
 * </p>
 */
public class RegexCriterion extends AbstractTextCriterion {

	private static final int DEFAULT_MAXIMUM_BYTE_RANGE = 8 * 1024;

	private Pattern pattern;
	private int length;
	private boolean caseInsensitive = false;
	private boolean gotoStartOffset = false;
	private boolean readLines = false;

	/**
	 * Creates a new {@link RegexCriterion} as found in a {@link MagicPattern}. The criterion shall define one Search
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int, boolean)})
	 * </p>
	 *
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public RegexCriterion() throws MagicPatternException {
		super(MagicOperator.EQUALS);
	}

	/**
	 * Returns true, if the regex shall be evaluated case insensitive.
	 *
	 * @return True, if the regex shall be evaluated case insensitive.
	 */
	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	/**
	 * Returns true, if the offset shall be updated to the start of the match, instead of the end.
	 *
	 * @return True, if the offset shall be updated to the start of the match, instead of the end.
	 */
	public boolean isGotoStartOffset() {
		return gotoStartOffset;
	}

	/**
	 * Returns true, if the length of the matched String shall be given in lines, not in bytes.
	 *
	 * @return True, if the length of the matched String shall be given in lines, not in bytes.
	 */
	public boolean isReadLines() {
		return readLines;
	}

	/**
	 * Returns the number of lines or length of bytes, that shall be searched.
	 *
	 * @return The number of lines or length of bytes, that shall be searched.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Returns the Regex pattern, that shall be matched for this criterion.
	 *
	 * @return The Regex pattern, that shall be matched for this criterion.
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * Returns the value, that is actually found in the data at the expected position. May not return null directly,
	 * wrap 'null' value using {@link ExtractedValue} instead. This does not return a usable value for regex
	 * criteria.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @param length            The value length in bytes. (-1 if no length shall be given.)
	 * @param invertEndianness  Whether the currently determined endianness shall be inverted.
	 * @return The value, that shall match the criterion.
	 */
	@Override
	public ExtractedValue<String> getActualValue(byte[] data, int currentReadOffset, int length, boolean invertEndianness) {
		return new ExtractedValue<String>(null, currentReadOffset);
	}

	/**
	 * Shall evaluate the {@link MagicCriterion} for the given data and offset and shall return a {@link MagicCriterionResult}
	 * summarizing the evaluation results.
	 *
	 * @param data              The binary data, that shall be checked whether they match this criterion.
	 * @param currentReadOffset The initial offset in the given data.
	 * @param invertEndianness  True, if the endianness of extracted data shall be inverted for this test.
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 */
	@Override
	public MagicCriterionResult<String> isMatch(byte[] data, int currentReadOffset, boolean invertEndianness) {
		if (pattern == null) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}
		// If neither a byte or line count is specified, the search is limited automatically to 8KiB.
		int length = getLength() <= 0 ? DEFAULT_MAXIMUM_BYTE_RANGE : getLength();
		ByteArrayInputStream bis;
		String match = null;
		int matchOffset = currentReadOffset;

		// Execute a REGEX based pattern search.
		try {
			// Execute a line based pattern matching.
			if (isReadLines()) {
				// When a line count is specified, an implicit byte count also computed assuming each line is 80
				// characters.
				int implicitByteCount = length * 80;
				bis = new ByteArrayInputStream(
						data, currentReadOffset, implicitByteCount
				);
				Scanner scanner = new Scanner(bis);
				int searchedLines = 0;
				try {
					while (scanner.hasNextLine() && searchedLines < length) {
						match = scanner.findInLine(pattern);
						if (match != null) {
							matchOffset = currentReadOffset +
									(isGotoStartOffset() ? scanner.match().start() : scanner.match().end());
							break;
						}
						scanner.nextLine();
						searchedLines++;
					}
				} finally {
					scanner.close();
					bis.close();
				}
			}
			// Execute a byte count based pattern matching.
			else {
				bis = new ByteArrayInputStream(
						data, currentReadOffset, length
				);
				Scanner scanner = new Scanner(bis);
				try {
					match = scanner.findWithinHorizon(pattern, 0);
					if (match != null) {
						matchOffset = currentReadOffset +
								(isGotoStartOffset() ? scanner.match().start() : scanner.match().end());
					}
				} finally {
					scanner.close();
					bis.close();
				}
			}
		} catch (IOException e) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}

		if (match == null) {
			return new MagicCriterionResult<String>(false, this, currentReadOffset);
		}

		// The “s” flag update the offset to the start offset of the match, rather than the end.
		return new MagicCriterionResult<String>(true, this, matchOffset, match);
	}

	/**
	 * Parse the given raw definition to initialize this {@link MagicCriterion} instance.
	 *
	 * @param magicPattern  The pattern, this criterion is defined for. (For reflective access.) A 'null' value shall
	 *                      be treated as invalid.
	 * @param rawDefinition The raw definition of the {@link MagicCriterion} as a String.
	 * @throws MagicPatternException Shall be thrown, if the parsing failed.
	 */
	@Override
	public void doParse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		if (rawDefinition == null || rawDefinition.length() == 0) {
			throw new MagicPatternException("Criterion definition is empty.");
		}

		// Try to find additional flags and patterns for the String type.
		parseTypeAppendedModifiers(getMagicPattern().getType().getModifiers());

		// Compile regex pattern.
		setExpectedValue(PatternUtils.escapePattern(rawDefinition));
		try {
			if (isCaseInsensitive()) {
				this.pattern = Pattern.compile(getExpectedValue(), Pattern.CASE_INSENSITIVE);
			} else {
				this.pattern = Pattern.compile(getExpectedValue());
			}
		} catch (PatternSyntaxException ex) {
			throw new MagicPatternException(
					String.format("Invalid/unknown REGEX pattern syntax: %s", getExpectedValue()), ex
			);
		}
	}

	/**
	 * Parse the type appended modifiers of this {@link MagicCriterion}.
	 *
	 * @param modifiers The type appended modifiers.
	 */
	@SuppressWarnings("DuplicatedCode")
	@Override
	protected void parseTypeAppendedModifiers(String modifiers) {
		TextCriterionModifiers textFlagsAndModifiers = new TextCriterionModifiers(modifiers);
		if (textFlagsAndModifiers.isEmpty()) {
			return;
		}

		// The size of the string to search should also be limited by specifying /<length>, to avoid performance issues
		// scanning long files.
		this.length = textFlagsAndModifiers.getNumericModifiers().isEmpty() ?
				0 : textFlagsAndModifiers.getNumericModifiers().get(0);

		// The type specification can also be optionally followed by /[c][s][l].
		for (char ch : textFlagsAndModifiers.getCharacterModifiers()) {
			switch (ch) {
				// The “c” flag makes the match case insensitive
				case 'c':
					this.caseInsensitive = true;
					break;
				// The “s” flag updates the offset to the start offset of the match, rather than the end.
				case 's':
					this.gotoStartOffset = true;
					break;
				// The “l” modifier, changes the limit of length to mean number of lines instead of a byte count.
				case 'l':
					this.readLines = true;
			}
		}
	}

	/**
	 * Returns the characteristic starting bytes of this {@link MagicCriterion}. Allows for faster selection of
	 * relevant patterns.
	 *
	 * @return An array of the characteristic starting bytes of criterion, or null if such bytes can not be determined.
	 */
	@Override
	public byte[] getStartingBytes() {
		return null;
	}
}
