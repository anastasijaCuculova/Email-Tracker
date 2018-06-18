import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import javax.mail.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class EmailNotification extends TimerTask {

    public static void main(String[] args) throws Exception {
        System.err.println("Hello");
        Timer timer = new Timer();
        SerialCommunication serialCommunication = new SerialCommunication();
        serialCommunication.initialize();
        Thread thread = new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ie) {
                }
            }
        };
        thread.start();



        TimerTask fetchMail = new EmailNotification();
        timer.scheduleAtFixedRate(fetchMail, new Date(), fONCE_PER_DAY);
        Thread.sleep(4000);
        getSerialPort();

    }
    private final static long fONCE_PER_DAY = 1000 * 60;




    private static void getSerialPort() {
        CommPortIdentifier serialPortId;
        Enumeration enumCommunication = CommPortIdentifier.getPortIdentifiers();
        SerialPort serialPort = null;
        OutputStream outputStream = null;
        while (enumCommunication.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumCommunication.nextElement();
            if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (serialPortId.getName().equals("COM1")) {
                    try {
                        serialPort = (SerialPort)
                                serialPortId.open("SimpleWriteApp", 2000);
                    } catch (PortInUseException e) {
                        System.out.println("err");
                    }
                    try {
                        outputStream = serialPort.getOutputStream();
                    } catch (IOException e) {
                        System.out.println("err1");
                    }
                    try {
                        serialPort.setSerialPortParams(9600,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                    } catch (UnsupportedCommOperationException e) {
                        System.out.println("err2");
                    }
                    try {
                        outputStream.write(printMailsAndGetLength());
                        System.out.println(printMailsAndGetLength());

                        outputStream.close();
                        serialPort.close();

                    } catch (IOException e) {
                        System.out.println("err3");
                    }
                }
                System.err.println(serialPortId.getName());
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Fetching mail...");
        int lenght = printMailsAndGetLength();
    }

    private static int printMailsAndGetLength() {
        int length = 0;
        try {
            String host = "imap.gmail.com";// change accordingly
            String mailStoreType = "pop3";
            String username = "anastasijacuculova11@gmail.com";// change accordingly
            String password = "*********";// change accordingly

            Properties properties = new Properties();
            properties.setProperty("mail.store.protocol", "imaps");


            Session emailSession = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            Store store = emailSession.getStore("imaps");
            store.connect(host, username, password);
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);
            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                System.out.println("Text: " + message.getContent().toString());
            }
            length = messages.length;
            System.err.println(messages.length);
            //close the store and folder objects
            emailFolder.close(false);
            store.close();


        } catch (javax.mail.NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return length;
    }
}
