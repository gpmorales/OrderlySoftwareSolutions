package com.OrderlyAPI.Menu.controllers;

import com.OrderlyAPI.Menu.model.MenuItem;
import com.OrderlyAPI.Menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("OrderlyAPI/admin/")
public class MenuController {
    // Deals with restaurant logic to upload and display the menu on page
    private final MenuService menuService;

    @Autowired
    MenuController(MenuService menuService) {
        this.menuService = menuService;
    }


    // TODO => upload ResponseEntity body?
    @PostMapping(value = "/buildMenu", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> buildMenu(@RequestParam String restaurantId,
                                            @RequestBody List<MenuItem> restaurantMenu) {
        try {
            if (restaurantId == null || restaurantId.isEmpty()) {
                return ResponseEntity.badRequest().body("Restaurant Id is empty or null");
            } else if (restaurantMenu.isEmpty()) {
                return ResponseEntity.badRequest().body("Menu is empty");
            }

            final Optional<Collection<MenuItem>> persistedMenu = menuService.createMenu(restaurantId, restaurantMenu);

            if (persistedMenu.isEmpty()) {
                return ResponseEntity.badRequest().body("Could not create Mongo collection with Menu Items, might already exist");
            }

            if (persistedMenu.get().size() < restaurantMenu.size()) {
                return ResponseEntity.accepted().body("Some items could not added to the menu");
            }

            return ResponseEntity.ok().body("Successfully uploaded menu for " + restaurantId);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong when trying to execute this operation\n" + e.getMessage());
        }
    }


    @PostMapping(value = "/updateMenu", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updateMenu(@RequestParam String restaurantId,
                                             @RequestBody List<MenuItem> updatedMenu) {
        try {
            if (restaurantId == null || restaurantId.isEmpty() || updatedMenu.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request, menu is empty or restaurant id is empty");
            }

            final Optional<Collection<MenuItem>> newMenu = menuService.updateMenu(restaurantId, updatedMenu);

            if (newMenu.isEmpty()) {
                return ResponseEntity.internalServerError().body("Could update menu to Mongo");
            }

            if (newMenu.get().size() < updatedMenu.size()) {
                return ResponseEntity.accepted().body("Some menu items could not be added to the menu");
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong when trying to execute this operation\n" + e.getMessage());
        }
    }


    @PostMapping(value = "/updateItem", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updateMenuItem(@RequestParam String restaurantId,
                                                 @Valid @RequestBody MenuItem updatedMenuItem,
                                                 BindingResult bindingResult) {
        try {
            if (restaurantId == null || restaurantId.isEmpty()) {
                return ResponseEntity.badRequest().body("Restaurant Id is empty or null");
            }

            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body("Invalid request");
            }

            final Optional<MenuItem> updatedMenu = menuService.updateMenuItem(restaurantId, updatedMenuItem);

            if (updatedMenu.isEmpty()) {
                return ResponseEntity.badRequest().body("Item doesn't exist");
            }

            return ResponseEntity.ok().body("Successfully updated the item on your Menu");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong when trying to execute this operation\n" + e.getMessage());
        }
    }


    @PostMapping(value = "/addItem", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> addMenuItem(@RequestParam String restaurantId,
                                              @Valid @RequestBody MenuItem newMenuItem,
                                              BindingResult bindingResult) {
        try {
            if (restaurantId == null || restaurantId.isEmpty()) {
                return ResponseEntity.badRequest().body("Restaurant Id is empty or null");
            }

            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body("Invalid request");
            }

            final Optional<MenuItem> updatedMenu = menuService.addMenuItem(restaurantId, newMenuItem);

            if (updatedMenu.isEmpty()) {
                return ResponseEntity.badRequest().body("Item already exists");
            }

            return ResponseEntity.ok().body("Successfully added the item to your Menu");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong when trying to execute this operation\n" + e.getMessage());
        }
    }


    @PostMapping(value = "/deleteMenuItems", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> deleteMenuItems(@RequestParam String restaurantId,
                                                  @RequestBody List<String> itemsToBeDeleted) {
        try {
            if (restaurantId == null || restaurantId.isEmpty()) {
                return ResponseEntity.badRequest().body("Restaurant Id is empty or null");
            }

            if (menuService.removeMenuItems(restaurantId, itemsToBeDeleted).size() < itemsToBeDeleted.size()) {
                return ResponseEntity.badRequest().body("Not all items could be removed");
            }
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong when trying to execute this operation\n" + e.getMessage());
        }
    }


    @GetMapping("/clearMenu")
    public ResponseEntity<Object> clearMenu(@RequestParam String restaurantId) {
        try {
            if (restaurantId == null || restaurantId.isEmpty()) {
                return ResponseEntity.badRequest().body("Restaurant Id is empty or null");
            }

            if (!menuService.clearMenu(restaurantId)) {
                return ResponseEntity.badRequest().body("Menu items could not be removed");
            }
            return ResponseEntity.ok().body("Successfully removed your menu");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong when trying to execute this operation\n" + e.getMessage());
        }
    }


    /** Frontend rendering */

    @GetMapping("/getMenuItems")
    public ResponseEntity<List<MenuItem>> displayMenuFields(@RequestParam String restaurantId) {
        try {
            if (restaurantId == null || restaurantId.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            final Optional<List<MenuItem>> menuItemFields = menuService.getAllMenuItemFields(restaurantId);

            if (menuItemFields.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().body(menuItemFields.get());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/renderImages")
    public ResponseEntity<List<InputStream>> getImageProfiles(@RequestParam String restaurantId) {
        try {
            if (restaurantId == null || restaurantId.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            final Optional<List<InputStream>> imageProfiles = menuService.getAllImageProfiles(restaurantId);

            if (imageProfiles.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            return ResponseEntity.ok().body(imageProfiles.get());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping(value = "/uploadImageProfile", consumes = {"multipart/form-data"})
    public ResponseEntity<Object> attachMediaToMenuItem(@RequestParam String restaurantId,
                                                        @RequestParam String itemName,
                                                        @RequestBody MultipartFile itemImage) {
        try {
            if (restaurantId == null || restaurantId.isEmpty()) {
                return ResponseEntity.badRequest().body("Restaurant Id is empty or null");
            }

            if (itemName.isEmpty() || itemImage.isEmpty() || itemImage.getBytes().length > 1_000_000) {
                return ResponseEntity.badRequest().build();
            }

            final Optional<Boolean> imageUpload = menuService.uploadImageProfile(restaurantId, itemName, itemImage);

            if (imageUpload.isEmpty()) {
                return ResponseEntity.internalServerError().build();
            }

            return ResponseEntity.ok().body("Successfully added the image to your menu");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong when trying to execute this operation\n" + e.getMessage());
        }
    }

}
