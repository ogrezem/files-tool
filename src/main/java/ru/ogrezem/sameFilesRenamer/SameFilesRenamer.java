package ru.ogrezem.sameFilesRenamer;

import com.google.common.hash.Hashing;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SameFilesRenamer {

    private static final String BASE_DIRECTORY_PATH = "./files/";

    // TODO: придумать методу нормальное название
    public void sortSameFiles(String firstDirName, String secondDirName, String thirdDirName)
            throws IOException {
        var firstDir = new File(BASE_DIRECTORY_PATH + firstDirName);
        var secondDir = new File(BASE_DIRECTORY_PATH + secondDirName);
        var targetDir = new File(BASE_DIRECTORY_PATH + thirdDirName);
        checkDirsExistAndAreDir(firstDir, secondDir, targetDir);
        File[] firstDirFilesList = firstDir.listFiles();
        File[] secondDirFilesList = secondDir.listFiles();
        checkFirstAndSecondDirsNotEmpty(firstDirFilesList, secondDirFilesList);
        ArrayList<File> firstDirFiles = new ArrayList<>(Arrays.asList(firstDirFilesList));
        ArrayList<File> secondDirFiles = new ArrayList<>(Arrays.asList(secondDirFilesList));
        HashMap<String, ArrayList<File>> filesGroupedUnderNames =
                groupFilesByNames(firstDirFiles, secondDirFiles);
        HashMap<String, File> namesToFilesForMoving =
                getNamesAndFilesWithoutDuplicatesForMoving(firstDirFiles, secondDirFiles, filesGroupedUnderNames);
        moveFilesWithNewNames(targetDir, namesToFilesForMoving);
    }

    private static void checkFirstAndSecondDirsNotEmpty(File[] firstDirFilesList, File[] secondDirFilesList) {
        if (firstDirFilesList == null || secondDirFilesList == null)
            throw new RuntimeException("Хотя бы одна из выбранных папок пуста");
    }

    private static void checkDirsExistAndAreDir(File firstDir, File secondDir, File targetDir)
            throws FileNotFoundException {
        boolean anyDirNotExist = !firstDir.exists() || !secondDir.exists() || !targetDir.exists();
        boolean anyDirIsntDir = !firstDir.isDirectory() || !secondDir.isDirectory() || !targetDir.isDirectory();
        if (anyDirNotExist || anyDirIsntDir)
            throw new FileNotFoundException("Хотя бы одна из заданных папок не существует");
    }

    private static void moveFilesWithNewNames(File targetDir, HashMap<String, File> namesToFilesForMoving)
            throws IOException {
        for (HashMap.Entry<String, File> namesToFilesForMovingEntry : namesToFilesForMoving.entrySet()) {
            File fileWillBeMoved = namesToFilesForMovingEntry.getValue();
            String fileWillBeMovedNewName = namesToFilesForMovingEntry.getKey();
            moveFileToDirectory(fileWillBeMoved, targetDir, fileWillBeMovedNewName);
        }
    }

    private static HashMap<String, File> getNamesAndFilesWithoutDuplicatesForMoving(
            ArrayList<File> firstDirFiles, ArrayList<File> secondDirFiles,
            HashMap<String, ArrayList<File>> namesAndFilesWithTheseNames
    ) {
        var namesToFilesForMoving = new HashMap<String, File>();
        for (File firstDirFile : firstDirFiles)
            namesToFilesForMoving.put(firstDirFile.getName(), firstDirFile);
        for (File secondDirFile : secondDirFiles)
            namesToFilesForMoving.put(secondDirFile.getName(), secondDirFile);
        for (HashMap.Entry<String, ArrayList<File>> nameAndFilesWithThisName : namesAndFilesWithTheseNames.entrySet()) {
            List<File> files = nameAndFilesWithThisName.getValue();
            List<File> uniqueFiles = distinctFiles(files);
            giveIndexesToSameNamesFiles(namesToFilesForMoving, uniqueFiles);
        }
        return namesToFilesForMoving;
    }

    private static void giveIndexesToSameNamesFiles(HashMap<String, File> namesToFilesForMoving, List<File> uniqueFiles) {
        for (int i = 0; i < uniqueFiles.size(); i++) {
            File uniqueFile = uniqueFiles.get(i);
            if (i == 0) {
                namesToFilesForMoving.put(uniqueFile.getName(), uniqueFile);
                continue;
            }
            String fileNameWithIndex = getFileNameWithIndex(uniqueFile.getName(), i);
            namesToFilesForMoving.put(fileNameWithIndex, uniqueFile);
        }
    }

    private static HashMap<String, ArrayList<File>> groupFilesByNames(
            ArrayList<File> firstDirFiles, ArrayList<File> secondDirFiles
    ) {
        var filesGroupedByName = new HashMap<String, ArrayList<File>>();
        ListIterator<File> firstDirFilesIterator = firstDirFiles.listIterator();
        ListIterator<File> secondDirFilesIterator = secondDirFiles.listIterator();
        firstDirFilesLoop:
        while (firstDirFilesIterator.hasNext()) {
            while (secondDirFilesIterator.hasNext()) {
                File firstDirFile = firstDirFilesIterator.next();
                File secondDirFile = secondDirFilesIterator.next();
                String firstDirFileName = firstDirFile.getName();
                if (firstDirFileName.equals(secondDirFile.getName())) {
                    filesGroupedByName.putIfAbsent(firstDirFileName, new ArrayList<>(2));
                    ArrayList<File> filesWithSameName = filesGroupedByName.get(firstDirFileName);
                    filesWithSameName.addAll(List.of(firstDirFile, secondDirFile));
                    // it's important to notice: any directory can't contain two or more files with the same names
                    // remove files from the lists to retain only files without duplicates in appropriate lists
                    firstDirFilesIterator.remove();
                    secondDirFilesIterator.remove();
                    continue firstDirFilesLoop;
                }
            }
        }
        return filesGroupedByName;
    }

    private static String getFileNameWithIndex(String fileName, int index) {
        var fileNamePartsSplittedWithDot = new ArrayList<String>(Arrays.asList(fileName.split("\\.")));
        List<String> fileNamePartsSplittedWithDotWithoutExtension =
                fileNamePartsSplittedWithDot.subList(0, fileNamePartsSplittedWithDot.size() - 1);
        String fileNameWithoutExtension = String.join(".", fileNamePartsSplittedWithDotWithoutExtension);
        String fileExtension = fileNamePartsSplittedWithDot.get(fileNamePartsSplittedWithDot.size() - 1);
        return fileNameWithoutExtension + "(" + index + ")" + "." + fileExtension;
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