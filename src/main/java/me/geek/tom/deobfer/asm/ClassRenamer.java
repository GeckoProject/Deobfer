package me.geek.tom.deobfer.asm;

import me.geek.tom.deobfer.DeobferMain;
import me.geek.tom.deobfer.Utils;
import me.geek.tom.deobfer.mappings.ClassMapping;
import me.geek.tom.deobfer.mappings.FieldMapping;
import me.geek.tom.deobfer.mappings.Mappings;
import me.geek.tom.deobfer.mappings.MethodMapping;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

public class ClassRenamer extends ClassVisitor {
    private Mappings mappings;
    private List<FieldMapping> fields;
    private List<MethodMapping> methods;

    private String name;

    public Mappings getMappings() {
        return mappings;
    }

    public ClassRenamer(ClassVisitor cv, Mappings mappings) {
        super(ASM5, cv);
        this.mappings = mappings;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        DeobferMain.LOGGER.fine("Visiting class: " + name);

        String newName = name;
        String newSuperName = superName;
        String[] newInterfaces = Arrays.copyOf(interfaces, interfaces.length);

        ClassMapping classMapping = this.mappings.findClass(name);
        if (classMapping == null)
            try {
                classMapping = this.mappings.findClass(name.substring(0, name.indexOf("$")));
            } catch (IndexOutOfBoundsException ignored) {}

        if (classMapping == null) {
            DeobferMain.LOGGER.info("No mappings for class: " + name);
            fields = Collections.emptyList();
            methods = Collections.emptyList();
        }
        else {
            this.fields = this.mappings.getFields().get(classMapping);
            this.methods = this.mappings.getMethods().get(classMapping);

            this.name = Utils.remapClassName(name, mappings);

            DeobferMain.LOGGER.fine("Loaded " + this.fields.size() + " field mappings and " + this.methods.size() + " method mappings.");

            // Remap name
            newName = Utils.remapClassName(name, mappings);

            // Remap superName
            newSuperName = Utils.remapClassName(superName, mappings);

            // Remap interfaces
            for (int i = 0; i < interfaces.length; i++) {
                newInterfaces[i] = Utils.remapClassName(interfaces[i], mappings);
            }
        }

        super.visit(version, access, newName, signature, newSuperName, newInterfaces);
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
            DeobferMain.LOGGER.fine("[ Fields ] Renaming '" + name + "' to '" + newName + "'");
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
                DeobferMain.LOGGER.fine("[ Method ] Renaming '" + name + "' to '" + newName + "'");
                break;
            }
        }

        MethodVisitor mv = super.visitMethod(access, newName, newDesc, signature, exceptions);

        return mv == null ? null : new MethodProcessor(mv, mappings);
    }

    public String getName() {
        return name;
    }
}
