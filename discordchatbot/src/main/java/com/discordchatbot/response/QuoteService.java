package com.discordchatbot.response;

import com.discordchatbot.dto.QuoteDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class QuoteService {

    private final WebClient webClient;

    public QuoteService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.quotable.io").build();
    }

    public QuoteDTO getQuote() {
        Mono<QuoteDTO> response = this.webClient.get()
                .uri("/random")
                .retrieve()
                .bodyToMono(QuoteDTO.class);

        return response.block();
    }
}
