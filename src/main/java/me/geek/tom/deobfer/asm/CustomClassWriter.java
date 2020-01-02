package me.geek.tom.deobfer.asm;

import me.geek.tom.deobfer.DeobferMain;
import me.geek.tom.deobfer.asm.injectors.FieldInjector;
import me.geek.tom.deobfer.asm.injectors.InterfaceInjector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

public class CustomClassWriter {

    private ClassReader reader;
    private ClassWriter writer;

    public CustomClassWriter(String className) throws IOException {
        this.reader = new ClassReader(className);
        this.writer = new ClassWriter(reader, 0);
    }

    public byte[] addInterface() {
        DeobferMain.LOGGER.info("Adding interface!");
        InterfaceInjector interfaceInjector = new InterfaceInjector(writer, "me/geek/tom/deobfer/testing/ITestInterface");

        reader.accept(interfaceInjector, 0);

        DeobferMain.LOGGER.info("Interface addition complete!");
        return writer.toByteArray();
    }

    public byte[] addField() {
        DeobferMain.LOGGER.info("Adding field!");
        FieldInjector fieldInjector = new FieldInjector("testField", Opcodes.ACC_PUBLIC, writer);

        reader.accept(fieldInjector, 0);

        DeobferMain.LOGGER.info("Field addition complete!");

        return writer.toByteArray();
    }
}
