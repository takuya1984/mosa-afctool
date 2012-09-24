import java.io.UnsupportedEncodingException;

public class Test {
	public static void main(String[] args) {
		try {
			new Test().test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test() throws UnsupportedEncodingException {
		System.out
				.println(MidB(
						new String("abcde芸能界４５あいうえお12345".getBytes("Shift-JIS"),"Shift-JIS"),
						2, 9));
	}

	public static String MidB(String src, int start, int length)
			throws UnsupportedEncodingException {
		byte[] bytes = src.getBytes("Shift-JIS");
		String ret = new String(bytes, start, length, "Shift-JIS");
		return new String(ret.getBytes("UTF-8"), "UTF-8");
	}
}
