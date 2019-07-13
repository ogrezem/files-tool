package ru.ogrezem.sameFilesRenamer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SameFilesRenamerTest {

    @Test
    public void sortSameFiles() {
        String fileName = "gogo.jpg";
        int index = 2;
        var fileNamePartsSplittedWithDot = new ArrayList<String>(Arrays.asList(fileName.split("\\.")));

        String fileExtension = fileNamePartsSplittedWithDot.get(fileNamePartsSplittedWithDot.size() - 1);
        List<String> fileNamePartsSplittedWithDotWithoutExtension =
                fileNamePartsSplittedWithDot.subList(0, fileNamePartsSplittedWithDot.size() - 1);
        String fileNameWithoutExtension = String.join(".", fileNamePartsSplittedWithDotWithoutExtension);
        System.out.println(fileNameWithoutExtension + "(" + index + ")" + "." + fileExtension);
    }
}