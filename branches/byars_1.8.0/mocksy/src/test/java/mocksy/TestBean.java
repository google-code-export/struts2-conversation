package mocksy;

public class TestBean {
	protected String field = "original";
	public TestBean(){}
	public void test() {
		this.pTest();
	}
	private void pTest() {
		System.out.println("sup");
	}
}
