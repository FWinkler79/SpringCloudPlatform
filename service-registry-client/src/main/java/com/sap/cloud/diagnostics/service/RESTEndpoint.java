package com.sap.cloud.diagnostics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RESTEndpoint {
  
    @Autowired
    private DiscoveryClient discoveryClient;
    
    /**
     * A REST endpoint to lookup service instance information 
     * for a given service ID at Eureka service registry.
     * 
     * @param serviceId the ID of the service as registered in Eureka.
     * @return the list of service instances in JSON. Contains all service instance information.
     * @throws Exception in case of an issue.
     */
    @RequestMapping(value = "/lookup/{serviceId}", method = RequestMethod.GET)
    public List<ServiceInstance> lookup(@PathVariable String serviceId) throws Exception {  
        return discoveryClient.getInstances(serviceId);
    }
}
