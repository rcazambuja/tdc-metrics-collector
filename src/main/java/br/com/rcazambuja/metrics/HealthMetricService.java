package br.com.rcazambuja.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class HealthMetricService {
    private HealthEndpoint healthMvc;
    private GaugeService gaugeService;
    
    @Autowired
    public HealthMetricService(final HealthEndpoint health, final GaugeService gaugeService) {
        this.healthMvc = health;
        this.gaugeService = gaugeService;
    }

    @Scheduled(fixedDelay=5000)
    public void schedule() {
        Health health = healthMvc.invoke();
        gaugeService.submit("health.status", convert(health));
    }

    private Float convert(Object healthObj) {
        if(healthObj instanceof Health) {
            String value = ((Health) healthObj).getStatus().getCode();
            if(null != value && value.equals("UP")) {
                return 1f;
            }
            return 0f;
        }
        throw new IllegalArgumentException();
    }
}
