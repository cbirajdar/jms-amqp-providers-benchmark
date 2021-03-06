package demo.benchmark;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class RabbitMqMessageBroker extends AbstractMessageBroker {

    private Connection connection;

    private Channel channel;

    RabbitMqMessageBroker(String port) throws Exception {
        createConnection(port);
    }

    @Override public void createConnection(String port) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE, false, false, false, null);
    }

    @Override public void enqueue() {
        submit(producerThreads, () -> stream(enqueueCount, i -> publish(payload), "Enqueue"));
    }

    private void publish(String data) {
        try {
            channel.basicPublish("", QUEUE, null, String.valueOf(data).getBytes());
        } catch (IOException e) {
            log().error("Error publishing data to the queue", e);
        }
    }

    @Override public void dequeue() throws IOException {
        Consumer consumer = new DefaultConsumer(channel);
        submit(consumerThreads, () -> stream(enqueueCount, i -> consume(consumer), "Dequeue"));
    }

    private void consume(Consumer consumer) {
        try {
            channel.basicConsume(QUEUE, true, consumer);
        } catch (IOException e) {
            log().error("Error consuming data from the queue", e);
        }
    }

    @Override public void closeConnection() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
