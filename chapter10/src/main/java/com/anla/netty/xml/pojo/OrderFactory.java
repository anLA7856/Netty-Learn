package com.anla.netty.xml.pojo;

/**
 * @user anLA7856
 * @time 19-1-26 下午6:02
 * @description
 */
public class OrderFactory {
    public static Order create(long orderID) {
        Order order = new Order();
        order.setOrderNumber(orderID);
        order.setTotal(9999.999f);
        Address address = new Address();
        address.setCity("长沙");
        address.setCountry("中国");
        address.setPostCode("123456");
        address.setState("湖南省");
        address.setStreet1("雨花区");
        order.setBillTo(address);
        Customer customer = new Customer();
        customer.setCustomerNumber(orderID);
        customer.setFirstName("anLA");
        customer.setLastName("7856");
        order.setCustomer(customer);
        order.setShipping(Shipping.INTERNATIONAL_MAIL);
        order.setShipTo(address);
        return order;
    }
}
