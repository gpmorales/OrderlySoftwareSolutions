package com.OrderlyAPI.Menu.service;

import com.OrderlyAPI.Menu.controllers.MenuController;
import com.OrderlyAPI.Menu.model.MenuItem;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;

@Service
public class MenuService {

    private final Logger logger = LoggerFactory.getLogger(MenuController.class);

    private final DecimalFormat decimalFormatter = new DecimalFormat("#.##");

    private final Validator validator;

    private final Comparator<MenuItem> menuItemComparator;

    private final MongoTemplate mongoMenuDAO;

    MenuService(Validator validator, Comparator<MenuItem> menuItemComparator, MongoTemplate mongoMenuDAO) {
        this.menuItemComparator = menuItemComparator;
        this.mongoMenuDAO = mongoMenuDAO;
        this.validator = validator;
    }

    public Optional<Collection<MenuItem>> createMenu(String restaurantId, List<MenuItem> menu) {
        if (mongoMenuDAO.collectionExists(restaurantId)) {
            return Optional.empty();
        }
        // Validate menu to ensure seamless frontend integration
        List<MenuItem> validatedMenu = validateMenu(restaurantId, menu);
        return Optional.of(mongoMenuDAO.insert(validatedMenu, restaurantId));
    }

    public Optional<Collection<MenuItem>> updateMenu(String restaurantId, List<MenuItem> newMenu) {
        if (!mongoMenuDAO.collectionExists(restaurantId)) {
            return Optional.empty();
        }
        // Add to current menu
        List<MenuItem> validatedMenu = validateMenu(restaurantId, newMenu);
        return Optional.of(mongoMenuDAO.insert(validatedMenu, restaurantId));
    }

    public Optional<MenuItem> updateMenuItem(String restaurantId, MenuItem updatedMenuItem) {
        try {
            if (!mongoMenuDAO.collectionExists(restaurantId)) {
                return Optional.empty();
            }
            return Optional.of(mongoMenuDAO.save(updatedMenuItem, restaurantId));

        } catch (MongoException e) {
            return Optional.empty();
        }
    }

    public Optional<MenuItem> addMenuItem(String restaurantId, MenuItem newMenuItem) {
        if (!mongoMenuDAO.collectionExists(restaurantId)) {
            return Optional.empty();
        }

        final String menuItemName = newMenuItem.getItem();
        Query currentItemQuery = new Query(Criteria.where("item").is(menuItemName));
        List<MenuItem> searchResult = mongoMenuDAO.find(currentItemQuery, MenuItem.class, restaurantId);

        if (searchResult.isEmpty()) {
            return Optional.of(mongoMenuDAO.save(newMenuItem, restaurantId));
        }

        return Optional.empty(); // Item alr exists
    }

    public List<Boolean> removeMenuItems(String restaurantId, List<String> menuItems) {
        if (!mongoMenuDAO.collectionExists(restaurantId)) {
            return List.of();
        }

        final List<Boolean> acknowledgedRemovals = new ArrayList<>();

        // Erase current menu collection
        menuItems.forEach(menuItemName -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("item").is(menuItemName));
            if (mongoMenuDAO.findAndRemove(query, MenuItem.class, restaurantId) != null) {
                acknowledgedRemovals.add(true);
            }
        });

        return acknowledgedRemovals;
    }

    public Boolean clearMenu(String restaurantId) {
        // removes all elements from that restaurant Collection
        if (!mongoMenuDAO.collectionExists(restaurantId)) return false;

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").exists(true));

        final long menuSize = mongoMenuDAO.count(query, restaurantId);
        final long itemsRemoved = mongoMenuDAO.remove(query, restaurantId).getDeletedCount();

        return menuSize == itemsRemoved;
    }


    // TODO -> Render the menu page but not the item images
    public Optional<List<MenuItem>> getAllMenuItemFields(String restaurantId) {
        if (!mongoMenuDAO.collectionExists(restaurantId)) return Optional.empty();

        final List<MenuItem> menuItemFields = new ArrayList<>();
        final List<MenuItem> menuItems = mongoMenuDAO.findAll(MenuItem.class, restaurantId);

        for (MenuItem item : menuItems) {
            MenuItem menuItem = MenuItem.builder()
                    .item(item.getItem())
                    .price(item.getPrice())
                    .description(item.getDescription())
                    .addOns(item.getAddOns())
                    .sides(item.getSides())
                    .imageProfile(null)
                    .specialInstructions(item.getSpecialInstructions())
                    .build();

            menuItemFields.add(menuItem);
        }

        menuItemFields.sort(menuItemComparator);

        return Optional.of(menuItemFields);
    }

    // TODO -> Renders the menu page images only!
    public Optional<List<InputStream>> getAllImageProfiles(String restaurantId) {
        if (!mongoMenuDAO.collectionExists(restaurantId)) return Optional.empty();

        final List<InputStream> imageProfiles = new ArrayList<>();
        final List<MenuItem> menuItems = mongoMenuDAO.findAll(MenuItem.class, restaurantId);

        menuItems.sort(menuItemComparator);

        for (MenuItem menuItem : menuItems) {
            try {
                if (menuItem.getImageProfile() != null && menuItem.getImageProfile().available() > 0) {
                    logger.warn(menuItem.getItem());
                    imageProfiles.add(menuItem.getImageProfile());
                }
            } catch (IOException e) {
                imageProfiles.add(null); //TODO -> better impl?
            }
        }

        return Optional.of(imageProfiles);
    }

    public Optional<Boolean> uploadImageProfile(String restaurantId, String item, MultipartFile imageProfile) {
        if (!mongoMenuDAO.collectionExists(restaurantId)) return Optional.empty();

        Query query = new Query();
        query.addCriteria(Criteria.where("item").is(item));

        if (mongoMenuDAO.find(query, MenuItem.class, restaurantId).isEmpty()) {
            return Optional.empty();
        }

        try {
            final Update updateItem = new Update().set("imageProfile" , imageProfile.getInputStream());

            final UpdateResult operationResult = mongoMenuDAO.updateFirst(query, updateItem, MenuItem.class, restaurantId);

            if (!operationResult.wasAcknowledged()) {
                return Optional.empty();
            }
            return Optional.of(Boolean.TRUE);

        } catch (IOException e) {
            return Optional.empty();
        }
    }


    /** Helper Methods */
    private List<MenuItem> validateMenu(String restaurantId, List<MenuItem> menu) {
        return menu.stream().flatMap(menuItem -> {
            menuItem.setPrice(Double.parseDouble(decimalFormatter.format(menuItem.getPrice())));
            if (validMenuItem(restaurantId, menuItem).isPresent()) return Stream.of(menuItem);
            else return Stream.empty();
        }).toList();
    }

    private Optional<MenuItem> validMenuItem(String restaurantId, MenuItem menuItem) {
        Errors bindingResult = new BeanPropertyBindingResult(menuItem, "MenuItem");
        validator.validate(menuItem,bindingResult);

        if (!bindingResult.hasErrors()) { // Check if all required fields have been filled
            Query searchQuery = new Query(Criteria.where("item").is(menuItem.getItem()));
            // Checks if item is unique and is not alr present in menu / collection
            if (mongoMenuDAO.findOne(searchQuery, MenuItem.class, restaurantId) == null) {
                return Optional.of(menuItem);
            }
        }
        return Optional.empty();
    }

}
