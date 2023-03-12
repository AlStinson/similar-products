package com.javierdelgado.similarproducts.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Model for ProductDetail
 *
 * @see #/components/schemas/ProductDetail in
 * <a href="https://github.com/dalogax/backendDevTest/blob/main/similarProducts.yaml">similar products specification</a>
 * or <a href="https://github.com/dalogax/backendDevTest/blob/main/existingApis.yaml">existing apis specification</a>
 */
public class ProductDetail {

    @NotNull
    @Size(min = 1)
    private String id;

    @NotNull
    @Size(min = 1)
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Boolean availability;

    public ProductDetail(String id, String name, BigDecimal price, Boolean availability) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availability = availability;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "ProductDetail{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", availability=" + availability +
                '}';
    }
}
