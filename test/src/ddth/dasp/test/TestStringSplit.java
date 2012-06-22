package ddth.dasp.test;

public class TestStringSplit {
	public static void main(String[] args) {
		String s = "1;2;3;;;abc;;;1";
		String[] tokens = s.split(";");
		System.out.println(tokens.length);
	}
}
