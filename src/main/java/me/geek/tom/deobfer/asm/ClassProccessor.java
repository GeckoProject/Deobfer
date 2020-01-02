package me.geek.tom.deobfer.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClassProccessor {
    private ClassReader reader;
    private ClassWriter writer;

    public ClassWriter getWriter() {
        return writer;
    }

    public ClassProccessor(InputStream input) throws IOException {
        this.reader = new ClassReader(input);
        this.writer = new ClassWriter(reader, 0);
    }

    public void apply(OutputStream output, ClassVisitor cv) throws IOException {
        this.reader.accept(cv, 0);

        output.write(writer.toByteArray());
    }

    public byte[] apply(ClassVisitor cv) {
        this.reader.accept(cv, 0);

        return writer.toByteArray();
    }
}
