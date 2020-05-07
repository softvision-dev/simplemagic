package com.j256.simplemagic.endian;

import java.nio.ByteOrder;

/**
 * Creates a {@link EndianConverter}, that is capable of extracting values from binary with a specific endianness.
 */
public class EndianConverterFactory {

	// Static Singleton instances:
	private static BigEndianConverter bigEndianConverter;
	private static LittleEndianConverter littleEndianConverter;
	private static MiddleEndianConverter middleEndianConverter;
	private static EndianConverter nativeEndianConverter;

	/**
	 * Not instantiatable, use static factory method {@link EndianConverterFactory#createEndianConverter(EndianType)}
	 * instead.
	 */
	private EndianConverterFactory() {
	}

	/**
	 * This static factory method produces a {@link EndianConverter}, of extracting values from binary with a specific
	 * endianness.
	 *
	 * @param endianType The endian type, that determines the type of converter, that should be used.
	 * @return A converter fit to extracting values from binary with a specific endianness. (Must not return null)
	 */
	public static EndianConverter createEndianConverter(EndianType endianType) {
		switch (endianType) {
			case BIG:
				return bigEndianConverter == null ?
						bigEndianConverter = new BigEndianConverter() :
						bigEndianConverter;
			case LITTLE:
				return littleEndianConverter == null ?
						littleEndianConverter = new LittleEndianConverter() :
						littleEndianConverter;
			case MIDDLE:
				return middleEndianConverter == null ?
						middleEndianConverter = new MiddleEndianConverter() :
						middleEndianConverter;
			case NATIVE:
			default:
				return nativeEndianConverter == null ?
						nativeEndianConverter = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ?
								new BigEndianConverter() :
								new LittleEndianConverter() :
						nativeEndianConverter;
		}
	}
}
