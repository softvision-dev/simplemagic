package com.j256.simplemagic.pattern.components.operation.criterion.types;

import static org.junit.Assert.assertEquals;

import com.j256.simplemagic.MagicEntries;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

import java.io.IOException;

public class RegexCriterionTest {

	@Test
	public void testBasic() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse(">0 regex hello[abc] %s");
		byte[] data = "some line with helloa in it".getBytes();
		MatchingResult result = magicPattern.isMatch(data, 15, new MagicEntries());
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
		assertEquals("helloa", result.toString());
	}

	@Test
	public void testCaseInsensitive() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse(">0 regex/c hello[ABC] %s");
		byte[] data = "some line with helloa in it".getBytes();
		MatchingResult result = magicPattern.isMatch(data, 15, new MagicEntries());
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
		assertEquals("helloa", result.toString());
	}

	@Test
	public void testSlashes() throws MagicPatternException, IOException {
		/*
		 * \\xB is decimal 11 which is octal 013. The 8 backslashes doesn't seem right but if the Java string does one
		 * level of \, magic file does another, then you need 2 x 2 x 2 == 8.
		 */
		MagicPattern magicPattern = MagicPattern.parse(">0 regex/c hrm\\t\\0\\xB\\\\\\\\wow %s");
		byte[] data = "some line with hrm\t\0\13\\wow in it".getBytes();
		MatchingResult result = magicPattern.isMatch(data, 15, new MagicEntries());
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
		assertEquals("hrm\t\0\13\\wow", result.toString());
	}
}