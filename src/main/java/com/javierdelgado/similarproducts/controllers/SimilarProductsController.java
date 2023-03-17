package com.javierdelgado.similarproducts.controllers;

import com.javierdelgado.similarproducts.models.ProductDetail;
import com.javierdelgado.similarproducts.services.SimilarProductsService;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller that manages call to similar product service
 *
 * @see
 * <a href="https://github.com/dalogax/backendDevTest/blob/main/similarProducts.yaml">Yaml with openapi specifications</a>
 */
@RestController
@Validated
public class SimilarProductsController {

    private final Logger logger;
    private final SimilarProductsService similarProductsService;

    public SimilarProductsController(@NotNull SimilarProductsService similarProductsService) {
        this.logger = LoggerFactory.getLogger(SimilarProductsController.class);
        this.similarProductsService = similarProductsService;
        logger.info("SimilarProductsController initialized successfully");
    }

    /**
     * Returns the details of the similar products to a given one ordered by similarity. If the product can not be
     * found, it will return a 404 response.
     *
     * @param productId id of the product
     * @return ResponseEntity with status and result
     * @see 'get-product-similar' operation at
     * <a href="https://github.com/dalogax/backendDevTest/blob/main/similarProducts.yaml">similar products specification</a>
     */
    @GetMapping("/product/{productId}/similar")
    @Bulkhead(name = "get-similar-products")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(@NotNull @PathVariable("productId") String productId) {
        logger.debug("Request with id={}", productId);
        List<ProductDetail> similarProductDetails = similarProductsService.getSimilarProducts(productId);
        if (similarProductDetails == null) {
            logger.debug("Product with id={} was not found", productId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(similarProductDetails);
    }

    /**
     * Return a 400 Bad Request response in case of any ConstraintViolationException is thrown. This should only
     * happend at due a validation failed in method parameters annotations.
     *
     * @param e the exception that has been thrown
     * @return the message of the exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        logger.debug("Bad request. Reason: {}", e.getMessage());
        return e.getMessage();
    }

    /**
     * Return a 503 Service Unavailable in case of BulkheadFullException is thrown.
     *
     * @param e the exception that has been thrown
     * @return the message of the exception
     */
    @ExceptionHandler(BulkheadFullException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleBulkheadFullExceptions(Exception e) {
        return e.getMessage();
    }

    /**
     * Handle IOException (for example, if client does not wait for response)
     */
    @ExceptionHandler(IOException.class)
    public void handleIOException() {
        // Handle IOException
    }
}
