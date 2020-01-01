package me.geek.tom.deobfer;

import me.geek.tom.deobfer.mappings.ClassMapping;
import me.geek.tom.deobfer.mappings.Mappings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static Pattern CLASS = Pattern.compile("L(?<cls>[^;]+);");

    public static String remapDesc(String desc, Mappings mappings) {
        Matcher matcher = CLASS.matcher(desc);

        StringBuffer buf = new StringBuffer();

        while (matcher.find()) {
            String cls = matcher.group("cls");
            ClassMapping newCls = mappings.findClass(cls);

            matcher.appendReplacement(buf, Matcher.quoteReplacement("L" + (newCls != null ? newCls.getOrgName() : cls) + ";"));
        }
        matcher.appendTail(buf);
        return buf.toString();
    }
}
