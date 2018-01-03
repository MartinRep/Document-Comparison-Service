package ie.gmit.sw;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolService {
    private static ExecutorService executor;

    @SuppressWarnings("rawtypes")
    public static void initThreadPool(Class workerClass, int numOfWorkers) throws Exception {
	executor = Executors.newFixedThreadPool(numOfWorkers);
	HeavyWorker heavyWorker = (HeavyWorker) workerClass.newInstance();
	for (int i = 0; i < numOfWorkers; i++) {
	    Runnable worker = (Runnable) heavyWorker.clone();
	    executor.execute(worker);
	}
	Util.logMessage(String.format("Thread Pool initialized with %d workers", numOfWorkers));
    }

    public static void shutDown() {
	executor.shutdown();
	Util.logMessage("ThreadPool Shutdown.");
    }

    @Override
    protected void finalize() throws Throwable {
	ThreadPoolService.shutDown();
	super.finalize();
    }
}
