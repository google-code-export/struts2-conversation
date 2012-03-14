package org.byars.struts2.actions;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.google.code.struts2.test.junit.StrutsSpringTest;

@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class TestTest extends StrutsSpringTest<BookingController> {

    @Test
    public void randomTest() throws Exception {
        System.out.println("Random Test");
    }
}
