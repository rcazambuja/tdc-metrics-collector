package br.com.rcazambuja.metrics.influxdb;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.rcazambuja.metrics.model.Metric;

@Component
public class InfluxdbService {
    @Value("${influxdb.dbName}")
    String dbName;
    
    private InfluxdbConnectionFactory factory;     
    
    @Autowired
    public InfluxdbService(InfluxdbConnectionFactory factory) {
        this.factory = factory;
    }
    
    public void send(String measurement, long createdTime, Map<String, String> properties, List<Metric> metrics) {       
        BatchPoints.Builder batchPointsBuilder = BatchPoints
                .database(dbName)
                .retentionPolicy("autogen")
                .consistency(ConsistencyLevel.ALL);
        
        properties.forEach((k, v) -> batchPointsBuilder.tag(k, v));
        
        BatchPoints batchPoints = batchPointsBuilder.build();
        
        Point.Builder pointBuilder = Point
                .measurement(measurement)
                .time(createdTime, TimeUnit.SECONDS);
        
        for(Metric metric : metrics) {
            if(!Objects.isNull(metric.getValue())) {
                pointBuilder.addField(metric.getName(), ((Double)metric.getValue()).doubleValue());
            }
        }

        batchPoints.point(pointBuilder.build());
        sendToInflux(batchPoints);
    }
  
    private void sendToInflux(BatchPoints batchPoints) {
        InfluxDB influxDB = factory.getConnection();
        influxDB.write(batchPoints);
        influxDB.close();
    }
}