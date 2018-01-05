package ie.gmit.sw;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Creates and keep re-populating ThreadPool with HeavyWorker.class workers. Generalized class, can be reused. 
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public class ThreadPoolService {
    private static ExecutorService executor;

    /**
     * Creates ThreadPool of size numOfWorkers with type workerClass HeavyWorkers.
     * @param workerClass HeavyWorker abstract class type. Runnable and Cloneable.
     * @param numOfWorkers Size of the ThreadPool
     * @throws Exception
     */
    
    @SuppressWarnings("rawtypes")
    public static ExecutorService initThreadPool(Class workerClass, int numOfWorkers) throws Exception {
	executor = Executors.newFixedThreadPool(numOfWorkers, new ThreadFactory() {
	    
	    @Override
	    public Thread newThread(Runnable r) {
		return new Thread(r);
	    }
	});
	Util.logMessage(String.format("Thread Pool initialized with %d workers", numOfWorkers));
	return executor;
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
