package com.j256.simplemagic.pattern.components.criterion.types;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.PatternUtils;
import com.j256.simplemagic.pattern.components.criterion.AbstractMagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterion;
import com.j256.simplemagic.pattern.components.criterion.MagicCriterionResult;
import com.j256.simplemagic.pattern.components.criterion.operator.StringOperator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a Regex criterion from a line in magic (5) format.
 * <p>
 * From the magic(5) man page: A regular expression match in extended POSIX regular expression syntax (like egrep).
 * Regular expressions can take exponential time to process, and their performance is hard to predict, so their use is
 * discouraged. When used in production environments, their performance should be carefully checked. The type
 * specification can be optionally followed by /[c][s]. The 'c' flag makes the match case insensitive, while the 's'
 * flag update the offset to the start offset of the match, rather than the end. The regular expression is tested
 * against line N + 1 onwards, where N is the given offset. Line endings are assumed to be in the machine's native
 * format. ^ and $ match the beginning and end of individual lines, respectively, not beginning and end of file.
 * </p>
 *
 * @author graywatson
 */
public class RegexCriterion extends AbstractMagicCriterion<String, StringOperator> {

	private static final Pattern TYPE_PATTERN = Pattern.compile("[^/]+(/[cs]*)?");

	private int patternFlags;
	private boolean updateOffsetStart;
	private String testValue;
	private Pattern pattern;

	/**
	 * Creates a new {@link RegexCriterion} as found in a {@link MagicPattern}. The criterion shall define one Search
	 * evaluation contained by a pattern.
	 * <p>
	 * If the criterion is met for given binary data, this indicates the patterns type assumption to be at least
	 * partially correct. ({@link MagicCriterion#isMatch(byte[], int)})
	 * </p>
	 *
	 * @throws MagicPatternException Shall be thrown, if an invalid default operator has been set.
	 */
	public RegexCriterion() throws MagicPatternException {
		super(StringOperator.EQUALS);
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
	 * @return A {@link MagicCriterionResult} summarizing the evaluation results.
	 * @throws IOException           Shall be thrown, if accessing the data failed.
	 * @throws MagicPatternException Shall be thrown, if the evaluation failed (possibly due to a malformed criterion.)
	 */
	@Override
	public MagicCriterionResult<String, StringOperator> isMatch(byte[] data, int currentReadOffset)
			throws IOException, MagicPatternException {
		int readOffset = getMagicPattern().getOffset().getReadOffset(data, currentReadOffset);

		String line = null;
		int bytesOffset = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		try {
			for (int i = 0; i <= readOffset; i++) {
				line = reader.readLine();
				// if eof then no match
				if (line == null) {
					return new MagicCriterionResult<String, StringOperator>(this, currentReadOffset);
				}
				// TODO: this doesn't take into account multiple line-feeds and multi-byte chars
				if (i < readOffset) {
					bytesOffset += line.length() + 1;
				}
			}
		} catch (IOException ex) {
			return new MagicCriterionResult<String, StringOperator>(this, currentReadOffset);
		} finally {
			reader.close();
		}

		if (line == null || pattern == null) {
			return new MagicCriterionResult<String, StringOperator>(this, currentReadOffset);
		}
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			return new MagicCriterionResult<String, StringOperator>(this,
					bytesOffset + matcher.end(1), matcher.group(1));
		} else {
			return new MagicCriterionResult<String, StringOperator>(this, currentReadOffset);
		}
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
	public void parse(MagicPattern magicPattern, String rawDefinition) throws MagicPatternException {
		super.parse(magicPattern, rawDefinition);
		if (rawDefinition == null || rawDefinition.length() == 0) {
			throw new MagicPatternException("Criterion definition is empty.");
		}

		Matcher matcher = TYPE_PATTERN.matcher(getMagicPattern().getType().getTypeString());
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
		this.testValue = ".*(" + PatternUtils.unescapePattern(rawDefinition) + ").*";
		this.pattern = Pattern.compile(this.testValue, this.patternFlags);
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
