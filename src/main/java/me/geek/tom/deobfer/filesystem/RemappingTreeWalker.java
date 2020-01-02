package me.geek.tom.deobfer.filesystem;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class RemappingTreeWalker extends SimpleFileVisitor<Path> {

    private List<Path> fileQueue = new ArrayList<>();

    public List<Path> getFileQueue() {
        return fileQueue;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        if (attrs.isRegularFile())
            if (file.toString().endsWith(".class"))
                fileQueue.add(file);

        return super.visitFile(file, attrs);
    }
}
