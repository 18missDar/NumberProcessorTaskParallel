import java.util.ArrayList;
import java.util.List;

public class NumberProcessor {

    private static final int NUM_THREADS = 4;
    private static final int RANGE_SIZE = 1000000;

    private static volatile List<NumberData> results = new ArrayList<>();

    public static void main(String[] args) {
        // Generate a very long list of numbers
        List<Integer> numbers = generateNumbers(100000000);

        // Create and start worker threads
        List<Thread> threads = new ArrayList<>();
        int rangeStart = 0;
        for (int i = 0; i < NUM_THREADS; i++) {
            int rangeEnd = rangeStart + RANGE_SIZE;
            if (rangeEnd > numbers.size()) {
                rangeEnd = numbers.size();
            }
            Thread t = new Thread(new NumberFinder(numbers.subList(rangeStart, rangeEnd), i));
            threads.add(t);
            t.start();
            rangeStart = rangeEnd;
        }

        // Wait for worker threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Print results
        System.out.printf("%-10s%-10s%-10s\n", "Number", "Position", "Thread");
        for (NumberData data : results) {
            System.out.printf("%-10d%-10d%-10d\n", data.getNumber(), data.getPosition(), data.getThreadNumber());
        }
    }

    private static List<Integer> generateNumbers(int count) {
        List<Integer> numbers = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            numbers.add(i);
        }
        return numbers;
    }

    private static class NumberFinder implements Runnable {

        private List<Integer> numbers;
        private int threadNumber;

        public NumberFinder(List<Integer> numbers, int threadNumber) {
            this.numbers = numbers;
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            for (int i = 0; i < numbers.size(); i++) {
                int number = numbers.get(i);
                if (number % 3 == 0) {
                    results.add(new NumberData(number, i, threadNumber));
                }
            }
        }
    }

    private static class NumberData {

        private int number;
        private int position;
        private int threadNumber;

        public NumberData(int number, int position, int threadNumber) {
            this.number = number;
            this.position = position;
            this.threadNumber = threadNumber;
        }

        public int getNumber() {
            return number;
        }

        public int getPosition() {
            return position;
        }

        public int getThreadNumber() {
            return threadNumber;
        }
    }
}