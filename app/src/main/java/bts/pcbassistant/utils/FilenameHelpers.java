package bts.pcbassistant.utils;

import bts.pcbassistant.R;

/**
 * Created by a on 2017-05-17.
 */

public class FilenameHelpers {

    //http://stackoverflow.com/questions/7541550/remove-the-extension-of-a-file
    public static String stripExtension(final String s)
    {
        return s != null && s.lastIndexOf(".") > 0 ? s.substring(0, s.lastIndexOf(".")) : s;
    }

    public static String getExtension(String url) {
        String[] urlSplit = url.split("/");
        String filename = urlSplit[urlSplit.length - 1];
        String[] nameSplit = filename.split("[.]");
        StringBuffer fileExtension = new StringBuffer();
        // to prevent appending . after extension type
        if (nameSplit.length > 1) {
            for (int index = 1; index < nameSplit.length; index++) {
                if (index != nameSplit.length - 1)
                    fileExtension.append(nameSplit[index] + ".");
                else
                    fileExtension.append(nameSplit[index]);
            }
        } else {
            fileExtension.append(nameSplit[0]);
        }
        return fileExtension.toString();
    }

    public static String getPathType(String path) {
        String[] str = path.split(":");
        return (str.length>0)?str[0]:null;
    }

    public static boolean isSchOrBrd(String name) {
        return (name.toLowerCase().endsWith(".brd") || name.toLowerCase().endsWith(".sch"));
    }

    public static int filenameToLightDrawable(String name) {
        name = name.toLowerCase();
        if (name.endsWith(".brd"))
            return R.drawable.changebrdlight;
        else
        if (name.endsWith(".sch"))
            return R.drawable.changeschlight;
        return 0;
    }

}
