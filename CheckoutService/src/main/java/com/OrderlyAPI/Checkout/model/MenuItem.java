package com.OrderlyAPI.Checkout.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.List;

@Data
@Component
@NoArgsConstructor
public class MenuItem {

    @NotNull
    @NotEmpty
    @Pattern(regexp = "[A-Za-z0-9]{2,20}")
    private String itemName;

    @NotNull
    private Double itemPrice;

    private List<MenuItemOption> sides;

    private List<MenuItem> addOns;

    private InputStream imageProfile;

    @NotEmpty
    @NotNull
    private String specialInstructions;

    public MenuItem(String itemName, double itemPrice, List<MenuItemOption> sides, List<MenuItem> addOns, InputStream imageProfile, String specialInstructions) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.sides = sides;
        this.addOns = addOns;
        this.imageProfile = imageProfile;
        this.specialInstructions = specialInstructions;
    }

    @Data
    private static class MenuItemOption {
        private String itemOptionName;
        private double itemOptionPrice;
    }

}
