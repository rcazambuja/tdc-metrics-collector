package br.com.rcazambuja.metrics.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.rcazambuja.metrics.influxdb.InfluxdbService;

@Component
public class MetricsService {
    private InfluxdbService influxdbService;
    
    private final static String SYSTEM_METRICS = "System";
    private final static String HYSTRIX_METRICS = "Hystrix";
    private final static String DATASOURCE_METRICS = "Datasource";
    private final static String STREAMS_METRICS = "Streams";
    private final static String STREAMS_CHANNELS_METRICS = "StreamsChannels";
    private final static String HEALTH_METRICS = "Health";
    
    @Autowired
    public MetricsService(InfluxdbService influxdbService) {
        this.influxdbService = influxdbService;
    }
    
    public void store(ServiceMetrics metrics) {
        storeSystemMetrics(metrics);        
        storeHealthMetrics(metrics);        
        storeDatasourceMetrics(metrics);
        storeStreamMetrics(metrics);        
        storeStreamChannelsMetrics(metrics);        
        storeHystrixMetrics(metrics);
    }

    private void storeHystrixMetrics(ServiceMetrics metrics) {
        Map<String, List<Metric>> hystrixMetrics = metrics.getHystrixMetrics();
        for(Entry<String, List<Metric>> entry : hystrixMetrics.entrySet()) {
            Map<String, String> properties = new HashMap<>();            
            properties.putAll(metrics.getProperties());
            if(entry.getKey().contains("#")) {
                properties.put("circuitId", entry.getKey().substring(0, entry.getKey().indexOf(".")));
                properties.put("clientId", entry.getKey().substring(entry.getKey().indexOf(".")+1, entry.getKey().indexOf("#")));
                properties.put("methodId", entry.getKey().substring(entry.getKey().indexOf("#")+1));
            } else {
                properties.put("circuitId", entry.getKey());   
            }                       
                     
            influxdbService.send(HYSTRIX_METRICS, metrics.getCreatedTimeEpochSecond(), 
                    properties, entry.getValue());
        }
    }

    private void storeStreamChannelsMetrics(ServiceMetrics metrics) {
        Map<String, List<Metric>> channelsMetrics = metrics.getStreamsChannelsMetrics();
        for(Entry<String, List<Metric>> entry : channelsMetrics.entrySet()) {
            Map<String, String> properties = new HashMap<>();            
            properties.putAll(metrics.getProperties());
            properties.put("channelId", entry.getKey());
                                
            influxdbService.send(STREAMS_CHANNELS_METRICS, metrics.getCreatedTimeEpochSecond(), 
                    properties, entry.getValue());
        }
    }

    private void storeStreamMetrics(ServiceMetrics metrics) {
        if(!metrics.getStreamsMetrics().isEmpty()) {               
            influxdbService.send(STREAMS_METRICS, metrics.getCreatedTimeEpochSecond(), 
                    metrics.getProperties(), metrics.getStreamsMetrics());
        }
    }

    private void storeDatasourceMetrics(ServiceMetrics metrics) {
        if(!metrics.getDatasourceMetrics().isEmpty()) {
            influxdbService.send(DATASOURCE_METRICS, metrics.getCreatedTimeEpochSecond(), 
                    metrics.getProperties(), metrics.getDatasourceMetrics());
        }
    }

    private void storeHealthMetrics(ServiceMetrics metrics) {
        influxdbService.send(HEALTH_METRICS, metrics.getCreatedTimeEpochSecond(), 
                metrics.getProperties(), metrics.getHealthMetrics());
    }

    private void storeSystemMetrics(ServiceMetrics metrics) {
        influxdbService.send(SYSTEM_METRICS, metrics.getCreatedTimeEpochSecond(), 
                metrics.getProperties(), metrics.getSystemMetrics());
    }
}
 