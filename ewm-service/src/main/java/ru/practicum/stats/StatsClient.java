package ru.practicum.stats;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsClient {

    private final WebClient webClient;

    @Autowired
    public StatsClient(WebClient.Builder webClientBuilder, @Value("${stats-server.url}") String statsUrl) {
        this.webClient = webClientBuilder.baseUrl(statsUrl).build();
    }

    public void sendHit(HitDtoCreate hitDtoCreate) {
        webClient.post()
                .uri("/hit")
                .header("Content-Type", "application/json")
                .body(Mono.just(hitDtoCreate), HitDtoCreate.class)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder sb = new StringBuilder();
        sb.append("/stats")
                .append("?start=")
                .append(URLEncoder.encode(start, StandardCharsets.UTF_8))
                .append("&end=")
                .append(URLEncoder.encode(end, StandardCharsets.UTF_8))
                .append("&uris=");
        String prefix = "";
        for (String uri : uris) {
            sb.append(prefix);
            prefix = ",";
            sb.append(uri);
        }
        sb.append("&unique=")
                .append(unique);

        List<ViewStats> viewStatsList = webClient
                .get()
                .uri(sb.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStats>>() {
                })
                .block();
        log.info("StatsClient запрошена статистика {}", viewStatsList);
        return viewStatsList;
    }
}
