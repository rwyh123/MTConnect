package com.example.MTConnect.controller;

import com.example.MTConnect.service.MTConnectService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mtconnect")
public class MTConnectController {
    private final MTConnectService service;
    public MTConnectController(MTConnectService service) { this.service = service; }

    @GetMapping(value="/current", produces= MediaType.APPLICATION_XML_VALUE)
    public String current() { return service.currentXml(); }
}
