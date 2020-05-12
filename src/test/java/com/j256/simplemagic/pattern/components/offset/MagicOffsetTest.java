package com.j256.simplemagic.pattern.components.offset;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.error.MagicPatternException;
import com.j256.simplemagic.pattern.MagicPattern;
import com.j256.simplemagic.pattern.components.MagicOffset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MagicOffsetTest {

	private MagicOffset assertNoException(String rawDefinition) throws MagicPatternException {
		return MagicOffset.parse(rawDefinition);
	}

	private void assertException(String rawDefinition, String expectedMessage) {
		MagicPatternException exception = null;
		// no previous line
		try {
			MagicPattern.parse(rawDefinition);
		} catch (MagicPatternException ex) {
			assertEquals(expectedMessage, ex.getMessage());
			exception = ex;
		}
		assertNotNull(exception);
	}

	@Test
	public void testOffsets() throws MagicPatternException {
		offsetAssertions(assertNoException("&(0xef.i-0xff)"),
				true, true, false, 0, 239, EndianType.LITTLE, 4,
				true, MagicOffsetOperator.SUBTRACT, false, 255, null,
				4, false);
		offsetAssertions(assertNoException("&0xef"),
				true, false, false, 239, 0, null, 2,
				false, null, false, 0, null,
				1, false);
		offsetAssertions(assertNoException("&(0xef.b)"),
				true, true, false, 0, 239, EndianType.LITTLE, 1,
				false, null, false, 0, null,
				4, false);
		offsetAssertions(assertNoException("(0xef.s+0xff)"),
				false, true, false, 0, 239, EndianType.LITTLE, 2,
				false, MagicOffsetOperator.ADD, false, 255, null,
				4, false);
		offsetAssertions(assertNoException("&(0xef.l*0xff)"),
				true, true, false, 0, 239, EndianType.LITTLE, 4,
				false, MagicOffsetOperator.MULTIPLY, false, 255, null,
				4, false);
		offsetAssertions(assertNoException("&(0xef.m/0xff)"),
				true, true, false, 0, 239, EndianType.MIDDLE, 4,
				false, MagicOffsetOperator.DIVIDE, false, 255, null,
				4, false);
		offsetAssertions(assertNoException("&(0xef.B&0xff)"),
				true, true, false, 0, 239, EndianType.BIG, 1,
				false, MagicOffsetOperator.AND, false, 255, null,
				4, false);
		offsetAssertions(assertNoException("&(0xef.S|0xff)"),
				true, true, false, 0, 239, EndianType.BIG, 2,
				false, MagicOffsetOperator.OR, false, 255, null,
				4, false);
		offsetAssertions(assertNoException("&(0xef.I^0xff)"),
				true, true, false, 0, 239, EndianType.BIG, 4,
				true, MagicOffsetOperator.XOR, false, 255, null,
				4, false);
		offsetAssertions(assertNoException("&(&0xef.L+(-0x2))"),
				true, true, true, 0, 239, EndianType.BIG, 4,
				false, MagicOffsetOperator.ADD, true, -2, EndianType.LITTLE,
				4, false);
		offsetAssertions(assertNoException("-0xef"),
				false, false, false, -239, 0, null, 4,
				false, null, false, 0, null,
				4, false);
		offsetAssertions(assertNoException("&(&-0xef.L+(-0x2))"),
				true, true, true, 0, -239, EndianType.BIG, 4,
				false, MagicOffsetOperator.ADD, true, -2, EndianType.LITTLE,
				4, false);
		offsetAssertions(assertNoException("&(&-0xef.L+(-0x2.i))"),
				true, true, true, 0, -239, EndianType.BIG, 4,
				false, MagicOffsetOperator.ADD, true, -2, EndianType.LITTLE,
				4, true);
		offsetAssertions(assertNoException("&(&-0xef.L+(-0x2.B))"),
				true, true, true, 0, -239, EndianType.BIG, 4,
				false, MagicOffsetOperator.ADD, true, -2, EndianType.BIG,
				1, false);
		offsetAssertions(assertNoException("&(&-0xef.L*-0x2)"),
				true, true, true, 0, -239, EndianType.BIG, 4,
				false, MagicOffsetOperator.MULTIPLY, false, -2, null,
				4, false);
	}

	public void offsetAssertions(MagicOffset magicOffset,
			boolean relative, boolean indirect, boolean indirectOffsetRelative, long baseOffset, long indirectOffset,
			EndianType offsetEndianType, int offsetByteLength, boolean readID3length, MagicOffsetOperator offsetOperator,
			boolean operandIndirect, long operand, EndianType operandEndianType, int operandByteLength,
			boolean operandReadID3length) {
		assertEquals("Different offset base value expected.",
				baseOffset, magicOffset.getBaseOffset());
		assertEquals("Relative offset expected.",
				relative, magicOffset.isRelative());
		assertEquals("Indirect offset expected.",
				indirect, magicOffset.isIndirect());
		if (indirect) {
			assertNotNull("Indirect offset expected.",
					magicOffset.getIndirectOffset());
			assertEquals("Indirect offset expected.",
					indirectOffset, magicOffset.getIndirectOffset().getOffset());
			assertEquals("Relative indirect offset expected.",
					indirectOffsetRelative, magicOffset.getIndirectOffset().isRelative());
			if (offsetOperator != null) {
				assertNotNull("Offset operand expected.",
						magicOffset.getIndirectOffset().getOffsetModification());
				assertEquals("Different offset operator expected.",
						offsetOperator, magicOffset.getIndirectOffset().getOffsetModification().getOperator());
				assertEquals("Indirect operand expected.",
						operandIndirect, magicOffset.getIndirectOffset().getOffsetModification().isOperandIndirect());
				assertEquals("Different indirect operand expected.",
						operand, magicOffset.getIndirectOffset().getOffsetModification().getOperand());
				if (operandEndianType != null) {
					assertNotNull("Indirect offset operand read type expected",
							magicOffset.getIndirectOffset().getOffsetModification().getOperandReadType());
					assertEquals("Different operand endianType expected.",
							operandEndianType, magicOffset.getIndirectOffset().getOffsetModification().getOperandReadType()
									.getEndianType());
					assertEquals("Different operand byte length expected.",
							operandByteLength, magicOffset.getIndirectOffset().getOffsetModification().getOperandReadType()
									.getValueByteLength());
					assertEquals("Operand id3length expected.",
							operandReadID3length, magicOffset.getIndirectOffset().getOffsetModification().getOperandReadType()
									.isReadID3Length());
				}
			}
			if (offsetEndianType != null) {
				assertNotNull("Indirect offset read type expected.",
						magicOffset.getIndirectOffset().getOffsetReadType());
				assertEquals("Different endianness expected.",
						offsetEndianType, magicOffset.getIndirectOffset().getOffsetReadType().getEndianType());
				assertEquals("Different offset byte length expected.",
						offsetByteLength, magicOffset.getIndirectOffset().getOffsetReadType().getValueByteLength());
				assertEquals("ID3length expected.",
						readID3length, magicOffset.getIndirectOffset().getOffsetReadType().isReadID3Length());
			}
		}
	}
}