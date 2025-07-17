package service;

import model.MyOrder;
import reactor.core.publisher.Mono;

public interface IOrderService {
    Mono<Void> processOrder(MyOrder order);
}
