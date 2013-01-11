package mocksy;

import mocksy.core.Mock;
import mocksy.core.Verify;

import org.junit.Test;
import static mocksy.Mocksy.*;
import static org.junit.Assert.assertEquals;

public class MocksyTest {
	
	TestBean bean = mockify(new TestBean());
	
	@Test
	public void test() {
		
		delegate(bean).to(new Mock<TestBean>() {
			
			{
				System.out.println("s");
			}
			
			@Verify(calls = 1)
			void test() {
				System.out.println("wha?");
			}
			
		}).test();
		
		verify(bean);
		
		assertEquals("original", get(String.class).from(bean));
		
		set("new").on(bean);
		
		assertEquals("new", get(String.class).from(bean));
		
		remove(bean);
	}

}
