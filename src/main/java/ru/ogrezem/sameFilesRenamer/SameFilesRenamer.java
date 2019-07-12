package ru.ogrezem.sameFilesRenamer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SameFilesRenamer {

    private static final String BASE_DIRECTORY_PATH = "./files/";

    // TODO: придумать методу нормальное название
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
        var namesAndFilesWithTheseNames = new HashMap<String, List<File>>();
        for (File firstDirFile : firstDirFiles) {
            for (File secondDirFile : secondDirFiles) {
                String firstDirFileName = firstDirFile.getName();
                if (firstDirFileName.equals(secondDirFile.getName())) {
                    namesAndFilesWithTheseNames.putIfAbsent(firstDirFileName, new ArrayList<>());
                    List<File> filesWithSameName = namesAndFilesWithTheseNames.get(firstDirFileName);
                    if (filesWithSameName.isEmpty())
                        filesWithSameName.addAll(List.of(firstDirFile, secondDirFile));
                    else
                        filesWithSameName.add(secondDirFile);
                    // TODO: отшлёпать Дианку за плохое поведение
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
