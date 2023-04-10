package com.server.oceankeeper;

import com.server.oceankeeper.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

}