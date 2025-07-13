package service.impl;

import model.MyOrder;
import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.ProductRepository;
import service.IOrderControlService;
import service.IProductService;

@Service
public class OrderControlServiceImpl implements IOrderControlService {

    @Autowired
    private IProductService productService;

    @Autowired
    public ProductRepository productRepository;

    @Override
    @KafkaListener(topics = "${kafka.topic}", groupId = "group1")
    public void processOrder(MyOrder order) {
        productService.findByProductCode(order.getCodeProductOrdered())
                .flatMap(p -> {
                    p.setQuantityInStock(p.getQuantityInStock() - order.getQuantity());
                    return productRepository.save(p);
                })
                .subscribe();

    }
}
