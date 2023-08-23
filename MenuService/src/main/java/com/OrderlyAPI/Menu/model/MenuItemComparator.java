package com.OrderlyAPI.Menu.model;

import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class MenuItemComparator implements Comparator<MenuItem> {

    @Override
    public int compare(MenuItem itemA, MenuItem itemB) {
        return itemA.getItem().compareTo(itemB.getItem());
    }

}
