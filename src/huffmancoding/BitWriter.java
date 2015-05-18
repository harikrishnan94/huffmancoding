package huffmancoding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BitWriter {
	private long buffer = 0;
	private int currentBit = 0;
	private DataOutputStream os;

	public BitWriter(String file) throws IOException {
		os = new DataOutputStream(new FileOutputStream(file));
	}

	private void commit() throws IOException {
		os.writeInt((int) (buffer & 0xffffffff));
		buffer >>>= 32;
	}

	public void write(int buffer) throws IOException {
		this.buffer = buffer;
		commit();
	}

	public void write(byte buffer) throws IOException {
		os.write(buffer);
	}

	public void write(byte[] buffer) throws IOException {
		os.write(buffer);
	}

	public void write(String codeWord) throws IOException {
		int bits = codeWord.length();
		long code = Long.parseUnsignedLong(codeWord, 2);
		// System.out.print(new StringBuilder(codeWord).reverse().toString());
		// System.out.print(codeWord);
		buffer |= (code << currentBit);
		currentBit += bits;
		if (currentBit > 31) {
			commit();
			currentBit &= 31;
		}
	}

	public void close() throws IOException {
		if (currentBit != 0)
			commit();
		write(currentBit > 0 ? currentBit : 32);
		os.close();
		System.out.println();
	}

	public static void printBits(String file) {
		DataInputStream fis = null;
		try {
			fis = new DataInputStream(new FileInputStream(file));
		} catch (IOException e1) {
			System.err.println("Cannot open file : " + file + " for reading");
		}
		try {
			while (fis.available() / 4 != 0) {
				int buffer = fis.readInt();
				System.out.println(Integer.toBinaryString(buffer));
			}
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}