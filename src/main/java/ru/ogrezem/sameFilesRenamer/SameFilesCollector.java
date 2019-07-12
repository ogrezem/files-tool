package ru.ogrezem.sameFilesRenamer;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SameFilesCollector implements Collector<File, Integer, List<File>> {

    @Override
    public Supplier<Integer> supplier() {
        return null;
    }

    @Override
    public BiConsumer<Integer, File> accumulator() {
        return null;
    }

    @Override
    public BinaryOperator<Integer> combiner() {
        return null;
    }

    @Override
    public Function<Integer, List<File>> finisher() {
        return null;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return null;
    }
}
