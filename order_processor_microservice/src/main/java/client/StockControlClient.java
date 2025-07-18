package client;

import model.MyOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class StockControlClient {

    private final WebClient webClient;
    private final String updateSubtractStockPath;

    public StockControlClient(
            WebClient.Builder webClientBuilder,
            @Value("${stock.control.service.base.url}") String baseUrl,
            @Value("${stock.control.service.update.subtractStock.path}") String updateSubtractStockPath) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.updateSubtractStockPath = updateSubtractStockPath;
    }

    public Mono<Void> updateSubtractStock(MyOrder order) {
        System.out.println("Llamando a StockControlClient para actualizar stock...");

        int productCode = order.getCodeProductOrdered();
        int quantity = order.getQuantity();

        return this.webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(this.updateSubtractStockPath)
                        .queryParam("productCode", productCode)
                        .queryParam("quantity", quantity)
                        .build()
                )
                .retrieve()
                .bodyToMono(Void.class);
    }
}