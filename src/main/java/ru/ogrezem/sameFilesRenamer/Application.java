package ru.ogrezem.sameFilesRenamer;

import java.io.IOException;

public class Application {

    public static void main(String... args) throws IOException {
        var sameFilesRenamer = new SameFilesRenamer();
        String firstDirName = args[0] == null ? "pack1" : args[0]; // pack1
        String secondDirName = args[1] == null ? "pack2" : args[1]; // pack2
        String thirdDirName = args[2] == null ? "pack3" : args[2]; // pack3
        sameFilesRenamer.mergeDirsFilesWithoutNamesDuplication(
                firstDirName,
                secondDirName,
                thirdDirName
        );
//        System.out.println("Hi! How Are you?");
//        var scanner = new Scanner(System.in);
//        String answer = scanner.nextLine();
//        switch (answer) {
//            case "I'm fine":
//                System.out.println("That's good! Bye");
//                break;
//            default:
//                System.out.println("Idi naxyi");
//                break;
//        }
//        scanner.nextLine();
    }
}
