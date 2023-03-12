package com.javierdelgado.similarproducts.services;

import com.javierdelgado.similarproducts.models.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class SimilarProductsServiceTest {

    static final String MOCK_ID = "mockId";
    static final String MOCK_ID_2 = "mockId2";
    static final String MOCK_ID_3 = "mockId3";
    static final String MOCK_ID_4 = "mockId4";
    static final ProductDetail MOCK_PRODUCT_DETAIL_2 = new ProductDetail(MOCK_ID_2, "name2", ZERO, TRUE);
    static final ProductDetail MOCK_PRODUCT_DETAIL_3 = new ProductDetail(MOCK_ID_2, "name3", ZERO, TRUE);
    static final ProductDetail MOCK_PRODUCT_DETAIL_4 = new ProductDetail(MOCK_ID_2, "name4", ZERO, TRUE);

    @Autowired
    CacheManager cacheManager;
    @Autowired
    SimilarProductsService similarProductsService;
    @MockBean
    ProductsService productsService;

    @BeforeEach
    void beforeEach() {
        cacheManager.getCacheNames().stream().map(cacheManager::getCache).forEach(Cache::clear);
    }

    @Test
    void whenGetSimilarIdsReturnNull_thenReturnNull() {
        when(productsService.getSimilarIds(MOCK_ID)).thenReturn(null);
        assertThat(similarProductsService.getSimilarProducts(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenGetSimilarIdsReturnsEmptyList_thenReturnEmptyList() {
        when(productsService.getSimilarIds(MOCK_ID)).thenReturn(Collections.emptyList());
        List<ProductDetail> response = similarProductsService.getSimilarProducts(MOCK_ID);
        assertThat(response, not(nullValue()));
        assertThat(response, is(empty()));
    }


    @Test
    void whenGetSimilarIdsReturnList_thenReturnProductDetailList() {
        when(productsService.getSimilarIds(MOCK_ID)).thenReturn(List.of(MOCK_ID_2, MOCK_ID_3, MOCK_ID_4));
        when(productsService.getProductDetail(MOCK_ID_2)).thenReturn(MOCK_PRODUCT_DETAIL_2);
        when(productsService.getProductDetail(MOCK_ID_3)).thenReturn(MOCK_PRODUCT_DETAIL_3);
        when(productsService.getProductDetail(MOCK_ID_4)).thenReturn(MOCK_PRODUCT_DETAIL_4);

        List<ProductDetail> response = similarProductsService.getSimilarProducts(MOCK_ID);
        assertThat(response, not(nullValue()));
        assertThat(response, is(List.of(MOCK_PRODUCT_DETAIL_2, MOCK_PRODUCT_DETAIL_3, MOCK_PRODUCT_DETAIL_4)));
    }

    @Test
    void whenGetSimilarProductsIsCalledSeveralTimeWithSameIds_thenReturnSameProductDetail() {
        List<ProductDetail> mockResponse1 = List.of(MOCK_PRODUCT_DETAIL_2, MOCK_PRODUCT_DETAIL_3);

        when(productsService.getProductDetail(MOCK_ID_2)).thenReturn(MOCK_PRODUCT_DETAIL_2);
        when(productsService.getProductDetail(MOCK_ID_3)).thenReturn(MOCK_PRODUCT_DETAIL_3);
        when(productsService.getProductDetail(MOCK_ID_4)).thenReturn(MOCK_PRODUCT_DETAIL_4);
        when(productsService.getSimilarIds(MOCK_ID))
                .thenReturn(List.of(MOCK_ID_2, MOCK_ID_3))
                .thenReturn(List.of(MOCK_ID_3, MOCK_ID_4))
                .thenThrow(new RuntimeException());

        Stream.of(MOCK_ID, MOCK_ID, MOCK_ID)
                .map(similarProductsService::getSimilarProducts)
                .forEach(productDetail -> {
                    assertThat(productDetail, not(nullValue()));
                    assertThat(productDetail, is(mockResponse1));
                });
    }

    @Test
    void whenGetSimilarProductsIsCalledSeveralTimeWithDifferentIds_thenReturnDifferentProductDetail() {
        List<ProductDetail> mockResponse1 = List.of(MOCK_PRODUCT_DETAIL_2, MOCK_PRODUCT_DETAIL_3);
        List<ProductDetail> mockResponse2 = List.of(MOCK_PRODUCT_DETAIL_3, MOCK_PRODUCT_DETAIL_4);

        when(productsService.getProductDetail(MOCK_ID_2)).thenReturn(MOCK_PRODUCT_DETAIL_2);
        when(productsService.getProductDetail(MOCK_ID_3)).thenReturn(MOCK_PRODUCT_DETAIL_3);
        when(productsService.getProductDetail(MOCK_ID_4)).thenReturn(MOCK_PRODUCT_DETAIL_4);
        when(productsService.getSimilarIds(MOCK_ID)).thenReturn(List.of(MOCK_ID_2, MOCK_ID_3));
        when(productsService.getSimilarIds(MOCK_ID_2)).thenReturn(List.of(MOCK_ID_3, MOCK_ID_4));

        List<ProductDetail> response1 = similarProductsService.getSimilarProducts(MOCK_ID);
        List<ProductDetail> response2 = similarProductsService.getSimilarProducts(MOCK_ID_2);

        assertThat(response1, not(nullValue()));
        assertThat(response2, not(nullValue()));
        assertThat(response1, is(mockResponse1));
        assertThat(response2, is(mockResponse2));
    }

    @Test
    void whenAddProductDetailGetNullProductId_thenItIsNotAddedToTheResponseList() {
        List<ProductDetail> list = new ArrayList<>();
        similarProductsService.addProductDetail(list, null);
        assertThat(list, is(empty()));
    }

    @Test
    void whenGetProductDetailReturnNull_thenItIsNotAddedToTheResponseList() {
        when(productsService.getProductDetail(MOCK_ID)).thenReturn(null);
        List<ProductDetail> list = new ArrayList<>();
        similarProductsService.addProductDetail(list, MOCK_ID);
        assertThat(list, is(empty()));
    }

    @Test
    void whenGetProductDetailReturnValidResponse_thenItIsAddedToTheResponseList() {
        ProductDetail mockResponse = new ProductDetail(MOCK_ID, "name", ZERO, TRUE);
        when(productsService.getProductDetail(MOCK_ID)).thenReturn(mockResponse);
        List<ProductDetail> list = new ArrayList<>();
        similarProductsService.addProductDetail(list, MOCK_ID);
        assertThat(list, hasSize(1));
        assertThat(list.get(0), is(mockResponse));
    }

}
