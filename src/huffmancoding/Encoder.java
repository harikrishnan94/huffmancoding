package huffmancoding;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

class FileWriter {
	private final String outFile;
	private final HashMap<Integer, String> codeWords;
	private final BasicNode[] basics;
	private BitWriter bw;

	public FileWriter(String outFile, BasicNode[] basics,
			HashMap<Integer, String> codeWords) {
		this.outFile = outFile;
		this.codeWords = codeWords;
		this.basics = basics;
		try {
			bw = new BitWriter(outFile);
		} catch (IOException e) {
			System.err.println("Cannot open " + outFile + " for writing");
		}
	}

	public void write(String srcFile) {
		try {
			DataInputStream is = new DataInputStream(new FileInputStream(
					srcFile));
			while (is.available() > 0) {
				int symbol = (is.readByte() & 0xff);
				String code = codeWords.get(symbol);
				// if (code != null)
				bw.write(new StringBuilder(code).reverse().toString());
				// else
				// System.out.println(symbol + " " + (char) symbol);
				// System.out.println(code);
			}
			is.close();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeProbTable() {
		try {
			bw.write(basics.length);
			for (BasicNode basic : basics) {
				bw.write(basic.symbol);
				bw.write(basic.freq);
			}
		} catch (IOException e) {
			System.err.println("Cannot write to " + outFile);
		}
	}
}

public class Encoder {
	private static HashMap<Integer, String> codeWords;
	private static BasicNode[] basics;
	private static List<Node> nodes = null;

	public static void encode(String inFile, String outFile) {
		try {
			nodes = Node.generateNodesFromFile(inFile);
		} catch (IOException e) {
			System.err.println("Cannot open " + inFile + " for reading");
		}
		SortedSet<Node> tree = new TreeSet<>(nodes);
		int symbol = nodes.get(nodes.size() - 1).getBasic().symbol;
		Node left, right;
		while (tree.size() != 1) {
			left = tree.first();
			tree.remove(left);
			right = tree.first();
			tree.remove(right);
			tree.add(new Node(left, right, ++symbol));
		}
		Node last = tree.first();
		tree.clear();
		last.assignCodeWord();
		codeWords = new HashMap<>(nodes.size());
		basics = new BasicNode[nodes.size()];
		// int bitsRequired = 0;
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			basics[i] = node.getBasic();
			String code = node.getCodeWord();
			// System.out.printf(
			// "Symbol:%s\tFrequency:%d\tCodeWord:%s\tCodelength:%d\n",
			// node.getName(), node.getBasic().freq, code, code.length());
			// bitsRequired += node.getBasic().freq * code.length();
			codeWords.put(basics[i].symbol, code);
		}
		// System.out.println("Total Bits Required:" + bitsRequired);
		FileWriter fw = new FileWriter(outFile, basics, codeWords);
		fw.writeProbTable();
		fw.write(inFile);
	}
}
