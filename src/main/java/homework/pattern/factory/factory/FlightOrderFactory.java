package homework.pattern.factory.factory;

import homework.pattern.factory.FlightOrder;
import homework.pattern.factory.Order;

public class FlightOrderFactory implements OrderFactory {
    public Order getOrder() {
        return new FlightOrder();
    }
}
