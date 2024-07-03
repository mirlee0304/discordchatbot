package com.discordchatbot.response;

import com.discordchatbot.dto.QuoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @GetMapping("/quote")
    public QuoteDTO getQuote() {
        return quoteService.getQuote();
    }
}
