package com.j256.simplemagic.endian;

import org.junit.Test;

import static org.junit.Assert.*;

public class MiddleEndianConverterTest {

	@Test
	public void testBasic() {
		EndianConverter converter = EndianConverterFactory.createEndianConverter(EndianType.MIDDLE);
		byte[] bytes = new byte[]{1, 2, 3, 4};
		long val = converter.convertNumber(bytes, 0, 4);
		// BADC: 2*2^24 + 1*2^16 + 4*2^8 + 3
		assertEquals(33620995, val);
		byte[] outBytes = converter.convertToByteArray(val, 4);
		assertArrayEquals(bytes, outBytes);
		assertNull(converter.convertNumber(bytes, -1, 4));
	}

	@Test
	public void testId3() {
		EndianConverter converter = EndianConverterFactory.createEndianConverter(EndianType.MIDDLE);
		long val = converter.convertId3(new byte[]{1, 2, 3, 4}, 0, 4);
		// BADC: 2*2^21 + 1*2^14 + 4*2^7 + 3
		assertEquals(4211203, val);

		// shift over to test offset
		val = converter.convertId3(new byte[]{0, 0, 1, 2, 3, 4}, 2, 4);
		// BADC: 2*2^21 + 1*2^14 + 4*2^7 + 3
		assertEquals(4211203, val);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testInvalidLength() {
		EndianConverterFactory.createEndianConverter(EndianType.MIDDLE).convertNumber(new byte[0], 0, 2);
	}

	@Test
	public void testOutOfBytes() {
		assertNull(EndianConverterFactory.createEndianConverter(EndianType.MIDDLE).convertNumber(new byte[0], 0, 4));
	}

	@Test
	public void testWrongLength() {
		EndianConverterFactory.createEndianConverter(EndianType.MIDDLE).convertToByteArray(0, 2);
	}
}
