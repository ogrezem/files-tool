package ru.ogrezem.sameFilesRenamer;

import com.google.common.hash.Hashing;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SameFilesRenamer {

    private static final String BASE_DIRECTORY_PATH = "./files/";

    // TODO: придумать методу нормальное название
    public void sortSameFiles(String firstDirName, String secondDirName, String thirdDirName)
            throws IOException {
        var firstDir = new File(BASE_DIRECTORY_PATH + firstDirName);
        var secondDir = new File(BASE_DIRECTORY_PATH + secondDirName);
        var targetDir = new File(BASE_DIRECTORY_PATH + thirdDirName);
        boolean anyDirNotExist = !firstDir.exists() || !secondDir.exists() || !targetDir.exists();
        boolean anyDirIsntDir = !firstDir.isDirectory() || !secondDir.isDirectory() || !targetDir.isDirectory();
        if (anyDirNotExist || anyDirIsntDir)
            throw new FileNotFoundException("Хотя бы одна из заданных папок не существует");
        File[] firstDirFilesList = firstDir.listFiles();
        File[] secondDirFilesList = secondDir.listFiles();
        if (firstDirFilesList == null || secondDirFilesList == null)
            throw new RuntimeException("Хотя бы одна из выбранных папок пуста");
        List<File> firstDirFiles = Arrays.asList(firstDirFilesList);
        List<File> secondDirFiles = Arrays.asList(secondDirFilesList);
        var namesAndFilesWithTheseNames = new HashMap<String, List<File>>();
        firstDirFilesLoop:
        for (File firstDirFile : firstDirFiles) {
            for (File secondDirFile : secondDirFiles) {
                String firstDirFileName = firstDirFile.getName();
                if (firstDirFileName.equals(secondDirFile.getName())) {
                    namesAndFilesWithTheseNames.putIfAbsent(firstDirFileName, new ArrayList<>(2));
                    List<File> filesWithSameName = namesAndFilesWithTheseNames.get(firstDirFileName);
                    filesWithSameName.addAll(List.of(firstDirFile, secondDirFile));
                    firstDirFiles.remove(firstDirFile);
                    secondDirFiles.remove(secondDirFile);
                    continue firstDirFilesLoop;
                }
            }
        }
//        var filesForThirdDir = new ArrayList<File>();
        var namesToFilesForMoving = new HashMap<String, File>();
        for (File firstDirFile : firstDirFiles)
            namesToFilesForMoving.put(firstDirFile.getName(), firstDirFile);
        for (File secondDirFile : secondDirFiles)
            namesToFilesForMoving.put(secondDirFile.getName(), secondDirFile);
        for (HashMap.Entry<String, List<File>> nameAndFilesWithThisName : namesAndFilesWithTheseNames.entrySet()) {
            List<File> files = nameAndFilesWithThisName.getValue();
            List<File> uniqueFiles = distinctFiles(files);
            for (int i = 0; i < uniqueFiles.size(); i++) {
                File uniqueFile = uniqueFiles.get(i);
                if (i == 0) {
                    namesToFilesForMoving.put(uniqueFile.getName(), uniqueFile);
                    continue;
                }
                namesToFilesForMoving.put(uniqueFile.getName() + "(" + i + ")", uniqueFile);
            }
        }
        for (HashMap.Entry<String, File> namesToFilesForMovingEntry : namesToFilesForMoving.entrySet()) {
            File fileWillBeMoved = namesToFilesForMovingEntry.getValue();
            String fileWillBeMovedNewName = namesToFilesForMovingEntry.getKey();
            moveFileToDirectory(fileWillBeMoved, targetDir, fileWillBeMovedNewName);
        }
    }

    private static void moveFileToDirectory(File file, File directory, String fileNewName) throws IOException {
        Path initialFilePath = Paths.get(file.toURI());
        Path targetFilePath = Paths.get(directory.toPath() + "/" + fileNewName);
        Files.move(initialFilePath, targetFilePath);
    }

    private static List<File> distinctFiles(List<File> files) {
        return files.stream()
                .collect(Collectors.groupingBy(SameFilesRenamer::getFileHash))
                .entrySet().stream().map(entry -> entry.getValue().get(0))
                .collect(Collectors.toList());
    }

    private static String getFileHash(File file) {
        return Hashing.sha256().hashBytes(getFileBytes(file)).toString();
    }

    private static byte[] getFileBytes(File file) {
        try (var fileInputStream = new BufferedInputStream(new FileInputStream(file))) {
            return fileInputStream.readAllBytes();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}