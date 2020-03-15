package me.geek.tom.deobfer;

import me.geek.tom.deobfer.asm.ClassProccessor;
import me.geek.tom.deobfer.asm.ClassRenamer;
import me.geek.tom.deobfer.filesystem.RemappingTreeWalker;
import me.geek.tom.deobfer.mappings.Mappings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DeobferMain {

    private Mappings mappings;

    public static Logger LOGGER = Logger.getLogger("Deobfer");

    static {
        // Configure logging
        InputStream stream = DeobferMain.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new DeobferMain().run(args);
    }

    private void run(String[] args) throws Exception {
        LOGGER.info("Deobfer says: 'Hello, World!'");

        loadMappings(new File("./testdata/client.srg"));

        walkTreeAndRemap(new File("./testdata/client/"), new File("./testdata/out/"));
    }

    private void walkTreeAndRemap(File startDir, File outputDir) throws IOException {
        RemappingTreeWalker walker = new RemappingTreeWalker();

        Files.walkFileTree(startDir.toPath(), walker);


        List<Path> queue = walker.getFileQueue();
        int size = queue.size();
        LOGGER.info("Found " + size + " files to remap!");
        for (int i = 0; i < size; i++) {
            Path p = queue.get(i);
            LOGGER.info("Remapping file: " + p.toString() + " [ " + i + "/" + size + " | " + (int)((float)i/size * 100) + "% ]");
            doClassRenaming(p.toFile(), outputDir);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void doClassRenaming(File input, File outputDir) throws IOException {
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

        LOGGER.info("Mappings found " + classCount + " classes, " + fieldCount + " fields and " + methodCount + " methods to rename");
    }

    /* private void readClass(File classFile) throws Exception {

        ClassReader reader = new ClassReader(new FileInputStream(classFile));
        reader.accept(new CustomClassReader(), 0);
    } */
}
