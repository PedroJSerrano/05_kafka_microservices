package client;

import model.MyOrder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class StockControlClient {

    private final WebClient webClient;

    public StockControlClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
        // Nota: la URL base debe ir en el archivo de propiedades
    }

    public void updateSubtractStock(MyOrder order) {
        // Extrae los datos necesarios del objeto MyOrder
        int productCode = order.getCodeProductOrdered();
        int quantity = order.getQuantity();

        this.webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("update/subtract-stock")
                        .queryParam("productCode", productCode)
                        .queryParam("quantity", quantity)
                        .build()
                )
                .retrieve()
                .bodyToMono(Void.class)
                .block(); // O usa .subscribe() para reactivo
    }
}