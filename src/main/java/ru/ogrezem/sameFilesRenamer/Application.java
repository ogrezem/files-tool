package ru.ogrezem.sameFilesRenamer;

import java.io.IOException;

public class Application {

    public static void main(String... args) throws IOException {
        var sameFilesRenamer = new SameFilesRenamer();
        sameFilesRenamer.mergeDirsFilesWithoutNamesDuplication(
                "pack1",
                "pack2",
                "pack3"
        );
    }
}
