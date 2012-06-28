package ddth.dasp.temp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;

public class ChatServlet extends HttpServlet implements CometProcessor {

    private static final long serialVersionUID = 1L;

    protected List<HttpServletResponse> connections = new ArrayList<HttpServletResponse>();
    protected MessageSender messageSender = null;
    protected Thread randomMsg;

    public void init() throws ServletException {
        messageSender = new MessageSender();
        Thread messageSenderThread = new Thread(messageSender, "MessageSender["
                + getServletContext().getContextPath() + "]");
        messageSenderThread.setDaemon(true);
        messageSenderThread.start();

        randomMsg = new Thread() {

            private Random rand = new Random();
            private long counter = 0;

            public void run() {
                while (!isInterrupted()) {
                    messageSender.send("test", "Message: " + counter);
                    counter++;
                    try {
                        Thread.sleep(Math.abs(rand.nextInt() % 10000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        randomMsg.setDaemon(true);
        randomMsg.start();
    }

    public void destroy() {
        messageSender.stop();
        messageSender = null;

        randomMsg.interrupt();
        randomMsg = null;

        connections.clear();
    }

    private void writeData(HttpServletResponse response, String data) throws IOException {
        response.getWriter().print(data);
        response.getWriter().flush();
        // response.getWriter().close();
        // response.getOutputStream().print(data);
        // response.getOutputStream().flush();
        // response.flushBuffer();
    }

    @Override
    public void event(CometEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getHttpServletRequest();
        HttpServletResponse response = event.getHttpServletResponse();
        if (event.getEventType() == CometEvent.EventType.BEGIN) {
            event.setTimeout(24 * 3600);
            log("Begin for session: " + request.getSession(true).getId());
            String msg = "<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">";
            msg += "<head><title>JSP Chat</title></head><body bgcolor=\"#FFFFFF\">";
            msg += "<div id=\"msg\"></div>";
            response.setBufferSize(0);
            writeData(response, msg);
            synchronized (connections) {
                connections.add(response);
            }
        } else if (event.getEventType() == CometEvent.EventType.ERROR) {
            log("Error [" + event.getEventSubType() + "] for session: "
                    + request.getSession(true).getId());
            synchronized (connections) {
                connections.remove(response);
            }
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.END) {
            log("End for session: " + request.getSession(true).getId());
            synchronized (connections) {
                connections.remove(response);
            }
            String msg = "</body></html>";
            writeData(response, msg);
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.READ) {
            InputStream is = request.getInputStream();
            byte[] buf = new byte[512];
            do {
                int n = is.read(buf); // can throw an IOException
                if (n > 0) {
                    log("Read " + n + " bytes: " + new String(buf, 0, n) + " for session: "
                            + request.getSession(true).getId());
                } else if (n < 0) {
                    // error(event, request, response);
                    return;
                }
            } while (is.available() > 0);
        }

    }

    public class MessageSender implements Runnable {

        protected boolean running = true;
        protected ArrayList<String> messages = new ArrayList<String>();

        public MessageSender() {
        }

        public void stop() {
            running = false;
        }

        /**
         * Add message for sending.
         */
        public void send(String user, String message) {
            String msg = "[" + user + "]: " + message;
            log("Sending message: " + msg);
            synchronized (messages) {
                messages.add(msg);
                messages.notify();
            }
        }

        public void run() {

            while (running) {

                if (messages.size() == 0) {
                    try {
                        synchronized (messages) {
                            messages.wait();
                        }
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }

                synchronized (connections) {
                    String[] pendingMessages = null;
                    synchronized (messages) {
                        pendingMessages = messages.toArray(new String[0]);
                        messages.clear();
                    }
                    log("Sending " + pendingMessages.length + " messages to " + connections.size()
                            + " clients.");
                    // Send any pending message on all the open connections
                    for (HttpServletResponse response : connections) {
                        try {
                            for (String msg : pendingMessages) {
                                writeData(response, msg);
                            }
                        } catch (IOException e) {
                            log("IOExeption sending message", e);
                        }
                    }
                }

            }

        }
    }
}
