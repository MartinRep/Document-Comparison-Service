package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobHandler 
{
	private ArrayBlockingQueue<Job> inQueue;
	private ConcurrentHashMap<Integer, Results> outQueue;
	private static JobHandler instance;
	private static ExecutorService executor;
	private static volatile int jobNumber = 0;
	
	private JobHandler(int numOfWorkers)
	{
		inQueue = new ArrayBlockingQueue<>(numOfWorkers);
		outQueue = new ConcurrentHashMap<>();
	}
	
	public static synchronized JobHandler Init(int numOfWorkers)
	{
		if(instance == null)
		{
			instance = new JobHandler(numOfWorkers);
			//also initialize actual workers 10 or so.
			executor = Executors.newFixedThreadPool(numOfWorkers);
			for (int i = 0; i < numOfWorkers; i++) 
			{
				//Runnable worker = new Worker();
				//executor.execute(worker);
			}
		}
		return instance;
	}
	
	public ArrayBlockingQueue<Job> GetInQueue()
	{
		return inQueue;
	}
	
	public ConcurrentHashMap<Integer, Results> GetOutQueue()
	{
		return outQueue;
	}
	
	public synchronized int GetJobNumber()
	{
		jobNumber++;
		return jobNumber;
	}
	
	public void Shutdown()
	{
		executor.shutdown();
	}

	@Override
	protected void finalize() throws Throwable {
		Shutdown();
		super.finalize();
	}
	
	
	
}
