package homework.pattern.factory.abstractFactory;

import homework.pattern.factory.Order;

public interface AbstractFactory {
    Order getCarOrder();

    Order getFlightOrder();

    Order getHotelOrder();
}
