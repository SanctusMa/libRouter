package com.trc.android.router.compile;

public class Item {
    public static final String DELIMITER = "__--__";
    public static final String BREAK_LINE = "--__--";
    public String className;
    public String des;
    public String[] uris;
    public String meta;


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(className)
                .append(DELIMITER)
                .append(String.valueOf(des).replaceAll("\n", BREAK_LINE))
                .append(DELIMITER)
                .append(String.valueOf(meta).replaceAll("\n", BREAK_LINE));
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
        item.des = values[1].replaceAll(BREAK_LINE, "\n");
        item.meta = values[2].replaceAll(BREAK_LINE, "\n");
        item.uris = new String[values.length - 3];
        for (int i = 0; i < item.uris.length; i++) {
            item.uris[i] = values[i + 3];
        }
        return item;
    }
}
