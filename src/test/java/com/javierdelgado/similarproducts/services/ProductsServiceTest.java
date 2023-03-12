package com.javierdelgado.similarproducts.services;

import com.javierdelgado.similarproducts.models.ProductDetail;
import com.javierdelgado.similarproducts.services.proxies.ProductsServiceProxy;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductsServiceTest {

    public static final String MOCK_ID = "mockId";

    @Autowired
    CacheManager cacheManager;
    @Autowired
    ProductsService productsService;
    @MockBean
    ProductsServiceProxy productsServiceProxy;

    @BeforeEach
    void beforeEach() {
        cacheManager.getCacheNames().stream().map(cacheManager::getCache).forEach(Cache::clear);
    }

    @Test
    void whenProxyGetSimilarIdsThrowsFeignNotFoundException_thenReturnNull() {
        Request request = Request.create(Request.HttpMethod.GET, "ProductsServiceTestUrl", Collections.emptyMap(),
                Request.Body.empty(), null);
        when(productsServiceProxy.getSimilarIds(MOCK_ID))
                .thenThrow(new FeignException.NotFound("ProductNotFound", request, null, null));
        assertThat(productsService.getSimilarIds(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetSimilarIdsThrowsFeignInternalServerErrorException_thenReturnNull() {
        Request request = Request.create(Request.HttpMethod.GET, "ProductsServiceTestUrl", Collections.emptyMap(),
                Request.Body.empty(), null);
        when(productsServiceProxy.getSimilarIds(MOCK_ID))
                .thenThrow(new FeignException.InternalServerError("InternalServerError", request, null, null));
        assertThat(productsService.getSimilarIds(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetSimilarIdsThrowsRuntimeException_thenReturnNull() {
        when(productsServiceProxy.getSimilarIds(MOCK_ID)).thenThrow(new RuntimeException());
        assertThat(productsService.getSimilarIds(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetSimilarIdsReturnsNullBody_thenReturnNull() {
        when(productsServiceProxy.getSimilarIds(MOCK_ID)).thenReturn(null);
        assertThat(productsService.getSimilarIds(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetSimilarIdsReturnsCorrectResponse_thenReturnListOfSimilarIds() {
        List<String> mockResponse = List.of(MOCK_ID);
        when(productsServiceProxy.getSimilarIds(MOCK_ID)).thenReturn(mockResponse);
        List<String> response = productsService.getSimilarIds(MOCK_ID);
        assertThat(response, not(nullValue()));
        assertThat(response, hasSize(1));
        assertThat(response.get(0), is(MOCK_ID));
    }

    @Test
    void whenProxyGetProductDetailThrowsFeignNotFoundException_thenReturnNull() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "ProductsServiceTestUrl",
                Collections.emptyMap(),
                Request.Body.empty(),
                null);
        when(productsServiceProxy.getProductDetail(MOCK_ID))
                .thenThrow(new FeignException.NotFound("ProductNotFound", request, null, null));
        assertThat(productsService.getProductDetail(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetProductDetailThrowsFeignInternalServerErrorException_thenReturnNull() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "ProductsServiceTestUrl",
                Collections.emptyMap(),
                Request.Body.empty(),
                null);
        when(productsServiceProxy.getProductDetail(MOCK_ID))
                .thenThrow(new FeignException.InternalServerError("InternalServerError", request, null, null));
        assertThat(productsService.getProductDetail(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetProductDetailThrowsRuntimeException_thenReturnNull() {
        when(productsServiceProxy.getProductDetail(MOCK_ID)).thenThrow(new RuntimeException());
        assertThat(productsService.getProductDetail(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetProductDetailReturnsNullBody_thenReturnNull() {
        when(productsServiceProxy.getProductDetail(MOCK_ID)).thenReturn(null);
        assertThat(productsService.getProductDetail(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetProductDetailReturnsMalformedProductDetail_thenReturnNull() {
        when(productsServiceProxy.getProductDetail(MOCK_ID)).thenReturn(new ProductDetail(null, null, null, null));
        assertThat(productsService.getProductDetail(MOCK_ID), is(nullValue()));
    }

    @Test
    void whenProxyGetProductDetailReturnsCorrectResponse_thenReturnProductDetail() {
        ProductDetail mockResponse = new ProductDetail(MOCK_ID, "name", BigDecimal.ZERO, Boolean.TRUE);
        when(productsServiceProxy.getProductDetail(MOCK_ID)).thenReturn(mockResponse);
        ProductDetail response = productsService.getProductDetail(MOCK_ID);
        assertThat(response, not(nullValue()));
        assertThat(response, is(mockResponse));
    }

    @Test
    void whenGetProductDetailIsCalledSeveralTimeWithSameIds_thenReturnSameProductDetail() {
        ProductDetail mockResponse1 = new ProductDetail(MOCK_ID, "name", BigDecimal.ZERO, Boolean.TRUE);
        ProductDetail mockResponse2 = new ProductDetail("mockId2", "name2", BigDecimal.ONE, Boolean.FALSE);
        when(productsServiceProxy.getProductDetail(MOCK_ID))
                .thenReturn(mockResponse1)
                .thenReturn(mockResponse2)
                .thenThrow(new RuntimeException());
        Stream.of(MOCK_ID, MOCK_ID, MOCK_ID)
                .map(productsService::getProductDetail)
                .forEach(productDetail -> {
                    assertThat(productDetail, not(nullValue()));
                    assertThat(productDetail, is(mockResponse1));
                });
    }

    @Test
    void whenGetProductDetailIsCalledSeveralTimeWithDifferentIds_thenReturnDifferentProductDetail() {
        ProductDetail mockResponse1 = new ProductDetail(MOCK_ID, "name", BigDecimal.ZERO, Boolean.TRUE);
        ProductDetail mockResponse2 = new ProductDetail("mockId2", "name2", BigDecimal.ONE, Boolean.FALSE);
        when(productsServiceProxy.getProductDetail(anyString()))
                .thenReturn(mockResponse1)
                .thenReturn(mockResponse2);
        ProductDetail response1 = productsService.getProductDetail(MOCK_ID);
        ProductDetail response2 = productsService.getProductDetail("mockId2");

        assertThat(response1, not(nullValue()));
        assertThat(response2, not(nullValue()));
        assertThat(response1, is(mockResponse1));
        assertThat(response2, is(mockResponse2));
    }

}