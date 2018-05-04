package homework.pattern.factory.factory;

import homework.pattern.factory.FlightOrder;
import homework.pattern.factory.HotelOrder;
import homework.pattern.factory.Order;

public class HotelOrderFactory implements OrderFactory {
    public Order getOrder() {
        return new HotelOrder();
    }
}
