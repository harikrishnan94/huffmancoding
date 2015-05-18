import java.io.IOException;

import huffmancoding.Decoder;
import huffmancoding.Encoder;

public class HuffmanCodingTester {
	public static void main(String[] args) throws IOException {
		Encoder.encode("/home/hari/workspace-java/HuffmanCoding/src/in.txt",
				"/home/hari/workspace-java/HuffmanCoding/src/out.comp");
		Decoder.decode("/home/hari/workspace-java/HuffmanCoding/src/out.comp",
				"/home/hari/workspace-java/HuffmanCoding/src/out.txt");
	}
}
