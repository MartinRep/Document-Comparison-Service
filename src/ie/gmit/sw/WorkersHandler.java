package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkersHandler 
{
	private static ArrayBlockingQueue<Job> inQueue;
	private static ConcurrentHashMap<Integer, Results> outQueue;
	private static WorkersHandler instance;
	private static ExecutorService executor;
	private static volatile int jobNumber = 0;
	private static volatile int workerNumber = 0;
	private static ArrayBlockingQueue<String> servLog;
	
	private WorkersHandler(int numOfWorkers)
	{
		// Shared variables initialization
		inQueue = new ArrayBlockingQueue<>(numOfWorkers);
		outQueue = new ConcurrentHashMap<>();
		servLog = new ArrayBlockingQueue<>(numOfWorkers);
		//initialization of workers thread pool. The size determined in web.xml.
		executor = Executors.newFixedThreadPool(numOfWorkers);
		// Population of Thread pool
		for (int i = 0; i < numOfWorkers; i++) 
		{
			Runnable worker = new Worker();
			executor.execute(worker);
		}
		servLog.offer("JobHandler initialized.");
	}
	
	// Singleton
	public static synchronized WorkersHandler init(int numOfWorkers)
	{
		if(instance == null)
		{
			instance = new WorkersHandler(numOfWorkers);
		}
		return instance;
	}
	
	public static ArrayBlockingQueue<Job> getInQueue()
	{
		return inQueue;
	}
	
	public static ConcurrentHashMap<Integer, Results> getOutQueue()
	{
		return outQueue;
	}
	
	// This way it's easy to keep track on number of total jobs. Can be saved/processed
	public static synchronized int getJobNumber()
	{
		jobNumber++;
		return jobNumber;
	}
	
	// Easy way to keep track
	public static synchronized int getWorkerNumber()
	{
		workerNumber++;
		return workerNumber;
	}
	
	public static ArrayBlockingQueue<String> getServLog()
	{
		return servLog;
	}
	
	// Safe shutting down of thread pool, to avoid possible memory leaks
	public static void shutdown()
	{
		executor.shutdown();
		servLog.offer(String.format("Total jobs processed: %d", jobNumber));
		servLog.offer(String.format("Total workers spawned: %d", workerNumber));
	}

	@Override
	protected void finalize() throws Throwable {
		shutdown();
		super.finalize();
	}
	
	
	
}
