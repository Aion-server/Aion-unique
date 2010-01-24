/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network;

import java.nio.ByteBuffer;

import com.aionemu.commons.utils.Rnd;

/**
 * Crypt will encrypt server packet and decrypt client packet.
 * 
 * @author hack99
 * @author -Nemesiss-
 */
public class Crypt
{
	/**
	 * Second byte of client packet must be equal to this
	 */
	public final static byte	staticClientPacketCode	= 0x54;
	/**
	 * Second byte of server packet must be equal to this
	 */
	public final static byte	staticServerPacketCode	= 0x55;// 1.5.x (0x54 works too)
	/**
	 * Static xor key
	 */
	private static byte[]		staticKey				= "nKO/WctQ0AVLbpzfBkS6NevDYT8ourG5CRlmdjyJ72aswx4EPq1UgZhFMXH?3iI9"
															.getBytes();
	/**
	 * Current xor key for client packet decoding.
	 */
	private byte[]				clientPacketKey;
	/**
	 * Current xor key for server packet encoding.
	 */
	private byte[]				serverPacketKey;
	/**
	 * Crypt is enabled after first server packet was send.
	 */
	private boolean				isEnabled;

	/**
	 * Enable crypt key - generate random key that will be used to encrypt second server packet [first one is
	 * unencrypted] and decrypt client packets. This method is called from SM_KEY server packet, that packet sends key
	 * to aion client.
	 * 
	 * @return "false key" that should by used by aion client to encrypt/decrypt packets.
	 */
	public final int enableKey()
	{
		if(clientPacketKey != null)
			throw new KeyAlreadySetException();

		/** rnd key - this will be used to encrypt/decrypt packet */
		int key = Rnd.nextInt();

		clientPacketKey = new byte[] { (byte) (key & 0xff), (byte) ((key >> 8) & 0xff), (byte) ((key >> 16) & 0xff),
			(byte) ((key >> 24) & 0xff), (byte) 0xa1, (byte) 0x6c, (byte) 0x54, (byte) 0x87 };

		serverPacketKey = new byte[clientPacketKey.length];
		System.arraycopy(clientPacketKey, 0, serverPacketKey, 0, clientPacketKey.length);

		/** false key that will be sent to aion client in SM_KEY packet */
		return (key ^ 0xCD92E451) + 0x3FF2CC87;
	}

	/**
	 * Decrypt client packet from this ByteBuffer.
	 * 
	 * @param buf
	 * @return true if decryption was successful.
	 */
	public final boolean decrypt(ByteBuffer buf)
	{
		if(!isEnabled)
			return false;

		final byte[] data = buf.array();
		final int size = buf.remaining();

		/** index to byte that should be decrypted now */
		int arrayIndex = buf.arrayOffset() + buf.position();

		/** prev encrypted byte */
		int prev = data[arrayIndex];

		/** decrypt first byte */
		data[arrayIndex++] ^= (clientPacketKey[0] & 0xff);

		/** decrypt loop */
		for(int i = 1; i < size; i++, arrayIndex++)
		{
			int curr = data[arrayIndex] & 0xff;
			data[arrayIndex] ^= (staticKey[i & 63] & 0xff) ^ (clientPacketKey[i & 7] & 0xff) ^ prev;
			prev = curr;
		}

		/** oldKey value as long */
		long oldKey = (((long) clientPacketKey[0] & 0xff) << 0) | (((long) clientPacketKey[1] & 0xff) << 8)
			| (((long) clientPacketKey[2] & 0xff) << 16) | (((long) clientPacketKey[3] & 0xff) << 24)
			| (((long) clientPacketKey[4] & 0xff) << 32) | (((long) clientPacketKey[5] & 0xff) << 40)
			| (((long) clientPacketKey[6] & 0xff) << 48) | (((long) clientPacketKey[7] & 0xff) << 56);

		/** change key */
		oldKey += size;

		/** set key new value */
		clientPacketKey[0] = (byte) (oldKey >> 0 & 0xff);
		clientPacketKey[1] = (byte) (oldKey >> 8 & 0xff);
		clientPacketKey[2] = (byte) (oldKey >> 16 & 0xff);
		clientPacketKey[3] = (byte) (oldKey >> 24 & 0xff);
		clientPacketKey[4] = (byte) (oldKey >> 32 & 0xff);
		clientPacketKey[5] = (byte) (oldKey >> 40 & 0xff);
		clientPacketKey[6] = (byte) (oldKey >> 48 & 0xff);
		clientPacketKey[7] = (byte) (oldKey >> 56 & 0xff);

		return validateClientPacket(buf);
	}

	/**
	 * Check if packet was correctly decoded, also check if packet was correctly coded by aion client.
	 * 
	 * @param buf
	 * @return true if packet is correctly decoded
	 */
	private final boolean validateClientPacket(ByteBuffer buf)
	{
		return buf.get(0) == ~buf.get(2) && buf.get(1) == staticClientPacketCode;
	}

	/**
	 * Encrypt server packet from this ByteBuffer.
	 * 
	 * @param buf
	 */
	public final void encrypt(ByteBuffer buf)
	{
		if(!isEnabled)
		{
			/** first packet is not encrypted */
			isEnabled = true;
			return;
		}

		final byte[] data = buf.array();
		final int size = buf.remaining();

		/** index to byte that should be encrypted now */
		int arrayIndex = buf.arrayOffset() + buf.position();

		/** encrypt first byte */
		data[arrayIndex] ^= (serverPacketKey[0] & 0xff);

		/** prev encrypted byte */
		int prev = data[arrayIndex++];

		/** encrypt loop */
		for(int i = 1; i < size; i++, arrayIndex++)
		{
			data[arrayIndex] ^= (staticKey[i & 63] & 0xff) ^ (serverPacketKey[i & 7] & 0xff) ^ prev;
			prev = data[arrayIndex];
		}

		/** oldKey value as long */
		long oldKey = (((long) serverPacketKey[0] & 0xff) << 0) | (((long) serverPacketKey[1] & 0xff) << 8)
			| (((long) serverPacketKey[2] & 0xff) << 16) | (((long) serverPacketKey[3] & 0xff) << 24)
			| (((long) serverPacketKey[4] & 0xff) << 32) | (((long) serverPacketKey[5] & 0xff) << 40)
			| (((long) serverPacketKey[6] & 0xff) << 48) | (((long) serverPacketKey[7] & 0xff) << 56);

		/** change key */
		oldKey += size;

		/** set key new value */
		serverPacketKey[0] = (byte) (oldKey >> 0 & 0xff);
		serverPacketKey[1] = (byte) (oldKey >> 8 & 0xff);
		serverPacketKey[2] = (byte) (oldKey >> 16 & 0xff);
		serverPacketKey[3] = (byte) (oldKey >> 24 & 0xff);
		serverPacketKey[4] = (byte) (oldKey >> 32 & 0xff);
		serverPacketKey[5] = (byte) (oldKey >> 40 & 0xff);
		serverPacketKey[6] = (byte) (oldKey >> 48 & 0xff);
		serverPacketKey[7] = (byte) (oldKey >> 56 & 0xff);
	}

	/**
	 * Server packet opcodec obfuscation.
	 * 
	 * @param op
	 * @return obfuscated opcodec
	 */
	public static final byte encodeOpcodec(int op)
	{
		return (byte) ((op + 0xAE) ^ 0xEE);
	}
}
