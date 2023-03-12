package com.javierdelgado.similarproducts.services;

import com.javierdelgado.similarproducts.models.ProductDetail;
import com.javierdelgado.similarproducts.services.proxies.ProductsServiceProxy;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

/**
 * Class that manages requests sent to product-service via ProductServiceProxy.
 * It is a wrapper to ProductServiceProxy. Its ensures that in case of any bad-formed data or any exception is throw
 * (readTimeout, 400, 404, 500 ... responses or any unexpected one) null value is returned instead If that happens,
 * it can be considered as a 404 response (Not Found).
 *
 * @see
 * <a href="https://github.com/dalogax/backendDevTest/blob/main/existingApis.yaml">Yaml with openapi specifications</a>
 */
@Component
@Validated
public class ProductsService {

    private final Logger logger = LoggerFactory.getLogger(ProductsService.class);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private ProductsServiceProxy productsServiceProxy;

    public ProductsService(@NotNull ProductsServiceProxy productsServiceProxy) {
        this.productsServiceProxy = productsServiceProxy;
        logger.info("ProductsService initialized successfully");
    }

    /**
     * Returns the ids of the similar products to a given one ordered by similarity. If there is any kind of problem
     * in the request, null is returned instead.
     *
     * @param productId id of the product
     * @return a list of strings with the ids of the similar products or null
     */
    @SuppressWarnings("java:S1168")
    public List<String> getSimilarIds(@NotNull String productId) {
        try {
            logger.debug("GetSimilarIds request sent with id={}", productId);
            List<String> similarIds = productsServiceProxy.getSimilarIds(productId);
            logger.debug("GetSimilarIds request with id={} returned {}", productId, similarIds);
            return similarIds;
        } catch (FeignException e) {
            logger.debug("Error with id={}. {}.", productId, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error with id={}.", productId, e);
        }
        return null;
    }

    /**
     * Returns the product detail for a given productId. It uses cache to ensure that only 1 call to the service is
     * done if many products have another as similar product (instead of calling getProductDetail one time per
     * another one that has it as similar). If there is any kind of problem in the request, null is returned instead.
     *
     * @param productId id of the product
     * @return product detail of the product or null
     */
    @Cacheable(value = "productDetail", sync = true)
    public ProductDetail getProductDetail(@NotNull String productId) {
        try {
            logger.debug("GetProductDetail request sent with id={}", productId);
            ProductDetail productDetail = productsServiceProxy.getProductDetail(productId);
            logger.debug("GetProductDetail request sent with id={} returned {}", productId, productDetail);
            Set<ConstraintViolation<ProductDetail>> validationErrors = validator.validate(productDetail);
            if (!validationErrors.isEmpty()) {
                logger.warn("Error with id={}. Incorrect data in response. Data: {}. Error: {}", productId,
                        productDetail, validationErrors);
                return null;
            }
            return productDetail;
        } catch (FeignException e) {
            logger.debug("Error with id={}. {}.", productId, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error with id={}.", productId, e);
        }
        return null;
    }
}
