package br.com.rcazambuja.metrics.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;


@Component
public class MetricsConverter {
    
    @SuppressWarnings("unchecked")
    public ServiceMetrics convert(Map<String, Object> source) {
        List<Metric> metrics = extractMetrics((List<Map<String, Object>>) source.get("metrics"));
        Map<String, String> properties = extractProperties((Map<String, String>) source.get("properties"));
        LocalDateTime createTime = getTime((String) source.get("createdTime"));
        
        return ServiceMetrics.of(createTime, metrics, properties);        
        
    }
    
    private List<Metric> extractMetrics(List<Map<String, Object>> listMetrics) {
        List<Metric> metrics = new ArrayList<>();
        for(Map<String, Object> map : listMetrics) {
            metrics.add(Metric.of((String)map.get("name"), map.get("value")));
        }
        return metrics;
    }
    
    private Map<String, String> extractProperties(Map<String, String> properties) {
        Map<String, String> newProperties = new HashMap<>();        
        newProperties.put("serviceId", properties.get("spring.application.name"));
        newProperties.put("host", properties.get("spring.cloud.client.hostname"));               
        return newProperties;
    }
    
    private LocalDateTime getTime(String timestamp) {        
        timestamp = timestamp.substring(0, timestamp.indexOf("."));
        LocalDateTime localDateTime = LocalDateTime.parse(timestamp);
        return localDateTime;               
    }
}
