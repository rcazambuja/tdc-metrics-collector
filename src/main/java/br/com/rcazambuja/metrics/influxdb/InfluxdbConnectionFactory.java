package br.com.rcazambuja.metrics.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InfluxdbConnectionFactory {    
    @Value("${influxdb.url}")
    String url;
    
    @Value("${influxdb.port}")
    String port;
    
    @Value("${influxdb.username}")
    String username;
    
    @Value("${influxdb.password}")
    String password;
    
    public InfluxDB getConnection() {
        return InfluxDBFactory.connect(url + ":" + port, username, password);
    }
}
