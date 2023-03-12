package com.javierdelgado.similarproducts.controllers;

import com.javierdelgado.similarproducts.models.ProductDetail;
import com.javierdelgado.similarproducts.services.SimilarProductsService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SimilarProductsControllerTest {

    public static final String MOCK_ID = "mockId";

    @Autowired
    SimilarProductsController similarProductsController;
    @MockBean
    SimilarProductsService similarProductsService;

    @Test
    void whenGetSimilarProductsGetNullProductId_thenThrowConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> similarProductsController.getSimilarProducts(null));
    }

    @Test
    void whenSimilarProductServiceReturnNull_thenReturn404Response() {
        when(similarProductsService.getSimilarProducts(MOCK_ID)).thenReturn(null);
        ResponseEntity<List<ProductDetail>> response = similarProductsController.getSimilarProducts(MOCK_ID);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    void whenSimilarProductServiceReturnListOfProductDetails_thenReturn200ResponseWithTheList() {
        List<ProductDetail> mockResponse = Collections.singletonList(new ProductDetail(MOCK_ID, "name",
                BigDecimal.ZERO, Boolean.TRUE));
        when(similarProductsService.getSimilarProducts(MOCK_ID)).thenReturn(mockResponse);
        ResponseEntity<List<ProductDetail>> response = similarProductsController.getSimilarProducts(MOCK_ID);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(mockResponse));
    }

}
