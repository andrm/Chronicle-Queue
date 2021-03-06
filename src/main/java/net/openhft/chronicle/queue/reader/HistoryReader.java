package net.openhft.chronicle.queue.reader;

import net.openhft.chronicle.core.util.Histogram;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.internal.reader.InternalChronicleHistoryReader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface HistoryReader {

    HistoryReader withMessageSink(final Consumer<String> messageSink);

    HistoryReader withBasePath(final Path path);

    HistoryReader withProgress(boolean p);

    HistoryReader withTimeUnit(TimeUnit p);

    HistoryReader withHistosByMethod(boolean b);

    HistoryReader withIgnore(long ignore);

    HistoryReader withMeasurementWindow(long measurementWindow);

    HistoryReader withSummaryOutput(int offset);

    void execute();

    Map<String, Histogram> readChronicle();

    void outputData();

    /**
     * Creates and returns a new history reader that will use
     * the queue located at {@link #withBasePath } provided later.
     *
     * @return a new history reader that will use
     * the queue located at {@link #withBasePath } provided later
     */
    static HistoryReader create() {
        return new InternalChronicleHistoryReader();
    }

    /**
     * Creates and returns a new history reader that will use
     * the provided {@code queueSupplier } to provide the queue.
     *
     * @return a new history reader that will use
     *         the provided {@code queueSupplier } to provide the queue.
     */
    static HistoryReader create(@NotNull final Supplier<? extends ChronicleQueue> queueSupplier) {
        return new InternalChronicleHistoryReader(queueSupplier);
    }

}