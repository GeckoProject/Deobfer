package me.geek.tom.deobfer;

import me.geek.tom.deobfer.mappings.ClassMapping;
import me.geek.tom.deobfer.mappings.Mappings;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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

    public static String remapClassName(String currentName, Mappings mappings) {
        ClassMapping mapping = mappings.findClass(currentName);
        return mapping == null ? currentName : mapping.getOrgName();
    }

    private static boolean isJavaLambdaMetafactory(Handle bsm) {
        return bsm.getTag() == Opcodes.H_INVOKESTATIC
                && bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")
                && (bsm.getName().equals("metafactory")
                && bsm.getDesc().equals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;")
                || bsm.getName().equals("altMetafactory")
                && bsm.getDesc().equals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;"))
                && !bsm.isInterface();
    }

    public static Handle getLambdaImplementedMethod(String name, String desc, Handle bsm, Object... bsmArgs) {
        if (isJavaLambdaMetafactory(bsm)) {
            assert desc.endsWith(";");
            return new Handle(Opcodes.H_INVOKEINTERFACE, desc.substring(desc.lastIndexOf(')') + 2, desc.length() - 1), name, ((Type) bsmArgs[0]).getDescriptor(), true);
        } else {
            System.out.printf("unknown invokedynamic bsm: %s/%s%s (tag=%d iif=%b)%n", bsm.getOwner(), bsm.getName(), bsm.getDesc(), bsm.getTag(), bsm.isInterface());

            return null;
        }
    }
}
