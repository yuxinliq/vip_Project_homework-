package homework.pattern.factory.abstractFactory;

import homework.pattern.factory.CarOrder;
import homework.pattern.factory.FlightOrder;
import homework.pattern.factory.HotelOrder;
import homework.pattern.factory.Order;

public class AllOrderFactory implements AbstractFactory {
    public Order getCarOrder() {
        return new CarOrder();
    }

    public Order getFlightOrder() {
        return new FlightOrder();
    }

    public Order getHotelOrder() {
        return new HotelOrder();
    }
}
