package com.OrderlyAPI.Menu.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@Document
public class MenuItem {

    @Id
    @NotNull
    @NotEmpty
    @Pattern(regexp = "[A-Za-z0-9]{2,20}")
    private String item;

    @NotNull
    private Double price;

    //@NotNull
    //private Boolean activeItem;

    @NotNull
    @NotEmpty
    private String description;

    private List<MenuItemOption> sides;

    private List<MenuItem> addOns;

    private InputStream imageProfile; //TODO ->

    @NotEmpty
    @NotNull
    private String specialInstructions;

    public MenuItem(String item, Double price, String description, List<MenuItemOption> sides, List<MenuItem> addOns, InputStream imageProfile, String specialInstructions) {
        this.item = item;
        this.price = price;
        this.description = description;
        this.sides = sides;
        this.addOns = addOns;
        this.imageProfile = imageProfile;
        this.specialInstructions = specialInstructions;
    }

    @Data
    private static class MenuItemOption {
        private String sideItemName;
        private double sideItemPrice;
    }

}
