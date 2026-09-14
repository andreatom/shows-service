package it.its.cinema.shows_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "catalog-provider", url = "${cinema.catalog.remote-url}")
public interface CatalogClient {

    @GetMapping(value = "/catalog.json", produces = "application/json")
    List<MovieJson> scaricaCatalogo();
}
