package me.geek.tom.deobfer.asm;

import me.geek.tom.deobfer.Utils;
import me.geek.tom.deobfer.mappings.FieldMapping;
import me.geek.tom.deobfer.mappings.Mappings;
import me.geek.tom.deobfer.mappings.MethodMapping;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM4;

public class ClassRenamer extends ClassVisitor {
    private Mappings mappings;
    private List<FieldMapping> fields;
    private List<MethodMapping> methods;

    public ClassRenamer(ClassVisitor cv, Mappings mappings) {
        super(ASM4, cv);
        this.mappings = mappings;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("Visiting class: " + name);

        if (this.mappings.findClass(name) == null)
            System.out.println("No mappings for class: " + name);
        else {
            this.fields = this.mappings.getFields().get(this.mappings.findClass(name));
            this.methods = this.mappings.getMethods().get(this.mappings.findClass(name));

            System.out.println("Loaded " + this.fields.size() + " field mappings and " + this.methods.size() + " method mappings.");
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {

        FieldMapping mapping = null;
        for (FieldMapping mp : fields) {
            if (mp.getObfName().equals(name)) {
                mapping = mp;
                break;
            }
        }

        String newName = name;

        String newDesc = Utils.remapDesc(desc, mappings);

        if (mapping != null) {
            newName = mapping.getOrgName();
            System.out.println("[ Fields ] Renaming " + name + " to " + newName);
        }

        return super.visitField(access, newName, newDesc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        String newName = name;

        String newDesc = Utils.remapDesc(desc, mappings);

        for (MethodMapping mp : methods) {
            if (mp.shouldRename(name, newDesc)) {
                newName = mp.getOrgName();
                System.out.println("[ Method ] Renaming " + name + " to " + newName);
                break;
            }
        }

        return super.visitMethod(access, newName, newDesc, signature, exceptions);
    }
}
