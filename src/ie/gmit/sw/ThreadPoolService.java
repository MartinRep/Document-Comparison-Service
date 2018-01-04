package ie.gmit.sw;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Creates and keep re-populating ThreadPool with HeavyWorker.class workers. Generalized class, can be reused. 
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public class ThreadPoolService {
    private static ExecutorService executor;

    /**
     * Creates ThreadPool of size numOfWorkers with type workerClass Workers.
     * @param workerClass HeavyWorker abstract class type. Runnable and Cloneable.
     * @param numOfWorkers Size of the ThreadPool
     * @throws Exception
     */
    
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

    /**
     * Function to orderly shutdown the ThreadPool, prevents memory leaks
     */
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
