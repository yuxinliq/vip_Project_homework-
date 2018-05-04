package homework.pattern.proxy;

import homework.pattern.proxy.customerized.CustomerizedProxy;
import homework.pattern.proxy.customerized.CustomerizedScalper;
import homework.pattern.proxy.jdk.JDKScalper;
import org.junit.Test;

public class ProxyTest {

    @Test
    public void testJdk() {
        JDKScalper.getInstance(new EasternAirlines()).sellTickets();
    }

    @Test
    public void testCustomized() {
        CustomerizedScalper.getInstance(new EasternAirlines()).sellTickets();
    }
}
