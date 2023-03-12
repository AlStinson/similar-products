package com.javierdelgado.similarproducts.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Manages unexpected exception that are not catch after controller level
 */
@ControllerAdvice
public class SimilarProductsControllerAdvice {

    Logger logger = LoggerFactory.getLogger(SimilarProductsControllerAdvice.class);

    /**
     * Returns a 500 Internal Server Error response in case of any unexpected exception
     *
     * @param e the exception that has been thrown
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void exceptionHandler(Exception e) {
        logger.error("Unexpected exception.", e);
    }
}
