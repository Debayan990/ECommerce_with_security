package com.cts.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "item-service-client", url = "${item.service.url}")
public interface ItemServiceClient {

    @GetMapping("/api/items/{id}")
    void getItemById(@PathVariable("id") Long id);
}
