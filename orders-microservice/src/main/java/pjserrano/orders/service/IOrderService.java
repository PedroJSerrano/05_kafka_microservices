package pjserrano.orders.service;

import pjserrano.common.model.MyOrder;
import reactor.core.publisher.Mono;

public interface IOrderService {
    Mono<Void> processOrder(MyOrder order);
}
