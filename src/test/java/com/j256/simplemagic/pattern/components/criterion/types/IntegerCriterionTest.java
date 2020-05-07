package com.j256.simplemagic.pattern.components.criterion.types;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

public class IntegerCriterionTest extends BaseMagicTypeTest {

	@Test
	public void testLittleEndianNumber() throws MagicPatternException, IOException {
		MagicPattern pattern = MagicPattern.parse("0 lelong 0x03cbc6c5");
		assertEquals(MatchingState.FULL_MATCH, pattern.isMatch(hexToBytes("c5c6cb03")).getMatchingState());
	}

	@Test
	public void testBigEndianNumber() throws MagicPatternException, IOException {
		MagicPattern pattern = MagicPattern.parse("0 belong 0x03c7b3a1");
		assertEquals(MatchingState.FULL_MATCH, pattern.isMatch(hexToBytes("03c7b3a1")).getMatchingState());
	}

	@Test
	public void testUnsignedGreaterThan() throws IOException {
		String magic = "0 ubelong >0xF0000000 match";
		byte[] bytes = hexToBytes("FFFFFFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("F0000001");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("EFFFFFFF");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("AAAAAAAA");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("80000000");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("7FFFFFFF");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("00000000");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testSignedGreaterThan1() throws IOException {
		String magic = "0 belong >0xF0000000 match";

		byte[] bytes = hexToBytes("7FFFFFFF");
		testOutput(magic, bytes, "match");
	}

	@Test
	public void testSignedGreaterThan() throws IOException {
		String magic = "0 belong >0xF0000000 match";

		// higher always
		byte[] bytes = hexToBytes("F0000001");
		testOutput(magic, bytes, "match");
		bytes = hexToBytes("FFFFFFFF");
		testOutput(magic, bytes, "match");

		// Higher bc of two's complement
		bytes = hexToBytes("00000000");
		testOutput(magic, bytes, "match");
		bytes = hexToBytes("7FFFFFFF");
		testOutput(magic, bytes, "match");

		// lower always
		bytes = hexToBytes("EFFFFFFF");
		testOutput(magic, bytes, null);
		bytes = hexToBytes("AAAAAAAA");
		testOutput(magic, bytes, null);
		bytes = hexToBytes("80000000");
		testOutput(magic, bytes, null);
	}
}