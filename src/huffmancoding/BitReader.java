package huffmancoding;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class BitReader {
	private byte[] buffer = new byte[4 * 4 * 8];
	private final DataInputStream is;
	private int currentBit = 0;
	private int validBits = 0;
	private boolean EOF = false;

	public BitReader(String inFile) throws IOException {
		is = new DataInputStream(new FileInputStream(inFile));
		Arrays.fill(buffer, validBits, buffer.length, (byte) -1);
		loadBuffer();
	}

	public BitReader(DataInputStream is) throws IOException {
		this.is = is;
		Arrays.fill(buffer, validBits, buffer.length, (byte) -1);
		loadBuffer();
	}

	public boolean skip(int numBits) throws IOException {
		currentBit += numBits;
		if (currentBit < validBits) {
			loadBuffer();
			if (EOF) {
				return false;
			}
		}
		return true;
	}

	public void close() throws IOException {
		is.close();
	}

	private void storeBits(int[] temp) {
		int numBits = validBits;
		for (int i = 0; i < temp.length && numBits > 0; i++) {
			for (int j = 0; j < 32 && numBits > 0; j++, numBits--, temp[i] >>>= 1) {
				buffer[i * 32 + j] = (byte) (temp[i] & 1);
			}
		}
		Arrays.fill(buffer, validBits, buffer.length, (byte) -1);
	}

	private boolean loadBuffer() throws IOException {
		if (currentBit < validBits) {
			return true;
		}
		if (is.available() < 2 * 4) {
			EOF = true;
			return false;
		}
		currentBit = 0;
		int[] temp;
		if (is.available() < (buffer.length >>> 3) + 4 * 2) {
			temp = new int[is.available() / 4];
			for (int i = 0; i < temp.length; i++)
				temp[i] = is.readInt();
			validBits = (temp.length - 2) * 32 + temp[temp.length - 1];
		} else {
			temp = new int[4];
			for (int i = 0; i < temp.length; i++)
				temp[i] = is.readInt();
			validBits = temp.length * 32;
		}
		storeBits(temp);
		return true;
	}

	public byte next() throws IOException {
		if (loadBuffer()) {
			return buffer[currentBit++];
		} else {
			return buffer[currentBit];
		}
	}
}
