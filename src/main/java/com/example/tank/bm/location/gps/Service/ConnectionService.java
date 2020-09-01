package com.example.tank.bm.location.gps.Service;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ConnectionService {



    public void openConnection() {
        SerialPort serialPort = new SerialPort("/dev/ttyACM0");
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            String s = new String(serialPort.readBytes(), StandardCharsets.UTF_8);
            serialPort.closePort();//Close serial port
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
}
