package com.anla.netty.xml;

import com.anla.netty.xml.pojo.Order;
import com.anla.netty.xml.pojo.OrderFactory;
import org.jibx.runtime.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @user anLA7856
 * @time 19-1-26 下午11:37
 * @description
 */
public class TestOrder {
    private IBindingFactory factory = null;
    private StringWriter writer = null;
    private StringReader reader = null;
    private final static String CHARSET_NAME = "UTF-8";

    private String encode2Xml(Order order) throws JiBXException, IOException {
        factory = BindingDirectory.getFactory(Order.class);
        writer = new StringWriter();
        IMarshallingContext mctx = factory.createMarshallingContext();
        mctx.setIndent(2);
        mctx.marshalDocument(order, CHARSET_NAME, null, writer);
        String xmlStr = writer.toString();
        writer.close();
        System.out.println(xmlStr.toString());
        return xmlStr;
    }

    private Order decode2Order(String xmlBody) throws JiBXException {
        reader = new StringReader(xmlBody);
        IUnmarshallingContext uctx = factory.createUnmarshallingContext();
        Order order = (Order) uctx.unmarshalDocument(reader);
        return order;
    }

    public static void main(String[] args) throws JiBXException, IOException {
        TestOrder test = new TestOrder();
        Order order = OrderFactory.create(123);
        String body = test.encode2Xml(order);
        Order order2 = test.decode2Order(body);
        System.out.println(order2);

    }
}
