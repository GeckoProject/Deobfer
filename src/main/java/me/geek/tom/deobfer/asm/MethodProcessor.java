package me.geek.tom.deobfer.asm;

import me.geek.tom.deobfer.Utils;
import me.geek.tom.deobfer.mappings.ClassMapping;
import me.geek.tom.deobfer.mappings.FieldMapping;
import me.geek.tom.deobfer.mappings.Mappings;
import me.geek.tom.deobfer.mappings.MethodMapping;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ASM4;

@SuppressWarnings("WeakerAccess")
public class MethodProcessor extends MethodVisitor {
    private Mappings mappings;

    public MethodProcessor(MethodVisitor mv, Mappings mappings) {
        super(ASM4, mv);
        this.mappings = mappings;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {

        ClassMapping cm = mappings.findClass(owner);

        String newName = name;
        String newOwner = owner;

        if (cm != null) {
            for (FieldMapping fm : mappings.getFields().get(cm)) {
                if (fm.getObfName().equals(name)) {
                    newName = fm.getOrgName();
                    newOwner = cm.getOrgName();
                    System.out.println("[ Fields ] Rename reference of '" + owner + "." + name + "' to '" + newOwner + "." + newName + "'");
                    break;
                }
            }
        }

        super.visitFieldInsn(opcode, newOwner, newName, Utils.remapDesc(desc, mappings));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        ClassMapping cm = mappings.findClass(owner);

        String newName = name;
        String newOwner = owner;

        if (cm != null) {
            newOwner = cm.getOrgName();

            for (MethodMapping mm : mappings.getMethods().get(cm)) {
                if (mm.getObfName().equals(name)) {
                    newName = mm.getOrgName();
                    System.out.println("[ Method ] Rename call of '" + owner + "::" + name + "' to '" + newOwner + "::" + newName + "'");
                    break;
                }
            }
        }

        super.visitMethodInsn(opcode, newOwner, newName, desc, itf);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        ClassMapping cm = mappings.findClass(type);

        String newType = type;

        if (cm != null) {
            System.out.println("[  Type  ] Rename reference to type: '" + type + "' to '" + newType + "'");
            newType = cm.getOrgName();
        }

        super.visitTypeInsn(opcode, newType);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {

        Object[] newBsmArgs = new Object[bsmArgs.length];
        int i = 0;

        for (Object o : bsmArgs) {
            if (o instanceof Type) {
                String descriptor = ((Type) o).getDescriptor();
                newBsmArgs[i] = Type.getMethodType(Utils.remapDesc(descriptor, mappings));
            } else if (o instanceof Handle) {
                Handle handle = (Handle) o;

                ClassMapping cm = mappings.findClass(handle.getOwner());
                String newOwner = handle.getOwner();
                String newName = handle.getName();

                if (cm != null) {

                    newOwner = cm.getOrgName();

                    for (MethodMapping mm : mappings.getMethods().get(cm))
                        if (mm.shouldRename(newName, handle.getDesc()))
                            newName = mm.getOrgName();
                }

                System.out.println("[ Lambda ] Remapped handle to '" + newOwner + "::" + newName + "'");

                newBsmArgs[i] = new Handle(handle.getTag(), newOwner, newName, Utils.remapDesc(handle.getDesc(), mappings), handle.isInterface());
            } else {
                newBsmArgs[i] = o;
            }
            i++;
        }

        Handle impl = Utils.getLambdaImplementedMethod(name, desc, bsm, newBsmArgs);

        String newName = name;
        String newDesc = Utils.remapDesc(desc, mappings);


        if (impl != null && mappings.getMethods().get(mappings.findClass(impl.getOwner())) != null) {
            for (MethodMapping mm : mappings.getMethods().get(mappings.findClass(impl.getOwner()))) {
                if (mm.shouldRename(impl.getName(), impl.getDesc())) {
                    newName = mm.getOrgName();
                    break;
                }
            }
        }

        super.visitInvokeDynamicInsn(newName, newDesc, bsm, newBsmArgs);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }
}
