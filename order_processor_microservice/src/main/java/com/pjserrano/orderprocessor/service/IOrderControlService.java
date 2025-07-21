package com.pjserrano.orderprocessor.service;

import pjserrano.common.model.MyOrder;

public interface IOrderControlService {

    void processOrder(MyOrder order);
}
