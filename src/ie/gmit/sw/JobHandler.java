package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobHandler 
{
	private static ArrayBlockingQueue<Job> inQueue;
	private static ConcurrentHashMap<Integer, Results> outQueue;
	private static JobHandler instance;
	private static ExecutorService executor;
	private static volatile int jobNumber = 0;
	private static ArrayBlockingQueue<String> servLog;
	
	private JobHandler(int numOfWorkers)
	{
		inQueue = new ArrayBlockingQueue<>(numOfWorkers);
		outQueue = new ConcurrentHashMap<>();
		servLog = new ArrayBlockingQueue<>(numOfWorkers);
		//also initialize actual workers thread pool of size workers from web.xml.
		executor = Executors.newFixedThreadPool(numOfWorkers);
		for (int i = 0; i < numOfWorkers; i++) 
		{
			Runnable worker = new Worker();
			executor.execute(worker);
		}
		servLog.offer("JobHandler initialized.");
	}
	
	public static synchronized JobHandler Init(int numOfWorkers)
	{
		if(instance == null)
		{
			instance = new JobHandler(numOfWorkers);
		}
		return instance;
	}
	
	public static ArrayBlockingQueue<Job> GetInQueue()
	{
		return inQueue;
	}
	
	public static ConcurrentHashMap<Integer, Results> GetOutQueue()
	{
		return outQueue;
	}
	
	public static synchronized int GetJobNumber()
	{
		jobNumber++;
		return jobNumber;
	}
	
	public static ArrayBlockingQueue<String> GetServLog()
	{
		return servLog;
	}
	
	public static void Shutdown()
	{
		executor.shutdown();
	}

	@Override
	protected void finalize() throws Throwable {
		Shutdown();
		super.finalize();
	}
	
	
	
}
