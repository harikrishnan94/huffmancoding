package huffmancoding;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

class SymbolWriter {
	private final Node tree;
	private final DataOutputStream os;
	private Node temp;

	public SymbolWriter(DataOutputStream os, Node tree) {
		this.tree = tree;
		this.os = os;
		temp = tree;
	}

	public void write(boolean bit) throws IOException {
		if (bit) {
			temp = temp.left;
			if (temp.left == null) {
				os.writeByte(temp.getBasic().symbol);
				temp = tree;
			}
		} else {
			temp = temp.right;
			if (temp.right == null) {
				os.writeByte(temp.getBasic().symbol);
				temp = tree;
			}
		}
	}
}

public class Decoder {
	private static DataInputStream is;
	private static BasicNode[] basics;
	private static SymbolWriter sw;

	public static void decode(String inFile, String outFile) throws IOException {
		is = new DataInputStream(new FileInputStream(inFile));
		basics = getProbTable(is);
		List<Node> nodes = new ArrayList<>(basics.length);
		for (BasicNode basic : basics) {
			char ch = (char) basic.symbol;
			nodes.add(new Node(basic.symbol, basic.freq, Character
					.isWhitespace(ch) ? Character.getName(ch) : String
					.valueOf(ch)));
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
		sw = new SymbolWriter(new DataOutputStream(
				new FileOutputStream(outFile)), last);
		BitReader br = new BitReader(is);
		byte bit;
		// int numBits = 0;
		while ((bit = br.next()) != -1) {
			// System.out.print(bit);
			// numBits++;
			sw.write(bit > 0);
		}
		// System.out.println();
		// System.out.println("Total Bits: " + numBits);
		br.close();
	}

	private static BasicNode[] getProbTable(DataInputStream is)
			throws IOException {
		int length = is.readInt();
		BasicNode[] basics = new BasicNode[length];
		for (int i = 0; i < length; i++) {
			int symbol = is.readInt();
			int freq = is.readInt();
			basics[i] = new BasicNode(symbol, freq);
		}
		return basics;
	}
}
