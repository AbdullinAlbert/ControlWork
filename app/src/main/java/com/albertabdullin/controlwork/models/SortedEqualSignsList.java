package com.albertabdullin.controlwork.models;

import java.util.ArrayList;
import java.util.List;

public class SortedEqualSignsList {
    private List<OrderedSign> list;

    public SortedEqualSignsList() {
        list = new ArrayList<>();
    }

    public SortedEqualSignsList(List<String> l) {
        list = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            list.add(new OrderedSign(i, l.get(i)));
        }
    }

    public void add(OrderedSign orderedSign) {
        int i = 0;
        boolean b = true;
        while (i < list.size() && b) {
            if (orderedSign.getID() > list.get(i).getID()) i++;
            else {
                list.add(i, orderedSign);
                b = false;
            }
        }
        if (b) list.add(orderedSign);
    }

    public OrderedSign remove(String sign) {
        int i = 0;
        while (!sign.equals(list.get(i).getSign())) i++;
        return list.remove(i);
    }

    public OrderedSign get(int position) {
        return list.get(position);
    }

    public int size() {
        return list.size();
    }
}

