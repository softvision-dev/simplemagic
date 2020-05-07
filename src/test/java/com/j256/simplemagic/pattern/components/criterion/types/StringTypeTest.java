package com.j256.simplemagic.pattern.components.criterion.types;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingResult;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

import com.j256.simplemagic.ContentInfoUtil;

public class StringTypeTest {

	@Test
	public void testBasicMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string hello");
		byte[] data = new byte[]{'h', 'e', 'l', 'l', 'o', '2'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testBasicNoMatch() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string hello");
		byte[] data = new byte[]{'h', 'e', 'l', 'l'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', 'e', 'l', 'l', 'p'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testOffset() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("3 string hello");
		byte[] data = new byte[]{'w', 'o', 'w', 'h', 'e', 'l', 'l', 'o', '2', '3'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testCaseInsensitive() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string/c hello");
		byte[] data = new byte[]{'h', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		data = new byte[]{'h', 'E', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		magicPattern = MagicPattern.parse("0 string/c Hello");
		data = new byte[]{'H', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'H', 'E', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testOptionalWhitespace() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string/b hello");
		byte[] data = new byte[]{'h', ' ', 'e', ' ', 'l', ' ', 'l', ' ', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'n', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testCompactWhitespace() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string/B h\\ ello");
		byte[] data = new byte[]{'h', ' ', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', ' ', 'e', ' ', 'l', ' ', 'l', ' ', 'o'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', ' ', ' ', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		magicPattern = MagicPattern.parse("0 string/B h\\ e\\ llo");
		data = new byte[]{'h', ' ', ' ', 'e', ' ', ' ', ' ', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', ' ', ' ', 'b', ' ', ' ', ' ', 'l', 'l', 'o'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testCompactWhitespacePlusCaseInsensitive() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string/Bc h\\ ello");
		byte[] data = new byte[]{'h', ' ', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', ' ', ' ', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'H', ' ', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'H', ' ', ' ', 'E', 'L', 'L', 'O'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testCompactPlusOptionalWhitespace() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string/Bb h\\ ello");
		byte[] data = new byte[]{'h', ' ', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', ' ', ' ', 'e', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', ' ', 'e', ' ', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', 'e', ' ', 'l', 'l', 'o'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
		data = new byte[]{'h', ' ', ' ', ' ', 'e', ' ', 'l', 'l', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testRenderValue() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string/Bb h\\ ello %s");
		byte[] data = new byte[]{'h', ' ', 'e', 'l', 'l', 'o'};
		MatchingResult result = magicPattern.isMatch(data);
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
		assertEquals("h ello", result.toString());

		data = new byte[]{'h', ' ', ' ', ' ', 'e', 'l', 'l', 'o'};
		result = magicPattern.isMatch(data);
		assertEquals(MatchingState.FULL_MATCH, result.getMatchingState());
		assertEquals("h   ello", result.toString());
	}

	@Test
	public void testEquals() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string =foo");
		byte[] data = new byte[]{'f', 'o', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		magicPattern = MagicPattern.parse("0 string =f");
		data = new byte[]{'f'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		data = new byte[]{'F'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());

		magicPattern = MagicPattern.parse("0 string/c =f");
		data = new byte[]{'F'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testGreaterThan() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string >\0");
		// really any string
		byte[] data = new byte[]{'\001'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		magicPattern = MagicPattern.parse("0 string >foo");
		data = new byte[]{'f', 'o', 'o', 'l',};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());

		magicPattern = MagicPattern.parse("0 string >foo\0");
		data = new byte[]{'f', 'o', 'o', 'l',};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		magicPattern = MagicPattern.parse("0 string >f");
		data = new byte[]{'g'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		data = new byte[]{'f'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testLessThan() throws MagicPatternException, IOException {
		MagicPattern magicPattern = MagicPattern.parse("0 string <fop");
		byte[] data = new byte[]{'f', 'o', 'o'};
		assertEquals(MatchingState.FULL_MATCH, magicPattern.isMatch(data).getMatchingState());

		magicPattern = MagicPattern.parse("0 string <f");
		data = new byte[]{'f'};
		assertEquals(MatchingState.NO_MATCH, magicPattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testGetStartingBytesInvalid() throws IOException {
		StringReader reader = new StringReader("0  string/b  x\n");
		// getting the starting bytes failed in this example
		new ContentInfoUtil(reader);
	}
}
