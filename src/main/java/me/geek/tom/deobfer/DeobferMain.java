package me.geek.tom.deobfer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.geek.tom.deobfer.asm.ClassProccessor;
import me.geek.tom.deobfer.asm.ClassRenamer;
import me.geek.tom.deobfer.filesystem.RemappingTreeWalker;
import me.geek.tom.deobfer.mappings.Mappings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void run(String[] args) throws Exception {
        LOGGER.info("Deobfer says: 'Hello, World!'");

        OptionParser parser = new OptionParser("tiny");
        parser.allowsUnrecognizedOptions();
        OptionSpec<File> mappings = parser.accepts("mappings").withRequiredArg().ofType(File.class);
        OptionSpec<File> input = parser.accepts("input").withRequiredArg().ofType(File.class);
        OptionSpec<File> output = parser.accepts("output").withRequiredArg().ofType(File.class);
        parser.acceptsAll(Arrays.asList("mappings-tiny-format", "tiny", "t"));

        OptionSet options = parser.parse(args);
        if (!options.has(mappings) || !options.has(input) || !options.has(output)) {
            LOGGER.severe("Missing required attributes! Usage:");
            parser.printHelpOn(System.out);
            System.exit(1);
        } else {
            File mappingsFile = options.valueOf(mappings);
            if (!mappingsFile.exists()) {
                LOGGER.severe("Mappings file " + mappingsFile.getName() + " does not exist!");
                System.exit(1);
            }

            File inputFile = options.valueOf(input);
            if (!mappingsFile.exists()) {
                LOGGER.severe("Input file " + inputFile.getName() + " does not exist!");
                System.exit(1);
            }

            File outputFile = options.valueOf(output);

            if (!outputFile.exists())
                outputFile.mkdirs();

            if (!options.has("mappings-tiny-format"))
                loadSrgMappings(mappingsFile);
            else
                loadTinyMappings(mappingsFile);
            walkTreeAndRemap(inputFile, outputFile);
        }
    }

    private void loadTinyMappings(File mappingsFile) throws IOException {
        mappings = Mappings.loadTinyMappings(mappingsFile);

        int classCount = mappings.getClasses().size();
        int fieldCount = mappings.getFields().size();
        int methodCount = mappings.getMethods().size();

        LOGGER.info("Mappings found " + classCount + " classes, " + fieldCount + " fields and " + methodCount + " methods to rename");
    }

    public void walkTreeAndRemap(File startDir, File outputDir) throws IOException {
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

    private void loadSrgMappings(File mappingsFile) throws IOException {
        mappings = Mappings.loadSrgFromFile(mappingsFile);

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
