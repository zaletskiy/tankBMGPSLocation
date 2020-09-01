package com.example.tank.bm.location.gps.Service;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ConnectionService {
    @Autowired
    private SimpMessagingTemplate template;


    SerialPort serialPort = null;

    public void run() throws SerialPortException {
        addEventListener();
    }

    private SerialPort getPort() throws SerialPortException {
        if (serialPort == null){
            return initializePort();
        } else {
            return serialPort;
        }
    }


    private SerialPort initializePort() throws SerialPortException {
        SerialPort serialPort = new SerialPort("/dev/ttyACM0");
        serialPort.openPort();//Open serial port
        serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        this.serialPort = serialPort;
        return serialPort;
    }

    private void closePort() throws SerialPortException {
        this.serialPort.closePort();//Close serial port
    }

    private void addEventListener() throws SerialPortException {
        getPort().addEventListener(new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent event) {

                if(event.getEventValue() > 0) {
                    try {
                        byte[] bytes = serialPort.readBytes();
                        String data = parseData(bytes);
                        template.convertAndSend("/gps/liveData", data);

                        System.out.println("JSSC -> initPort() : Received response hexstring: " + data);
                        System.out.println("----------------------------------");
                    }
                    catch (SerialPortException ex) {
                        System.out.println("JSSC -> initPort() : Error in receiving string from COM-port: " + ex);
                    }
                }
            }
        });
    }


    /*public synchronized String getGpsLiveData() {
        SerialPort serialPort = new SerialPort("/dev/ttyACM0");
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            byte[] bytes = serialPort.readBytes();
            serialPort.closePort();//Close serial port

            return parseData(bytes);
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
            return null;
        }
    }

    public synchronized void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR()) { // if we receive data
            if (serialPortEvent.getEventValue() > 0) { // if there is some existent data
                try {
                    byte[] bytes = this.serialPort.readBytes(); // reading the bytes received on serial port
                    if (bytes != null) {
                        for (byte b : bytes) {
                            this.serialInput.add(b); // adding the bytes to the linked list

                            // *** DEBUGGING *** //
                            System.out.print(String.format("%X ", b));
                        }
                    }
                } catch (SerialPortException e) {
                    System.out.println(e);
                    e.printStackTrace();
                }
            }
        }

    }
*/


    private String parseData(byte[] bytes) {
        if (bytes == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();

        String s = new String(bytes);
        String[] rows = s.split("\\r\\n");
        for (String row : rows) {
            if (row.contains("$GPRMC")){
                String[] data= row.split(",");
                if (!StringUtils.isEmpty(data[3]) && !StringUtils.isEmpty(data[5])) {
                    sb.append(data[3]).append(data[5]);
                }else {
                    sb.append("no data");
                }
            }
        }
        return sb.toString();

    }
}
