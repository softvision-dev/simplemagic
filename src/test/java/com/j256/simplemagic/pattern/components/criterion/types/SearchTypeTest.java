package com.j256.simplemagic.pattern.components.criterion.types;

import static org.junit.Assert.assertEquals;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

import java.io.IOException;

public class SearchTypeTest {

	@Test
	public void testBasicMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 search/5 hello");
		byte[] data = new byte[]{'h', 'e', 'l', 'l', 'o', '2'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testHitMaxOffset() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 search/4 hello");
		byte[] data = new byte[]{'1', 'h', 'e', 'l', 'l', 'o', '2'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testSubLineMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 search/7 hello");
		byte[] data = new byte[]{'1', '2', 'h', 'e', 'l', 'l', 'o', '2', '4'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testSubLineOffsetInfoMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 search/7 hello");
		byte[] data = new byte[]{'1', '2', 'h', 'e', 'l', 'l', 'o', '2', '4'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testNoMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 search/10 hello");
		byte[] data = new byte[]{'1', '2', 'h', 'e', 'l', 'l', '2', '4'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
		// no match after offset
		data = new byte[]{'1', '2', 'h', 'e', '\n', 'l', 'l', '2', '4'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
		// EOF before offset reached
		data = new byte[]{'1', '2', 'h', 'e', '\n', 'l', 'l', '2', '4'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testOptionalWhitespace() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 search/9/b hello %s");
		byte[] data = new byte[]{'1', '2', 'h', 'e', 'l', ' ', 'l', ' ', 'o', ' ', '2', '4'};
		// match on the line started at offset 1
		MatchingResult result = magicPattern.isMatch(data);
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
		assertEquals("hel l o", result.toString());
	}
}
