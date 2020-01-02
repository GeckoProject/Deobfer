package me.geek.tom.deobfer;

import me.geek.tom.deobfer.asm.ClassProccessor;
import me.geek.tom.deobfer.asm.ClassRenamer;
import me.geek.tom.deobfer.asm.CustomClassWriter;
import me.geek.tom.deobfer.asm.reader.CustomClassReader;
import me.geek.tom.deobfer.mappings.Mappings;
import org.objectweb.asm.ClassReader;

import java.io.*;

public class DeobferMain {

    private Mappings mappings;

    public static void main(String[] args) throws Exception {
        new DeobferMain().run(args);
    }

    private void run(String[] args) throws Exception {
        System.out.println("Deobfer says: 'Hello, World!'");

        String class_name = "ctp";

        loadMappings(new File("./testdata/client.srg"));
        doMethodRenaming(new File("./testdata/" + class_name + ".class"), new File("./testdata/out/"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void doMethodRenaming(File input, File outputDir) throws IOException {
        if (!outputDir.isDirectory() || !outputDir.exists())
            outputDir.mkdir();

        ClassProccessor proccessor = new ClassProccessor(new FileInputStream(input));

        ClassRenamer renamer = new ClassRenamer(proccessor.getWriter(), mappings);

        byte[] out = proccessor.apply(renamer);

        File output = new File(outputDir, renamer.getName() + ".class");

        if (!output.getParentFile().exists())
            output.getParentFile().mkdirs();

        new FileOutputStream(output).write(out);
    }

    private void loadMappings(File mappingsFile) throws IOException {
        mappings = Mappings.loadFromFile(mappingsFile);

        int classCount = mappings.getClasses().size();
        int fieldCount = mappings.getFields().size();
        int methodCount = mappings.getMethods().size();

        System.out.println("Mappings found " + classCount + " classes, " + fieldCount + " fields and " + methodCount + " methods to rename");
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
