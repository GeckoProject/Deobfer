package me.geek.tom.deobfer.asm.injectors;

import org.objectweb.asm.ClassVisitor;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.ASM4;

public class InterfaceInjector extends ClassVisitor {

    private String interfaceName;

    public InterfaceInjector(ClassVisitor cv, String interfaceName) {
        super(ASM4, cv);

        this.interfaceName = interfaceName;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("There is " + interfaces.length + " interfaces before: " + Arrays.toString(interfaces));
        String[] newInterfaces = new String[interfaces.length + 1];

        System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
        newInterfaces[newInterfaces.length - 1] = interfaceName;

        System.out.println("There is now " + newInterfaces.length + " interfaces: " + Arrays.toString(newInterfaces));

        super.visit(version, access, name, signature, superName, newInterfaces);
    }
}
