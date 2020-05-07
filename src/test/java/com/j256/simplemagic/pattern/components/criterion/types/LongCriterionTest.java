package com.j256.simplemagic.pattern.components.criterion.types;

import java.io.IOException;

import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.matching.MatchingState;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongCriterionTest extends BaseMagicTypeTest {

	@Test
	public void testLittleEndianNumber() throws MagicPatternException, IOException {
		MagicPattern pattern = MagicPattern.parse(">0 lequad 0xc3cbc6c5c7b3a1");
		byte[] data = new byte[]{hexToByte("0xa1"), hexToByte("0xb3"), hexToByte("0xc7"),
				hexToByte("0xc5"), hexToByte("0xc6"), hexToByte("0xcb"), hexToByte("0xc3"), 0};
		assertEquals(MatchingState.FULL_MATCH, pattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testBigEndianNumber() throws MagicPatternException, IOException {
		MagicPattern pattern = MagicPattern.parse(">0 bequad 0xc3cbc6c5c7b3a1");
		byte[] data = new byte[]{0, hexToByte("0xc3"), hexToByte("0xcb"),
				hexToByte("0xc6"), hexToByte("0xc5"), hexToByte("0xc7"), hexToByte("0xb3"), hexToByte("0xa1")};
		assertEquals(MatchingState.FULL_MATCH, pattern.isMatch(data).getMatchingState());
	}

	@Test
	public void testEqual() throws IOException {
		String magic = "0 bequad =0 match";
		byte[] bytes = hexToBytes("0000000000000000");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("FFFFFFFFFFFFFFFF");
		testOutput(magic, bytes, null);

		magic = "0 bequad =0xFFFFFFFFFFFFFFFF match";
		bytes = hexToBytes("FFFFFFFFFFFFFFFF");
		testOutput(magic, bytes, "match");

		magic = "0 bequad =0xFFFFFFFFFFFFFFFF match";
		bytes = hexToBytes("0000000000000000");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testNotEqual() throws IOException {
		String magic = "0 bequad !0 match";
		byte[] bytes = hexToBytes("FFFFFFFFFFFFFFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("0000000000000000");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testGreaterThan() throws IOException {
		String magic = "0 bequad >0x7FFFFFFFFFFF0000 match";
		byte[] bytes = hexToBytes("7FFFFFFFFFFFFFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("8000000000000000");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testGreaterThanUnsigned() throws IOException {
		String magic = "0 ubequad >0x7FFFFFFFFFFF0000 match";
		byte[] bytes = hexToBytes("7FFFFFFFFFFFFFFF");
		testOutput(magic, bytes, "match");
	}

	private byte hexToByte(String hex) {
		return Integer.decode(hex).byteValue();
	}
}