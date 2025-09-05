package com.presta.walletsettlement.rabbbitmq;

import com.presta.walletsettlement.wallet.domain.dto.LedgerTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbit.exchange.name}")
    private String exchangeName;

    @Value("${rabbit.routing.key}")
    private String routingKey;

    public TransactionMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTransactionMessage(LedgerTransaction transaction) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, transaction);
    }
}