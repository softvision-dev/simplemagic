package com.j256.simplemagic.endian;

import org.junit.Test;

import static org.junit.Assert.*;

public class BigEndianConverterTest {

	@Test
	public void testStuff() {
		EndianConverter converter = EndianConverterFactory.createEndianConverter(EndianType.BIG);
		byte[] bytes = new byte[]{10, 127, -100, 0, -128, 1, 62, -62};
		Long result = converter.convertNumber(bytes, 0, 8);
		byte[] outBytes = converter.convertToByteArray(result, 8);
		assertArrayEquals(bytes, outBytes);
		assertNull(converter.convertNumber(bytes, 0, bytes.length + 1));
		assertNull(converter.convertNumber(bytes, -1, bytes.length));
	}

	@Test
	public void testId3() {
		EndianConverter converter = EndianConverterFactory.createEndianConverter(EndianType.BIG);
		long val = converter.convertId3(new byte[]{1, 2, 3, 4}, 0, 4);
		// BADC: 1*2^21 + 2*2^14 + 3*2^7 + 4
		assertEquals(2130308, val);
	}
}
