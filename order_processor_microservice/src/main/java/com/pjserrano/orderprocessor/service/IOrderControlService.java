package com.pjserrano.orderprocessor.service;

import com.pjserrano.orderprocessor.model.MyOrder;

public interface IOrderControlService {

    void processOrder(MyOrder order);
}
