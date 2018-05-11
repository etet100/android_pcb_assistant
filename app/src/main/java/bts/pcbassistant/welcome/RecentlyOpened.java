package bts.pcbassistant.welcome;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Pattern;

import bts.pcbassistant.data.EagleDataSource;

/**
 * Created by a on 2017-06-17.
 */

public class RecentlyOpened {

    private static Context context;
    private static ArrayList<RecentlyOpenedItem> list;

    public static void init(Context context) {
        RecentlyOpened.context = context;
        RecentlyOpened.list = new ArrayList<>();

        String[] s = getPrefs().getString("recentFiles", "").split(Pattern.quote("|||"));
        for (int i = 0; i < s.length; i++) {
            if (!s[i].equals("")) add(s[i]);
        }
    }

    public static RecentlyOpenedItem add(String name, String source) {
        RecentlyOpenedItem rec = new RecentlyOpenedItem(name, source);
        return rec;
    }

    public static RecentlyOpenedItem add(String serialized) {
        RecentlyOpenedItem rec = new RecentlyOpenedItem(serialized);
        return rec;
    }

    public static void add(RecentlyOpenedItem rec) {
        list.add(0, rec);
        while (list.size() > 10) {
            list.remove(10);
        }
    }

    public static int getCount() {
        return list.size();
    }

    public static RecentlyOpenedItem getItem(int i) {
        return list.get(i);
    }

    public static String stringify() {
        String b = "";
        int pos = 0;
        for (RecentlyOpenedItem item : list) {
            Log.d("save", String.format("%d %s", pos++, item.stringify()));
            b += item.stringify() + "|||";
        }
        return b;
    }

    public static class RecentlyOpenedItem {

        public String name;
        public String dateTime;
        public boolean board;
        public boolean schematic;
        public String source;

        public String getDetails() {
            return "czas: "+this.dateTime+", źródło: "+this.source;
        }

        public String getPath() {
            return this.source+":"+this.name;
        }

        public RecentlyOpenedItem(String name, String source) {
            this.name = name;
            this.source = source;
            this.dateTime = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString();
            RecentlyOpened.add(this);
        }

        public RecentlyOpenedItem(String serialized) {
            String[] s = serialized.split(Pattern.quote("||"));
            this.name = s[0];
            this.source = s[1];
            this.dateTime = s[2];
            this.board = Boolean.valueOf(s[3]);
            this.schematic = Boolean.valueOf(s[4]);
            RecentlyOpened.list.add(this);
        }

        public void update(EagleDataSource.TYPE type) {
            switch (type) {
                case Board:
                    this.board = true;
                    break;
                case Schematic:
                    this.schematic = true;
                    break;
            }
            RecentlyOpened.save();
        }

        public String stringify() {
            return String.format(
                    "%s||%s||%s||%b||%b",
                    name, source, dateTime, board, schematic
            );
        }
    }

    private static SharedPreferences getPrefs() {
        return context.getSharedPreferences(
                "recentFiles",
                Context.MODE_PRIVATE);
    }

    public static void save() {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString("recentFiles", stringify());
        editor.commit();
    }

}
