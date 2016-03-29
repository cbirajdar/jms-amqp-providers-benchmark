package demo.benchmark;

import demo.benchmark.logging.Loggable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;

abstract class AbstractMessageBroker implements Loggable {

    Connection connection;

    Queue queue;

    Session session;

    final String QUEUE = "TestQueue";

    final Integer enqueue_count = Integer.valueOf(System.getProperty("enqueue_count"));

    public abstract void createConnection(String port) throws Exception;

    public void enqueue() throws Exception {
        long startTime = System.currentTimeMillis();
        MessageProducer messageProducer = session.createProducer(queue);
        for (int i = 0; i < enqueue_count; i++) {
            messageProducer.send(session.createTextMessage(String.valueOf(i)));
        }
        long endTime = System.currentTimeMillis();
        log().info("******** Time to Enqueue: {} ********", endTime - startTime);
    }

    public void dequeue() throws Exception {
        long startTime = System.currentTimeMillis();
        MessageConsumer messageConsumer = session.createConsumer(queue);
        for (int i = 0; i < enqueue_count; i++) {
            messageConsumer.receive();
        }
        long endTime = System.currentTimeMillis();
        log().info("******** Time to Dequeue: {} ********", endTime - startTime);
    }

    public void closeConnection() throws Exception {
        log().info("Closing connection....");
        connection.close();
    }
}
