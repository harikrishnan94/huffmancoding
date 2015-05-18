package huffmancoding;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {

	private final BasicNode basic;
	private String name;
	private int level = 1;
	private String codeWord = "";
	private int codeLength;
	private boolean codeWordsAssigned = false;
	Node left = null;
	Node right = null;
	static int sno = 0;

	public Node(int symbol, int freq, String name) {
		basic = new BasicNode(symbol, freq);
		this.name = name;
	}

	public Node(Node left, Node right, int symbol) {
		basic = new BasicNode(symbol, left.getBasic().freq
				+ right.getBasic().freq);
		this.left = left;
		this.right = right;
		level = left.level + right.level;
	}

	public String getName() {
		return name;
	}

	public String getCodeWord() {
		return codeWord;
	}

	public int getCodeLength() {
		return codeLength;
	}

	@Override
	public int compareTo(Node o) {
		if (getBasic().freq != o.getBasic().freq)
			return getBasic().freq - o.getBasic().freq;
		if (level != o.level)
			return level - o.level;
		return o.getBasic().symbol - getBasic().symbol;
	}

	public static int count(Node node) {
		if (node.left != null && node.right != null)
			return count(node.left) + count(node.right);
		return 1;
	}

	public void assignCodeWord() {
		if (codeWordsAssigned)
			return;
		codeWordsAssigned = true;
		if (left == null || right == null) {
			codeLength = codeWord.length();
			return;
		}
		left.codeWord = codeWord + "1";
		right.codeWord = codeWord + "0";
		left.assignCodeWord();
		right.assignCodeWord();
	}

	public static List<Node> generateNodesFromAsciiString(String str) {
		List<Node> nodes = new ArrayList<>();
		int freq[] = new int[256];
		for (int i = 0; i < str.length(); i++) {
			freq[i]++;
		}
		int symbol = 1;
		for (int i = 0; i < freq.length; i++) {
			if (freq[i] != 0) {
				char ch = (char) i;
				nodes.add(new Node(symbol++, freq[i], Character
						.isWhitespace(ch) ? Character.getName(ch) : String
						.valueOf(ch)));
			}
		}
		return nodes;
	}

	public static List<Node> generateNodesFromFile(String fileName)
			throws IOException {
		List<Node> nodes = new ArrayList<>();
		int freq[] = new int[256];
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(fileName));
			int c;
			while ((c = in.read()) != -1) {
				freq[c]++;
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		for (int i = 0; i < freq.length; i++) {
			if (freq[i] != 0) {
				char ch = (char) i;
				String sym = Character.isWhitespace(ch) ? Character.getName(ch)
						: String.valueOf(ch);
				nodes.add(new Node(i, freq[i], sym));
			}
		}
		return nodes;
	}

	public BasicNode getBasic() {
		return basic;
	}
}
