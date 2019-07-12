package ru.ogrezem.sameFilesRenamer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SameFilesRenamer {

    private static final String BASE_DIRECTORY_PATH = "./files/";

    public void sortSameFiles(String firstDirName, String secondDirName, String thirdDirName)
            throws IOException {
        var firstDir = new File(BASE_DIRECTORY_PATH + firstDirName);
        var secondDir = new File(BASE_DIRECTORY_PATH + secondDirName);
        var thirdDir = new File(BASE_DIRECTORY_PATH + thirdDirName);
        boolean anyDirNotExist = !firstDir.exists() || !secondDir.exists() || !thirdDir.exists();
        boolean anyDirIsntDir = !firstDir.isDirectory() || !secondDir.isDirectory() || !thirdDir.isDirectory();
        if (anyDirNotExist || anyDirIsntDir)
            throw new FileNotFoundException("Хотя бы одна из заданных папок не существует");
        File[] firstDirFiles = firstDir.listFiles();
        File[] secondDirFiles = secondDir.listFiles();
        File[] thirdDirFiles = thirdDir.listFiles();
        var filesWithSameNames = new HashMap<String, List<File>>();
        for (File firstDirFile : firstDirFiles) {
            for (File secondDirFile : secondDirFiles) {
                String firstDirFileName = firstDirFile.getName();
                if (firstDirFileName.equals(secondDirFile.getName())) {
                    filesWithSameNames.putIfAbsent(firstDirFileName, new ArrayList<>());
                    filesWithSameNames.get(firstDirFileName).addAll(List.of(firstDirFile, secondDirFile));
//                    filesWithSameNames.put(firstDirFileName, firstDirFile);
//                    filesWithSameNames.put(firstDirFileName, secondDirFile);
                }

            }
        }
    }

    private static byte[] readFileBytes(File firstDirFile) throws IOException {
        try (var firstDirFileIS = new BufferedInputStream(new FileInputStream(firstDirFile))) {
            return firstDirFileIS.readAllBytes();
        }
    }

    private static boolean byteArraysAreSame(byte[] firstBytes, byte[] secondBytes) {
        if (firstBytes.length != secondBytes.length)
            return false;
        for (int i = 0; i < firstBytes.length; i++) {
            if (firstBytes[i] != secondBytes[i])
                return false;
        }
        return true;
    }
}
