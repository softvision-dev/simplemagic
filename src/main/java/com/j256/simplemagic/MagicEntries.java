package com.j256.simplemagic;

import com.j256.simplemagic.error.ErrorCallBack;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.logger.Logger;
import com.j256.simplemagic.logger.LoggerFactory;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.j256.simplemagic.pattern.PatternUtils.findNonWhitespace;
import static com.j256.simplemagic.pattern.PatternUtils.findWhitespaceWithoutEscape;

public class MagicEntries {

	private static final Logger LOGGER = LoggerFactory.getLogger(MagicEntries.class);

	private static final int MAX_LEVELS = 20;
	private static final int FIRST_BYTE_LIST_SIZE = 256;

	// Definition extensions:
	private static final String MIME_TYPE_LINE = "!:mime";
	private static final String OPTIONAL_LINE = "!:optional";

	private final List<MagicPattern> magicPatterns = new ArrayList<MagicPattern>();
	//TODO: replace this array of generic types.
	@SuppressWarnings("unchecked")
	private final List<MagicPattern>[] firstByteEntryLists = new ArrayList[FIRST_BYTE_LIST_SIZE];

	public MagicEntries() {
	}

	/**
	 * Parse the {@link MagicPattern} entries, from the given {@link BufferedReader}. Report parsing failures to the given
	 * {@link ErrorCallBack}.
	 *
	 * @param lineReader    The {@link BufferedReader} magic pattern lines, shall be parsed for.
	 * @param errorCallBack The {@link ErrorCallBack}, that shall handle parsing failures. (Set to null to ignore.)
	 * @throws IOException Shall be thrown when an error occurred, using the given {@link BufferedReader}.
	 */
	public void addRules(BufferedReader lineReader, ErrorCallBack errorCallBack) throws IOException {
		if (lineReader == null) {
			return;
		}

		final MagicPattern[] levelParents = new MagicPattern[MAX_LEVELS];
		MagicPattern previousPattern = null;
		String line;
		while ((line = lineReader.readLine()) != null) {
			try {
				// skip blanks and comments
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}

				// handle definition extensions:
				if (line.startsWith("!:")) {
					// We ignore it if there is no previous pattern to describe.
					if (previousPattern == null) {
						continue;
					}
					// Mark previous pattern as optional.
					if (line.equals(OPTIONAL_LINE)) {
						previousPattern.setOptional(true);
						continue;
					}

					// Find extension parts.
					int startPos = findNonWhitespace(line, 0);
					int index = findWhitespaceWithoutEscape(line, startPos);
					if (index < 0) {
						throw new MagicPatternException(
								"invalid extension line has less than 2 whitespace separated fields"
						);
					}
					String key = line.substring(startPos, index);
					startPos = findNonWhitespace(line, index);
					if (startPos < 0) {
						throw new MagicPatternException(
								"invalid extension line has less than 2 whitespace separated fields"
						);
					}
					// find whitespace after value, if any
					index = findWhitespaceWithoutEscape(line, startPos);
					if (index < 0) {
						index = line.length();
					}
					String value = line.substring(startPos, index);

					// Set MIME type for previous pattern.
					if (key.equals(MIME_TYPE_LINE)) {
						previousPattern.setMimeType(value);
					}
				} else {
					MagicPattern pattern = MagicPattern.parse(line);
					int level = pattern.getLevel();
					if (previousPattern == null && level != 0) {
						throw new MagicPatternException(
								"first entry of the file but the level " + level + " should be 0"
						);
					}

					if (level == 0) {
						// top level entry
						this.magicPatterns.add(pattern);
					} else if (levelParents[level - 1] == null) {
						throw new MagicPatternException(
								String.format("entry has level %d but no parent entry with level %d", level, (level - 1))
						);
					} else {
						// we are a child of the one above us
						levelParents[level - 1].addChild(pattern);
					}
					levelParents[level] = pattern;
					previousPattern = pattern;
				}
			} catch (MagicPatternException ex) {
				if (errorCallBack != null) {
					errorCallBack.error(line, ex.getMessage(), ex);
				}
			}
		}
	}

	/**
	 * Optimize the magic entries by removing the first-bytes information into their own lists
	 */
	public void optimizeFirstBytes() {
		// now we post process the entries and remove the first byte ones we can optimize
		for (MagicPattern pattern : magicPatterns) {
			byte[] startingBytes;
			try {
				startingBytes = pattern.getStartingBytes();
			} catch (MagicPatternException e) {
				continue;
			}
			if (startingBytes == null || startingBytes.length == 0) {
				continue;
			}
			int index = (0xFF & startingBytes[0]);
			if (firstByteEntryLists[index] == null) {
				firstByteEntryLists[index] = new ArrayList<MagicPattern>();
			}
			firstByteEntryLists[index].add(pattern);
			/*
			 * We put an entry in the first-byte list but need to leave it in the main list because there may be
			 * optional characters or != or > comparisons in the match
			 */
		}
	}

	public ContentInfo findMatch(byte[] bytes) {
		if (bytes.length == 0) {
			return ContentInfo.EMPTY_INFO;
		}
		// first do the start byte ones
		int index = (0xFF & bytes[0]);
		if (index < firstByteEntryLists.length && firstByteEntryLists[index] != null) {
			ContentInfo info = findMatch(bytes, firstByteEntryLists[index]);
			if (info != null) {
				// this seems to be right to return even if only a partial match here
				return info;
			}
		}
		return findMatch(bytes, magicPatterns);
	}

	private ContentInfo findMatch(byte[] bytes, List<MagicPattern> entryList) {
		ContentInfo partialMatchInfo = null;
		for (MagicPattern pattern : entryList) {
			MatchingResult result;
			try {
				result = pattern.isMatch(bytes);
			} catch (IOException e) {
				continue;
			} catch (MagicPatternException e) {
				continue;
			}
			if (result == null) {
				continue;
			}

			switch (result.getMatchingState()) {
				case FULL_MATCH:
					return new ContentInfo(result.getMessage(), result.getMimeType(), result.toString(), false);
				case PARTIAL_MATCH:
					if (partialMatchInfo == null) {
						// first partial match may win
						LOGGER.trace("found partial match {}", pattern);
						partialMatchInfo = new ContentInfo(
								result.getMessage(), result.getMimeType(), result.toString(), true
						);
						// continue to look for non-partial
					}
			}
		}
		if (partialMatchInfo == null) {
			LOGGER.trace("returning no match");
			return null;
		} else {
			// returning first partial match
			LOGGER.trace("returning partial match {}", partialMatchInfo);
			return partialMatchInfo;
		}
	}
}
