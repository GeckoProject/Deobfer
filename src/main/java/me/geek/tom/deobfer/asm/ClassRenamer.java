package me.geek.tom.deobfer.asm;

import org.objectweb.asm.ClassVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class ClassRenamer extends ClassVisitor {
    public ClassRenamer() {
        super(ASM4);
    }
}
