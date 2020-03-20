package me.geek.tom.deobfer.mappings;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mappings {

    private List<ClassMapping> classes = new ArrayList<>();
    private Map<ClassMapping,List<FieldMapping>> fields = new HashMap<>();
    private Map<ClassMapping,List<MethodMapping>> methods = new HashMap<>();

    public List<ClassMapping> getClasses() {
        return classes;
    }

    public Map<ClassMapping,List<FieldMapping>> getFields() {
        return fields;
    }

    public Map<ClassMapping,List<MethodMapping>> getMethods() {
        return methods;
    }

    private Mappings() {}

    public static Mappings loadSrgFromFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        List<String> lines = IOUtils.readLines(inputStream, Charset.defaultCharset());

        String currentClass = null;
        ClassMapping currentClassMapping = null;

        Mappings ret = new Mappings();

        for (String line : lines) {
            if (!line.startsWith("\t")) { // Class line
                String[] names = line.trim().split(" ");
                currentClass = names[0];
                currentClassMapping = new ClassMapping(names[1], names[0]);

                ret.addClass(currentClassMapping);
            } else {
                if (currentClass == null)
                    throw new IOException("Illegal SRG file!");

                String ln = line.trim();

                if (ln.split(" ").length == 2) { // Fields
                    String[] names = ln.split(" ");

                    ret.addField(new FieldMapping(names[1], names[0], currentClass), currentClassMapping);
                } else if (ln.split(" ").length == 3) { // Methods
                    String[] parts = ln.split(" ");

                    String obfName = parts[0];
                    String desc = parts[1];
                    String orgName = parts[2];

                    ret.addMethod(new MethodMapping(orgName, obfName, desc, currentClass), currentClassMapping);
                }
            }
        }

        return ret;
    }

    public static Mappings loadTinyMappings(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        List<String> lines = IOUtils.readLines(inputStream, Charset.defaultCharset());

        Mappings ret = new Mappings();

        for (String line : lines) {
            if (line.startsWith("#"))
                continue;
            if (line.startsWith("CLASS")) {
                String[] parts = line.substring(6).split("\t");

                String org = parts[1];
                String obf = parts[0];
                ret.addClass(new ClassMapping(org, obf));
            } else if (line.startsWith("FIELD")) {
                String[] parts = line.substring(6).split("\t");
                String cls = parts[0];
                String obf = parts[2];
                String org = parts[3];
                ret.addField(new FieldMapping(org, obf, cls), ret.findClass(cls));
            } else if (line.startsWith("METHOD")) {
                String[] parts = line.substring(7).split("\t");
                String cls = parts[0];
                String desc = parts[1];
                String obf = parts[2];
                String org = parts[3];
                ret.addMethod(new MethodMapping(org, obf, desc, cls), ret.findClass(cls));
            }
        }

        return ret;
    }

    private void addClass(ClassMapping mapping) {
        this.classes.add(mapping);
        this.fields.put(mapping, new ArrayList<>());
        this.methods.put(mapping, new ArrayList<>());
    }

    private void addField(FieldMapping mapping, ClassMapping classMapping) {
        this.fields.get(classMapping).add(mapping);
    }

    private void addMethod(MethodMapping mapping, ClassMapping classMapping) {
        this.methods.get(classMapping).add(mapping);
    }

    public ClassMapping findClass(String obfName) {
        for (ClassMapping cls : this.classes) {
            if (cls.getObfName().equals(obfName))
                return cls;
        }
        return null;
    }
}
