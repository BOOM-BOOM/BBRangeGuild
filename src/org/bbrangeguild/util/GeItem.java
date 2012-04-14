package org.bbrangeguild.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vulcan
 */
public final class GeItem {

    private int price;
    private String name;
    public static final String WEBSITE = "http://services.runescape.com/m=itemdb_rs/api/catalogue/detail.json?item=";

    private GeItem(final String name, final int price) {
        this.name = name;
        this.price = price;
    }

    public static GeItem lookup(int itemId) {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(WEBSITE + itemId).openStream()));
            final StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                json.append(line);
            final String name = searchJSON(json.toString(), "item", "name");
            final int price = parseMultiplier(searchJSON(json.toString(), "item", "current", "price"));
            return new GeItem(name, price);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String searchJSON(final String json, final String...keys) {
        String search = "\"" + keys[0] + "\":";
        int idx = json.indexOf(search) + search.length();
        if (keys.length > 1 && json.charAt(idx) == '{') {
            String[] subKeys = new String[keys.length - 1];
            System.arraycopy(keys, 1, subKeys, 0, subKeys.length);
            return searchJSON(json.substring(idx), subKeys);
        }
        Pattern p = Pattern.compile(".*?[,\\{]\\\"" + keys[0] + "\\\":(-?[\\d]|[\\\"\\d].*?[kmb]?[^\\\\][\\\"\\d])[,\\}].*");
        Matcher m = p.matcher(json);
        if (m.find()) {
            String value = m.group(1);
            if (value.matches("\\\".*?\\\"")) {
                value = value.substring(1, value.length() - 1);
            }
            return value;
        }
        return "";
    }

    private static int parseMultiplier(final String str) {
        if (str.matches("-?\\d+(\\.\\d+)?[kmb]")) {
            return (int) (Double.parseDouble(str.substring(0, str.length() - 1))
                    * (str.endsWith("b") ? 1000000000D : str.endsWith("m") ? 1000000
                    : str.endsWith("k") ? 1000 : 1));
        } else {
            return Integer.parseInt(str);
        }
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

}
