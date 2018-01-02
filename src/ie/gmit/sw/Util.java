package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Util 
{
	private static ArrayBlockingQueue<Job> inQueue;
	private static ConcurrentHashMap<Integer, Results> outQueue;
	private static Util instance;
	private static volatile int jobNumber = 0;
	private static volatile int workerNumber = 0;
	private static ArrayBlockingQueue<String> servLog;
	private static int shingles;
	private static DocumentDAO db;
	private static boolean loggingON = false;
	
	private Util()
	{
	}
	
	// Singleton
	public static synchronized Util init()
	{
		if(instance == null)
		{
			instance = new Util();
		}
		return instance;
	}
	
	public static void initThreadPool(int numOfWorkers)
	{
		inQueue = new ArrayBlockingQueue<>(numOfWorkers);
		servLog = new ArrayBlockingQueue<>(numOfWorkers);
		outQueue = new ConcurrentHashMap<>();
		//initialization of workers thread pool. The size determined in web.xml.
		try
		{
			ThreadPoolService.initThreadPool(Worker.class ,numOfWorkers);
			logMessage("ThreadPool of size: "+ numOfWorkers +" initialized.");
		} catch (Exception e)
		{
			Util.logMessage("ERROR: ThreadPool failed to initialize with Error message: " + e.getMessage());
		}
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
	
	public static int getShingles()
	{
		return shingles;
	}

	public static void setShingles(int shingles)
	{
		Util.shingles = shingles;
	}
	

	public static DocumentDAO getDb()
	{
		return db;
	}

	public static void setDb(DocumentDAO db)
	{
		Util.db = db;
	}

	public static boolean isLoggingON()
	{
		return loggingON;
	}

	public static void setLoggingON(boolean loggingON)
	{
		Util.loggingON = loggingON;
	}
	
	public static void logMessage(String message)
	{
		if(Util.isLoggingON())servLog.offer(message);
	}

	// Safe shutting down of thread pool, to avoid possible memory leaks
	public static void shutdown()
	{
		ThreadPoolService.shutDown();
		if(Util.isLoggingON()) LogService.shutdown();
		logMessage(String.format("Total jobs processed: %d", jobNumber));
		logMessage(String.format("Total workers spawned: %d", workerNumber));
	}

	@Override
	protected void finalize() throws Throwable {
		shutdown();
		super.finalize();
	}
	
	
	
}
