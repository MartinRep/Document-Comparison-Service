package ie.gmit.sw;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolService
{
	private static ExecutorService executor;
	
	@SuppressWarnings("rawtypes")
	public static void initThreadPool(Class workerClass, int numOfWorkers) throws Exception
	{
		//initialization of workers thread pool. The size determined in web.xml.
		executor = Executors.newFixedThreadPool(numOfWorkers);
		// Population of Thread pool
		for (int i = 0; i < numOfWorkers; i++) 
		{
			Runnable worker = (Runnable) workerClass.newInstance();
			executor.execute(worker);
		}
		Util.logMessage(String.format("Thread Pool initialized with %d workers", numOfWorkers));
	}
	
	public static void shutDown()
	{
		executor.shutdown();
	}

	@Override
	protected void finalize() throws Throwable
	{
		ThreadPoolService.shutDown();
		super.finalize();
	}
	
	

}
