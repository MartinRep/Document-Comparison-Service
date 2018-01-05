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
    private ExecutorService executor;
    private HeavyWorker worker;
    /**
     * Creates ThreadPool of size numOfWorkers with type workerClass HeavyWorkers.
     * @param workerClass HeavyWorker abstract class type. Runnable and Cloneable.
     * @param numOfWorkers Size of the ThreadPool
     * @throws Exception
     */
    
    public ThreadPoolService() {
 	super();
     }
    
    @SuppressWarnings("rawtypes")
    public void initThreadPool(Class workerClass, int numOfWorkers) throws InstantiationException, IllegalAccessException {
	executor = Executors.newFixedThreadPool(numOfWorkers, new ThreadFactory() {
	    
	    @Override
	    public Thread newThread(Runnable r) {
		return new Thread(r);
	    }
	});
	worker = (HeavyWorker) workerClass.newInstance();
	Util.logMessage(String.format("Thread Pool initialized with %d workers", numOfWorkers));
    }
    
    /**
     * Add a new Worker to Thread pool. If more than predefine workers are submitted then 
     * they are held in a queue until threads become available.
     * @throws CloneNotSupportedException
     */
    
    public void addWorker() throws CloneNotSupportedException
    {
	Runnable newWorker = (Runnable) worker.clone();
	executor.execute(newWorker);
    }

    /**
     *  Orderly shutdown the ThreadPool, prevents memory leaks
     */
    public void shutDown() {
	executor.shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
	shutDown();
	super.finalize();
    }
}
