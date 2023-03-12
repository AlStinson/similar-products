package com.javierdelgado.similarproducts.services;

import com.javierdelgado.similarproducts.models.ProductDetail;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that manages business logic related to similar products service.
 *
 * @see
 * <a href="https://github.com/dalogax/backendDevTest/blob/main/similarProducts.yaml">Yaml with openapi specifications</a>
 */
@Component
@Validated
public class SimilarProductsService {

    private final Logger logger;
    private final ProductsService productsService;

    public SimilarProductsService(@NotNull ProductsService productsService) {
        this.logger = LoggerFactory.getLogger(SimilarProductsService.class);
        this.productsService = productsService;
        logger.info("SimilarProductsService initialized successfully");
    }

    /**
     * Returns the details of the similar products to a given one ordered by similarity. If the product can not be
     * found or there is any problem with the products-service, it will return null. If any of the similar products
     * can not be found, or it has similar data, it will not be included in the response. It uses cache to reduce the
     * numbers of calls to the products-service.
     *
     * @param productId id of the product
     * @return list of detail of the similar products or null
     */
    @Cacheable(value = "similarProductDetail", sync = true)
    @SuppressWarnings("java:S1168")
    public List<ProductDetail> getSimilarProducts(@NotNull String productId) {
        List<String> similarProductIds = productsService.getSimilarIds(productId);
        if (similarProductIds == null) {
            logger.debug("Product with id={} can not be found", productId);
            return null;
        }
        return similarProductIds.parallelStream().collect(ArrayList::new, this::addProductDetail, ArrayList::addAll);
    }


    /* Internal use only */
    public void addProductDetail(List<ProductDetail> list, String productId) {
        if (productId == null) {
            logger.debug("Can not add a product with id=null");
            return;
        }
        ProductDetail productDetail = productsService.getProductDetail(productId);
        if (productDetail == null) {
            logger.debug("Product with id={} will not be included in the response due it was not found.", productId);
            return;
        }
        list.add(productDetail);
    }
}
