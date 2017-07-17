package br.com.rcazambuja.metrics.kafka;

import java.io.IOException;
import java.util.Map;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.rcazambuja.metrics.influxdb.InfluxdbService;
import br.com.rcazambuja.metrics.model.MetricsConverter;
import br.com.rcazambuja.metrics.model.MetricsService;
import br.com.rcazambuja.metrics.model.ServiceMetrics;

@Service
@EnableBinding(KafkaChannels.class)
public class KafkaConsumerService {    
    private MetricsConverter metricsConverter;
    private MetricsService metricsService;
    
    public KafkaConsumerService(InfluxdbService influxdbService, MetricsConverter metricsConverter,
            MetricsService metricsService) {
        this.metricsConverter = metricsConverter;
        this.metricsService = metricsService;
    }
    
    @StreamListener(KafkaChannels.METRICS)
    public void process(Message<?> message) throws JsonParseException, JsonMappingException, IOException {
        String payload = (String) message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        Map<String, Object> messagePayload = mapper.readValue(payload , typeRef);                       
        ServiceMetrics metrics = metricsConverter.convert(messagePayload);        
        metricsService.store(metrics);
    }            
}
