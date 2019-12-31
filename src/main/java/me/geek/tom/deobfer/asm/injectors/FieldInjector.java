package me.geek.tom.deobfer.asm.injectors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class FieldInjector extends ClassVisitor {

    private String fieldName;
    private int access;
    private boolean isFieldPresent;

    public FieldInjector(String fieldName, int fieldAccess, ClassVisitor cv) {
        super(Opcodes.ASM4, cv);
        this.cv = cv;
        this.fieldName = fieldName;
        this.access = fieldAccess;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.println("FieldAdder::visitField -> " + name);

        if (name.equals(fieldName)) {
            isFieldPresent = true;
        }

        return cv.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitEnd() {
        System.out.println("FieldAdder::visitEnd");

        if (!isFieldPresent) {
            System.out.println("FieldAdder adding field at end of class.");
            FieldVisitor fv = cv.visitField(
                    access, fieldName, "C", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        cv.visitEnd();
    }
}