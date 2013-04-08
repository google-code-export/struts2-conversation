package com.github.overengineer.scope.testutil;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;

/**
 */
public class ConcurrentExecutionAssistant {

    public static class TestThreadGroup {
        Execution execution;
        boolean running = false;
        Long callCount = 0L;
        Collection<Thread> threads = new HashSet<Thread>();
        public TestThreadGroup(final Execution execution, int numThreads) {
            this.execution = execution;
            for (int i = 0; i < numThreads; i++) {
                threads.add(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (running) {
                            try {
                                execution.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            callCount++;
                        }
                    }
                }));
            }
        }
        public long run(long duration, String id) throws Exception {

            PrintStream out = System.out;

            //since printing to the screen is the biggest bottleneck, we remove it
            System.setOut(new PrintStream(new FileOutputStream("/dev/null")));

            primeJVM();

            running = true;
            for (Thread thread : threads) {
                thread.start();
            }
            Thread.sleep(duration);
            running = false;

            System.setOut(out);

            long count = callCount;

            System.out.println(id + " events");

            System.out.println("    Processed " + count + " events in " + duration / 1000 + " seconds");

            double doubleDuration = duration * 1.0d;
            double msPerEvent = doubleDuration / count;

            System.out.println("    Average processing required " + msPerEvent + "ms");

            return count;
        }
        private void primeJVM() throws Exception {
            for (int i = 0; i < 1000000; i++) {
                execution.execute();
            }
        }
    }

    public interface Execution {
        void execute() throws Exception;
    }
}

