package com.trc.android.router.compile;

public class Item {
    public static final String DELIMITER = "__--__";
    public String className;
    public String des;
    public String[] uris;
    public String meta;


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(className)
                .append(DELIMITER)
                .append(des)
                .append(DELIMITER)
                .append(meta);
        for (String uri : uris) {
            stringBuilder.append(DELIMITER).append(uri);
        }
        return stringBuilder.toString();
    }

    public Item() {

    }

    public static Item parse(String s) {
        Item item = new Item();
        String[] values = s.split(DELIMITER);
        item.className = values[0];
        item.des = values[1];
        item.meta = values[2];
        item.uris = new String[values.length - 3];
        for (int i = 0; i < item.uris.length; i++) {
            item.uris[i] = values[i + 3];
        }
        return item;
    }
}
