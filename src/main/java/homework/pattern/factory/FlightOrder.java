package homework.pattern.factory;

public class FlightOrder implements Order {
    public void getOrderType() {
        System.out.println("这是一张机票订单");
    }
}
