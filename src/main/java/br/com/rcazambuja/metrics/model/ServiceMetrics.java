package br.com.rcazambuja.metrics.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ServiceMetrics {
    private LocalDateTime createdTime;
    
    private List<Metric> metrics = new ArrayList<>();
    private List<Metric> systemMetrics = new ArrayList<>();
    private Map<String, List<Metric>> hystrixMetrics = new HashMap<>();
    private List<Metric> eurekaMetrics = new ArrayList<>();
    private List<Metric> healthMetrics = new ArrayList<>();
    private List<Metric> datasourceMetrics = new ArrayList<>();
    private List<Metric> streamsMetrics = new ArrayList<>();
    private Map<String, List<Metric>> streamsChannelsMetrics = new HashMap<>();
    private Map<String, List<Metric>> countersMetrics = new HashMap<>();
    
    private Map<String, String> properties = new HashMap<>();
    
    private static final String[] SYSTEM_METRICS = {
            "mem", "processors", "uptime", "instance.uptime", "systemload.average",
            "heap", "nonheap", "threads", "classes", "gc."};
    
    private static final String HYSTRIX_METRICS = ".hystrix.";
    private static final String EUREKA_METRICS = ".eurekaclient.";
    private static final String HEALTH_METRICS = "health.";
    private static final String DATASOURCE_METRICS = "datasource.";
    private static final String STREAMS_METRICS = "integration.";
    private static final String STREAMS_CHANNELS_METRICS = "channel.";
    private static final String COUNTERS_METRICS = "counter.status.";
    
    private ServiceMetrics() {}
    
    public static final ServiceMetrics of(LocalDateTime createdTime, List<Metric> metrics, Map<String, String> properties) {
        ServiceMetrics serviceMetrics = new ServiceMetrics();
        
        serviceMetrics.createdTime = createdTime;
        metrics.forEach(m -> serviceMetrics.addMetric(m));
        properties.forEach((k, v) -> serviceMetrics.addProperty(k, v)); 
        
        return serviceMetrics;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public long getCreatedTimeEpochSecond() {
        return createdTime
                .atZone(ZoneId.of("GMT"))
                .toEpochSecond();
    }
    
    public List<Metric> getMetrics() {
        return Collections.unmodifiableList(metrics);
    }
    
    public List<Metric> getSystemMetrics() {
        return Collections.unmodifiableList(systemMetrics);
    }
    
    public List<Metric> getEurekaMetrics() {
        return Collections.unmodifiableList(eurekaMetrics);
    }
    
    public List<Metric> getHealthMetrics() {
        return Collections.unmodifiableList(healthMetrics);
    }
    
    public List<Metric> getDatasourceMetrics() {
        return Collections.unmodifiableList(datasourceMetrics);
    }
    
    public List<Metric> getStreamsMetrics() {
        return Collections.unmodifiableList(streamsMetrics);
    }
    
    public Map<String, List<Metric>> getStreamsChannelsMetrics() {
        return Collections.unmodifiableMap(streamsChannelsMetrics);
    }
        
    public Map<String, List<Metric>> getHystrixMetrics() {
        return Collections.unmodifiableMap(hystrixMetrics);
    }
    
    public Map<String, List<Metric>> getCountersMetrics() {
        return Collections.unmodifiableMap(countersMetrics);
    }
    
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public String getProperty(String key) {
        return this.properties.get(key);
    }
    
    public void addProperty(String key, String value) { 
        this.properties.put(key, value);
    }
    
    private void putMappedHystrixMetrics(String circuitId, Metric metric) {
        List<Metric> metrics = hystrixMetrics.get(circuitId);
        if(metrics==null) metrics = new ArrayList<>();
        metrics.add(metric);
        hystrixMetrics.put(circuitId, metrics);
    }
    
    private void putStreamChannelMetrics(String channelId, Metric metric) {
        List<Metric> metrics = streamsChannelsMetrics.get(channelId);
        if(metrics==null) metrics = new ArrayList<>();
        metrics.add(metric);
        streamsChannelsMetrics.put(channelId, metrics);
    }
    
    private void putCountersMetrics(String requestedPath, Metric metric) {
        List<Metric> metrics = countersMetrics.get(requestedPath);
        if(metrics==null) metrics = new ArrayList<>();
        metrics.add(metric);
        countersMetrics.put(requestedPath, metrics);
    }
    
    private boolean isSystemMetric(Metric metric) {
        return Stream.of(SYSTEM_METRICS).anyMatch(x -> metric.getName().startsWith(x));
    }
    
    private boolean isHystrixMetric(Metric metric) {
        return metric.getName().contains(HYSTRIX_METRICS);
    }
    
    private boolean isEurekaMetric(Metric metric) {
        return metric.getName().contains(EUREKA_METRICS);
    }
    
    private boolean isHealthMetric(Metric metric) {
        return metric.getName().startsWith(HEALTH_METRICS);
    }
    
    private boolean isDatasourceMetric(Metric metric) {
        return metric.getName().startsWith(DATASOURCE_METRICS);
    }
    
    private boolean isStreamMetric(Metric metric) {
        return metric.getName().startsWith(STREAMS_METRICS);
    }
    
    private boolean isCounterMetric(Metric metric) {
        return metric.getName().startsWith(COUNTERS_METRICS);
    }
    
    private void addSystemMetric(Metric metric) {
        if(isSystemMetric(metric)) {
            this.systemMetrics.add(metric);            
        }        
    }       
    
    private void addHystrixMetric(Metric metric) {       
        if(isHystrixMetric(metric)) {
            String circuitId = "";
            String shortName = metric.getName();            
            shortName = shortName.substring(shortName.indexOf(HYSTRIX_METRICS)+HYSTRIX_METRICS.length());
            if(shortName.startsWith("hystrixthreadpool.")) {                
                circuitId = shortName.substring(18, shortName.indexOf(".", 18));                
            } else if (shortName.startsWith("hystrixcommand.ribboncommand.")) {
                shortName = shortName.replace("hystrixcommand.ribboncommand.","");
                circuitId = shortName.substring(0, shortName.indexOf("."));                
            } else {
                shortName = shortName.replace("hystrixcommand.","");
                circuitId = shortName.substring(0, shortName.indexOf(".", shortName.indexOf(".")+1));                
            }
            
            shortName = shortName.replace(circuitId+".", "");
            
            this.putMappedHystrixMetrics(circuitId, Metric.of(shortName, metric.getValue()));            
        }        
    }
    
    private void addEurekaMetric(Metric metric) {
        if(isEurekaMetric(metric)) {
            String shortName = metric.getName();
            shortName = shortName.substring(shortName.indexOf(EUREKA_METRICS)+EUREKA_METRICS.length());            
            this.eurekaMetrics.add(Metric.of(shortName, metric.getValue()));        
        }        
    }
    
    private void addHealthMetric(Metric metric) {
        if(isHealthMetric(metric)) {
            String shortName = metric.getName().replace(HEALTH_METRICS, "");
            this.healthMetrics.add(Metric.of(shortName, metric.getValue()));
        }        
    }
    
    private void addDatasourceMetric(Metric metric) {
        if(isDatasourceMetric(metric)) {
            String shortName = metric.getName().replace(DATASOURCE_METRICS, "");
            this.datasourceMetrics.add(Metric.of(shortName, metric.getValue()));
        }
    }
    
    private void addStreamMetric(Metric metric) {
        if(isStreamMetric(metric)) {
            String shortName = metric.getName().replace(STREAMS_METRICS, "");
            if(shortName.startsWith(STREAMS_CHANNELS_METRICS)) {                
                shortName = shortName.replace(STREAMS_CHANNELS_METRICS, "");
                String channelName = shortName.substring(0, shortName.indexOf("."));
                shortName = shortName.replace(channelName+".", "");
                putStreamChannelMetrics(channelName, Metric.of(shortName, metric.getValue()));                
            } else {
                this.streamsMetrics.add(Metric.of(shortName, metric.getValue()));
            }
        }
    }
    
    private void addCountersMetric(Metric metric) {
        if(isCounterMetric(metric)) {
            String shortName = "requestcount";
            String reqPath = metric.getName().replace(COUNTERS_METRICS, "");
            putCountersMetrics(reqPath, Metric.of(shortName, metric.getValue()));
        }
    }
    
    public void addMetric(Metric metric) {
        metric = Metric.of(
                metric.getName()
                    .replace("servo.", "")
                    .replace("gauge.", ""), 
                metric.getValue());
        addSystemMetric(metric);
        addHystrixMetric(metric);
        addEurekaMetric(metric);
        addHealthMetric(metric);
        addDatasourceMetric(metric);
        addStreamMetric(metric);
        addCountersMetric(metric);
        this.metrics.add(metric);
    }
    
    @Override
    public String toString() {
        return this.properties.toString();
    }
}
