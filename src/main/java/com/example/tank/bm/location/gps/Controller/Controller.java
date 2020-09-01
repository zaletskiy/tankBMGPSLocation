package com.example.tank.bm.location.gps.Controller;

import com.example.tank.bm.location.gps.Service.ConnectionService;
import jssc.SerialPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

@org.springframework.stereotype.Controller
public class Controller {
    @Autowired
    private ConnectionService connectionService;

    @MessageMapping("/run")
    @SendTo("/gps/liveData")
    public void run() throws SerialPortException {
        connectionService.run();
    }
}
