package me.geek.tom.deobfer.asm.reader;

import me.geek.tom.deobfer.DeobferMain;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.ASM5;


public class CustomClassReader extends ClassVisitor {
    public CustomClassReader() {
        super(ASM5);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        DeobferMain.LOGGER.info(access + " " + desc + " " + name + ((value == null) ? "" : value.toString()));

        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        if (!(name.equals("<init>") || name.equals("<clinit>")))
            DeobferMain.LOGGER.info(name + " " + desc + (exceptions == null ? "" : Arrays.toString(exceptions)));

        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
