package homework.pattern.factory.factory;

import homework.pattern.factory.CarOrder;
import homework.pattern.factory.Order;

public class CarOrderFactory implements OrderFactory {
    public Order getOrder() {
        return new CarOrder();
    }
}
