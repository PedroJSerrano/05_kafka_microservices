package com.pjserrano.orders.service;

import com.pjserrano.orders.model.MyOrder;
import reactor.core.publisher.Mono;

public interface IOrderService {
    Mono<Void> processOrder(MyOrder order);
}
