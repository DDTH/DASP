package ddth.dasp.framework.queue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * JMS implementation of {@link ITextMessageQueue}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class JmsMessageQueue extends AbstractTextMessageQueue {

    private ConnectionFactory connectionFactory;
    private Connection connection;

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public JmsMessageQueue setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }

    public void init() throws Exception {
        connection = connectionFactory.createConnection();
        connection.start();
    }

    public void destroy() throws Exception {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String consumeMessage(String message) throws Exception {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            Destination destination = session.createQueue(getQueueName());
            MessageConsumer consumer = session.createConsumer(destination);
            Message jmsMsg = consumer.receiveNoWait();
            if (jmsMsg instanceof TextMessage) {
                return ((TextMessage) jmsMsg).getText();
            }
            return null;
        } finally {
            session.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String consumeMessage(String message, long timeoutMillisecs) throws Exception {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            Destination destination = session.createQueue(getQueueName());
            MessageConsumer consumer = session.createConsumer(destination);
            Message jmsMsg = consumer.receive(timeoutMillisecs);
            if (jmsMsg instanceof TextMessage) {
                return ((TextMessage) jmsMsg).getText();
            }
            return null;
        } finally {
            session.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean produceMessage(String message) throws Exception {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            Destination destination = session.createQueue(getQueueName());
            MessageProducer producer = session.createProducer(destination);
            producer.setDisableMessageID(true);
            producer.setDisableMessageTimestamp(true);
            TextMessage jmsMsg = session.createTextMessage(message);
            producer.send(jmsMsg);
            return true;
        } finally {
            session.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean produceMessage(String message, long timeoutMillisecs) throws Exception {
        return produceMessage(message);
    }
}
