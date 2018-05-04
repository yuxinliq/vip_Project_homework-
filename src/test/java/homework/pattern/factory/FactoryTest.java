package homework.pattern.factory;

import homework.pattern.factory.abstractFactory.AbstractFactory;
import homework.pattern.factory.abstractFactory.AllOrderFactory;
import homework.pattern.factory.factory.CarOrderFactory;
import homework.pattern.factory.factory.OrderFactory;
import org.junit.Test;

public class FactoryTest {
    @Test
    public void testFactoryPattern() {
        OrderFactory factory = new CarOrderFactory();
        Order order = factory.getOrder();
        System.out.println(order);
        order.getOrderType();
    }

    @Test
    public void testAbstractFactoryPattern() {
        AbstractFactory factory = new AllOrderFactory();
        Order order = factory.getCarOrder();
        System.out.println(order);
        order.getOrderType();
    }
}
