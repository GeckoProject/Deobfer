package me.geek.tom.deobfer;

import me.geek.tom.deobfer.asm.CustomClassWriter;
import me.geek.tom.deobfer.asm.reader.CustomClassReader;
import me.geek.tom.deobfer.mappings.Mappings;
import org.objectweb.asm.ClassReader;

import java.io.*;

public class DeobferMain {
    public static void main(String[] args) throws Exception {
        new DeobferMain().run(args);
    }

    private void run(String[] args) throws Exception {
        System.out.println("Deobfer says: 'Hello, World!'");

        loadMappings(new File("./testdata/client.srg"));
    }

    private void loadMappings(File mappingsFile) throws IOException {
        Mappings mappings = Mappings.loadFromFile(mappingsFile);

        int classCount = mappings.getClasses().size();
        int fieldCount = mappings.getFields().size();
        int methodCount = mappings.getMethods().size();

        System.out.println("Mappings found " + classCount + " classes, " + fieldCount + " fields and " + methodCount + " methods!");
    }

    private void readClass(File classFile) throws Exception {

        ClassReader reader = new ClassReader(new FileInputStream(classFile));
        reader.accept(new CustomClassReader(), 0);
    }

    private void injector() throws Exception {
        CustomClassWriter testWriter = new CustomClassWriter("java.lang.Integer");

        File output = new File("./Integer.class");

        FileOutputStream outputStream = new FileOutputStream(output);

        outputStream.write(testWriter.addInterface());
    }
}
