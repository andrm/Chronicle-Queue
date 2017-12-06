package net.openhft.chronicle.queue;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReadOnlyTest {

    private static final String STR1 = "hello", STR2 = "hey";
    private File chroniclePath;

    private long lastIndexBeforeCycling;
    private SingleChronicleQueue readWrite;
    private SingleChronicleQueue readWrite2;
    @Before
    public void setup() throws InterruptedException {
        chroniclePath = new File(OS.TARGET, "read_only");
        readWrite = SingleChronicleQueueBuilder.binary(chroniclePath)
                .readOnly(false).buffered(false)
                .rollCycle(RollCycles.TEST_SECONDLY)
                //.testBlockSize()
                .build();
        final ExcerptAppender appender = readWrite.acquireAppender();
            appender.writeText(STR1);
            appender.writeText(STR2);
            lastIndexBeforeCycling = appender.lastIndexAppended();
            Thread.sleep(5100);



        readWrite2 = SingleChronicleQueueBuilder.binary(chroniclePath)
                .readOnly(false).buffered(false)
                .rollCycle(RollCycles.TEST_SECONDLY)
                //.testBlockSize()
                .build();
        final ExcerptAppender appender2 = readWrite2.acquireAppender();
        appender2.writeText(STR1);
        appender2.writeText(STR2);

    }

    @After
    public void teardown() {
        readWrite.close();
        readWrite2.close();
        IOTools.shallowDeleteDirWithFiles(chroniclePath);
    }

    @Test
    public void testReadFromReadOnlyChronicle() {
        try (SingleChronicleQueue out = SingleChronicleQueueBuilder
                .binary(chroniclePath).buffered(false)
                //.testBlockSize()
                .rollCycle(RollCycles.TEST_SECONDLY)
                .readOnly(true)
                .build()) {
            // check dump
            //assertTrue(out.dump().length() > 1);
            // and tailer
            ExcerptTailer tailer = out.createTailer();
            tailer.moveToIndex(lastIndexBeforeCycling);
           // assertEquals(STR1, tailer.readText());
            assertEquals(STR2, tailer.readText());
            assertEquals(STR1, tailer.readText());
            assertEquals(STR2, tailer.readText());
        }
    }
}
