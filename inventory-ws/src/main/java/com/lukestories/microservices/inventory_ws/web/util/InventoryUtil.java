package com.lukestories.microservices.inventory_ws.web.util;

import com.lukestories.microservices.inventory_ws.web.model.Inventory;

import java.util.HashSet;

public class InventoryUtil {

    public static HashSet getList() {

        int rows = 100;
        var set = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            var in = Inventory.builder().productId((long) i).amount((int)  (Math.random()*100)+2).build();
            set.add(in);
        }
        if (set.size() >= 1) {
            if (set.iterator().hasNext()) {
                var o = (Inventory)set.iterator().next();
                o.setAmount(0);
            }
        }
        return set;
    }
}
