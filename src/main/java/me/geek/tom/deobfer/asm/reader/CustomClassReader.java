package me.geek.tom.deobfer.asm.reader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.ASM4;


public class CustomClassReader extends ClassVisitor {
    public CustomClassReader() {
        super(ASM4);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.println(access + " " + desc + " " + name + ((value == null) ? "" : value.toString()));

        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        if (!(name.equals("<init>") || name.equals("<clinit>")))
            System.out.println(name + " " + desc + (exceptions == null ? "" : Arrays.toString(exceptions)));

        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
