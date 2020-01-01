package me.geek.tom.deobfer.asm;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class MethodProcessor extends MethodVisitor {
    public MethodProcessor(MethodVisitor mv) {
        super(ASM4, mv);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        System.out.println(owner + "::" + name);

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
