import java.io.IOException;

public class Decoder {

	public static void main(String[] args) {
		try {
			huffmancoding.Decoder.decode(args[0], args[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
