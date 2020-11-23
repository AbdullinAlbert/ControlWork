package com.albertabdullin.controlwork.models;

import java.util.ArrayList;
import java.util.List;

public class SortedEqualSignsList {
    private List<OrderedSign> list;

    private static List<OrderedSign> constantList = null;

    public SortedEqualSignsList(List<String> l) {
        list = new ArrayList<>();
        constantList = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            list.add(new OrderedSign(i, l.get(i)));
            constantList.add(new OrderedSign(i, l.get(i)));
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

    public static int getID(String sign) {
        int i = 0;
        if (constantList == null) throw new RuntimeException("Вызов SortedEqualSignsList с пустым списком");
        else while (!sign.equals(constantList.get(i).getSign()) && i < constantList.size()) i++;
        if (i == constantList.size()) throw new RuntimeException("Ошибка в выборе переменной");
        return i;
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

