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
    private HeavyWorker heavyWorker;
    private Runnable worker;
    /**
     * Creates ThreadPool of size numOfWorkers with type workerClass HeavyWorkers. Prototype design pattern.
     * @param workerClass HeavyWorker abstract class type. Runnable and Cloneable.
     * @param numOfWorkers Size of the Thread Pool
     * @throws Exception
     */
    
    @SuppressWarnings("rawtypes")
    public ThreadPoolService(Class workerClass, int numOfWorkers) throws InstantiationException, IllegalAccessException {
	executor = Executors.newFixedThreadPool(numOfWorkers, new ThreadFactory() {
	    
	    @Override
	    public Thread newThread(Runnable r) {
		return new Thread(r);
	    }
	});
	heavyWorker = (HeavyWorker) workerClass.newInstance();
    }
    
    /**
     * Creates ThreadPool of size numOfWorkers with Runnable type worker.
     * @param worker A Runnable type running in the Thread pool
     * @param numOfWorkers Size of the Thread pool
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    
    public ThreadPoolService(Runnable worker, int numOfWorkers) throws InstantiationException, IllegalAccessException {
	executor = Executors.newFixedThreadPool(numOfWorkers, new ThreadFactory() {
	    
	    @Override
	    public Thread newThread(Runnable r) {
		return new Thread(r);
	    }
	});
	this.worker = worker; 
    }
    
    /**
     * Add new instance of worker type Runnable to Thread pool. If more than predefine workers are submitted then 
     * they are held in a queue until threads become available. 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    
    public void addWorker() throws InstantiationException, IllegalAccessException
    {
	Runnable newWorker = worker.getClass().newInstance();
	executor.execute(newWorker);
    }
    
    /**
     * Add a heavyWorker clone to Thread pool. If more than predefine workers are submitted then 
     * they are held in a queue until threads become available.
     * @throws CloneNotSupportedException
     */
    
    public void addHeavyWorker() throws CloneNotSupportedException
    {
	Runnable newWorker = (Runnable) heavyWorker.clone();
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
