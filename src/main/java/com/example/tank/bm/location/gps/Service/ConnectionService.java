package com.example.tank.bm.location.gps.Service;

import jssc.*;
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

    public void stop() throws SerialPortException {
        getPort().removeEventListener();
        closePort();
    }

    private SerialPort getPort() throws SerialPortException {
        if (serialPort == null) {
            return initializePort();
        } else {
            return serialPort;
        }
    }

    private String findPort(){
        return SerialPortList.getPortNames()[0];
    }


    private SerialPort initializePort() throws SerialPortException {
        SerialPort serialPort = new SerialPort(findPort());
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

                if (event.getEventValue() > 0) {
                    try {
                        byte[] bytes = serialPort.readBytes();
                        String data = parseData(bytes);
                        template.convertAndSend("/gps/liveData", data);

                        System.out.println("JSSC -> initPort() : Received response hexstring: " + data);
                        System.out.println("----------------------------------");
                    } catch (SerialPortException ex) {
                        System.out.println("JSSC -> initPort() : Error in receiving string from COM-port: " + ex);
                    }
                }
            }
        });
    }

    private String parseData(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        String s = new String(bytes);
        String[] rows = s.split("\\r\\n");
        for (String row : rows) {
            if (row.contains("$GPRMC")) {
                String[] data = row.split(",");
                if (!StringUtils.isEmpty(data[3]) && !StringUtils.isEmpty(data[5])) {
                    sb.append(data[3]).append(data[5]);
                } else {
                    sb.append("no data");
                }
            }
        }
        return sb.toString();

    }
}
