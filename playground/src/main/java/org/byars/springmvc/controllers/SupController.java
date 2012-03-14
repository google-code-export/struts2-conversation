package org.byars.springmvc.controllers;

import org.byars.struts2.services.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.code.rees.scope.spring.ConversationController;

@RequestMapping("/")
@ConversationController
public class SupController {

    @Autowired
    MyService myService;

    @RequestMapping(method = RequestMethod.GET)
    public String beginSup(ModelMap model) {
        myService.printPrice();
        model.addAttribute("message", "sup, world.  spring mvc represent");
        return "sup-spring-mvc";
    }

}
