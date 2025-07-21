package com.pjserrano.orderprocessor.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import pjserrano.common.model.MyOrder;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockControlClientTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    private WebClient webClientMock;
    // CORREGIDO: Cambiado a RequestBodyUriSpec, que es el tipo devuelto por .put() antes de .uri()
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.ResponseSpec responseSpec;

    private StockControlClient stockControlClient;

    private static final String TEST_BASE_URL = "http://localhost:8000";
    private static final String TEST_PATH = "/update/subtract-stock";

    @BeforeEach
    void setUp() {
        webClientMock = mock(WebClient.class);
        // CORREGIDO: Creación del mock como RequestBodyUriSpec
        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClientMock);

        // CORREGIDO: webClientMock.put() ahora devuelve requestBodyUriSpec
        when(webClientMock.put()).thenReturn(requestBodyUriSpec);
        // La siguiente llamada es .uri() sobre requestBodyUriSpec
        when(requestBodyUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        stockControlClient = new StockControlClient(webClientBuilder, TEST_BASE_URL, TEST_PATH);
    }

    @Test
    void whenUpdateSubtractStockCalled_thenWebClientPutIsInvokedWithCorrectUriAndQueryParams() {
        MyOrder testOrder = new MyOrder(123, "Producto 123", 5, "Calle");

        stockControlClient.updateSubtractStock(testOrder).block();

        verify(webClientBuilder).baseUrl(eq(TEST_BASE_URL));
        verify(webClientBuilder).build();

        verify(webClientMock).put();
        verify(requestBodyUriSpec).uri(any(java.util.function.Function.class));
        verify(requestBodySpec).retrieve();
        verify(responseSpec).bodyToMono(Void.class);
    }
}