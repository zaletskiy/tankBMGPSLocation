package com.example.tank.bm.location.gps.Controller;

import com.example.tank.bm.location.gps.Service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.stereotype.Controller
public class Controller {
    @Autowired
    private ConnectionService connectionService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void start() {
        connectionService.openConnection();
    }
}
