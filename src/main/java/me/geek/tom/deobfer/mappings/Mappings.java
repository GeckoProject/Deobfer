package me.geek.tom.deobfer.mappings;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Mappings {

    private List<ClassMapping> classes = new ArrayList<>();
    private List<FieldMapping> fields = new ArrayList<>();
    private List<MethodMapping> methods = new ArrayList<>();

    public List<ClassMapping> getClasses() {
        return classes;
    }

    public List<FieldMapping> getFields() {
        return fields;
    }

    public List<MethodMapping> getMethods() {
        return methods;
    }

    private Mappings() {}

    public static Mappings loadFromFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        List<String> lines = IOUtils.readLines(inputStream, Charset.defaultCharset());

        String currentClass = "";

        Mappings ret = new Mappings();

        for (String line : lines) {
            if (!line.startsWith("\t")) { // Class line
                String[] names = line.trim().split(" ");
                currentClass = names[0];

                ret.addClass(new ClassMapping(names[1], names[0]));
            } else {
                if (currentClass == null)
                    throw new IOException("Illegal SRG file!");

                String ln = line.trim();

                if (ln.split(" ").length == 2) { // Fields
                    String[] names = ln.split(" ");

                    ret.addField(new FieldMapping(names[1], names[0], currentClass));
                } else if (ln.split(" ").length == 3) { // Methods
                    String[] parts = ln.split(" ");

                    String obfName = parts[0];
                    String desc = parts[1];
                    String orgName = parts[2];

                    ret.addMethod(new MethodMapping(orgName, obfName, desc, currentClass));
                }
            }
        }

        return ret;
    }

    private void addClass(ClassMapping mapping) {
        this.classes.add(mapping);
    }

    private void addField(FieldMapping mapping) {
        this.fields.add(mapping);
    }

    private void addMethod(MethodMapping mapping) {
        this.methods.add(mapping);
    }
}
