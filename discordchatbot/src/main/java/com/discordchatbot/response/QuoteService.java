package com.discordchatbot.response;

import com.discordchatbot.dto.QuoteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);
    private final WebClient webClient;

    public QuoteService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.quotable.io").build();
    }

    public QuoteDTO getQuote() {
        try {
            Mono<QuoteDTO> response = this.webClient.get()
                    .uri("/random")
                    .retrieve()
                    .bodyToMono(QuoteDTO.class);

            return response.block();
        } catch (WebClientResponseException e) {
            logger.error("Error response from quote API: Status {}, Body {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            return getDefaultQuote();
        } catch (Exception e) {
            logger.error("Error while retrieving quote from API", e);
            return getDefaultQuote();
        }
    }

    private QuoteDTO getDefaultQuote() {
        QuoteDTO defaultQuote = new QuoteDTO();
        defaultQuote.setContent("This is a default quote due to an error.");
        defaultQuote.setAuthor("Unknown");
        return defaultQuote;
    }
}
