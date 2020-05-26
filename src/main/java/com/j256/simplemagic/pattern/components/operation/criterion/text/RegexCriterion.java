package com.j256.simplemagic.pattern.components.operation.criterion.text;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.MagicOperator;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.operation.criterion.AbstractMagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.operation.criterion.MagicCriterionResult;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
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
public class RegexCriterion extends AbstractMagicCriterion<String> {

	private static final Pattern TYPE_PATTERN = Pattern.compile("(/[cs]*)?");

	private int patternFlags;
	private boolean updateOffsetStart;
	private String testValue;
	private Pattern pattern;

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
	 * Returns the Regex flags of this criterion.
	 *
	 * @return The Regex flags of this criterion.
	 */
	@SuppressWarnings("unused")
	public int getPatternFlags() {
		return patternFlags;
	}

	/**
	 * Returns true, if the offset shall be updated to the start of the match, instead of the end.
	 *
	 * @return True, if the offset shall be updated to the start of the match, instead of the end.
	 */
	@SuppressWarnings("unused")
	public boolean isUpdateOffsetStart() {
		return updateOffsetStart;
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
	 * Returns a String value, that must be found in binary data to match this criterion.
	 *
	 * @return The String value, that must be matched.
	 */
	@Override
	public String getTestValue() {
		return testValue;
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
			return new MagicCriterionResult<String>(this, currentReadOffset);
		}
		String match;
		ByteArrayInputStream bis = new ByteArrayInputStream(data, currentReadOffset, data.length - currentReadOffset);
		Scanner scanner = new Scanner(bis);
		try {
			match = scanner.findWithinHorizon(pattern, 0);
		} finally {
			scanner.close();
		}
		if (match == null) {
			return new MagicCriterionResult<String>(this, currentReadOffset);
		}
		return new MagicCriterionResult<String>(this,
				currentReadOffset + match.length(), match);
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

		Matcher matcher = TYPE_PATTERN.matcher(getMagicPattern().getType().getFlagsAndModifiers());
		if (matcher.matches()) {
			String flagsStr = matcher.group(1);
			if (flagsStr != null && flagsStr.length() > 1) {
				for (char ch : flagsStr.toCharArray()) {
					if (ch == 'c') {
						this.patternFlags |= Pattern.CASE_INSENSITIVE;
					} else if (ch == 's') {
						/*
						 * TODO: updateOffsetStart is parsed, but unused.
						 * => is the read offset updated correctly for regex criteria using this?
						 */
						this.updateOffsetStart = true;
					}
				}
			}
		}
		this.testValue = PatternUtils.escapePattern(rawDefinition);
		try {
			this.pattern = Pattern.compile(this.testValue, this.patternFlags);
		} catch (PatternSyntaxException ex) {
			throw new MagicPatternException(String.format("Invalid/unknown REGEX pattern syntax: %s", testValue), ex);
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
