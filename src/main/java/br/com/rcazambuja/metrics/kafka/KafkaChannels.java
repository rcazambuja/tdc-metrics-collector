package br.com.rcazambuja.metrics.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface KafkaChannels {
    public static final String METRICS = "applicationMetrics_in";
    
    @Input(METRICS)
    SubscribableChannel metrics();
}
