package com.javierdelgado.similarproducts.models;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void whenIdIsNull_thenValidationDoesNotPass() {
        ProductDetail productDetail = new ProductDetail(null, "name", BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "id"), hasSize(1));
    }

    @Test
    public void whenIdIsEmpty_thenValidationDoesNotPass() {
        ProductDetail productDetail = new ProductDetail("", "name", BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "id"), hasSize(1));
    }

    @Test
    public void whenIdIsNotEmpty_thenValidationPasses() {
        ProductDetail productDetail = new ProductDetail("id", "name", BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "id"), hasSize(0));
    }

    @Test
    public void whenNameIsNull_thenValidationDoesNotPass() {
        ProductDetail productDetail = new ProductDetail("id", null, BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "name"), hasSize(1));
    }

    @Test
    public void whenNameIsEmpty_thenValidationDoesNotPass() {
        ProductDetail productDetail = new ProductDetail("id", "", BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "name"), hasSize(1));
    }

    @Test
    public void whenNameIsNotEmpty_thenValidationPasses() {
        ProductDetail productDetail = new ProductDetail("id", "name", BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "name"), hasSize(0));
    }

    @Test
    public void whenPriceIsNull_thenValidationDoesNotPass() {
        ProductDetail productDetail = new ProductDetail("id", "name", null, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "price"), hasSize(1));
    }

    @Test
    public void whenPriceIsNotNull_thenValidationPasses() {
        ProductDetail productDetail = new ProductDetail("id", "name", BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "price"), hasSize(0));
    }

    @Test
    public void whenAvailabilityIsNull_thenValidationDoesNotPass() {
        ProductDetail productDetail = new ProductDetail("id", "name", BigDecimal.ZERO, null);
        assertThat(validator.validateProperty(productDetail, "availability"), hasSize(1));
    }

    @Test
    public void whenAvailabilityIsNotNull_thenValidationPasses() {
        ProductDetail productDetail = new ProductDetail("id", "name", BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validateProperty(productDetail, "availability"), hasSize(0));
    }

    @Test
    public void whenAllFieldsAreValid_thenAllValidationPasses() {
        ProductDetail productDetail = new ProductDetail("id", "name", BigDecimal.ZERO, Boolean.TRUE);
        assertThat(validator.validate(productDetail), hasSize(0));
    }
}