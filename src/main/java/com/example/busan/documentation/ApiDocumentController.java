package com.example.busan.documentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiDocumentController {

    @GetMapping
    public String getDocuments() {
        return "index";
    }
}
