package com.javierdelgado.similarproducts.services.proxies;

import com.javierdelgado.similarproducts.models.ProductDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Proxy that manages requests sent to product-service (existingApis).
 * The url is configurable via application.properties.
 *
 * @see
 * <a href="https://github.com/dalogax/backendDevTest/blob/main/existingApis.yaml">Yaml with openapi specifications</a>
 */
@FeignClient(name = "product-service", url = "${product-service.url}")
public interface ProductsServiceProxy {

    /**
     * Returns the ids of the similar products to a given one ordered by similarity
     *
     * @param productId id of the product
     * @return a list of strings with the ids of the similar products
     */
    @GetMapping("/product/{productId}/similarids")
    List<String> getSimilarIds(@PathVariable("productId") String productId);

    /**
     * Returns the product detail for a given productId
     *
     * @param productId id of the product
     * @return product detail of the product
     */
    @GetMapping("/product/{productId}")
    ProductDetail getProductDetail(@PathVariable("productId") String productId);
}
