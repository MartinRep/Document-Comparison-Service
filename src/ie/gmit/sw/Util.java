package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class. Facade Singleton. Initialize and share application wide variables.
 * Servlet initialized variables, Logging queue, Jobs inQueue, Results outQueue, etc..
 * 
 * @author Martin Repicky G00328337@gmit.ie
 *
 */
public class Util {
    private static ArrayBlockingQueue<Job> inQueue;
    private static ConcurrentHashMap<Integer, Results> outQueue;
    private static ThreadPoolService workersPool;
    private static volatile int jobNumber = 0;
    private static volatile int workerNumber = 0;
    private static ArrayBlockingQueue<String> servLog;

    private Util() {
    }

    /**
     * Initialize application wide, concurrent variables as well as ThreadPool of workers.
     * @param numOfWorkers Size of ThreadPool of Worker.class, inQueue of Job.class and Logging queue for String message logs.
     */
    public static Boolean init() {
	// initialization of workers thread pool. The size determined in web.xml.
	try {
	    inQueue = new ArrayBlockingQueue<>(Config.numOfWorkers);
	    servLog = new ArrayBlockingQueue<>(Config.numOfWorkers);
	    outQueue = new ConcurrentHashMap<>();
	    workersPool = new ThreadPoolService(Worker.class, Config.numOfWorkers);
	    if (Util.isLoggingOn()) LogService.init(Util.getServLog(), Config.logFile);
	    Util.logMessage(String.format("Thread Pool initialized with %d heavyWorkers", Config.numOfWorkers));
	    return true;
	} catch (Exception e) {
	    Util.logMessage("ERROR: ThreadPool failed to initialize with Error message: " + e.getMessage());
	    // In case Logging service failed to start.
	    System.out.println("ERROR: ThreadPool failed to initialize with Error message: " + e.getMessage());
	}
	return false;
    }

    /**
     * @return ArrayBlockingQueue. Used by UploadHandler Servlet and Worker 
     */
    public static ArrayBlockingQueue<Job> getInQueue() {
	return inQueue;
    }

    /**
     * @return ConcurrentHashmap<Integer. Results>. Used by worker and PoolHandler Servlet.
     */
    public static ConcurrentHashMap<Integer, Results> getOutQueue() {
	return outQueue;
    }
    
    /**
     * 
     * @return jobNumber integer. Used by UploadHandler to assign unique job number.
     */
    public static synchronized int getJobNumber() {
	jobNumber++;
	return jobNumber;
    }

    /**
     * @return wokerNumber integer. Used by Worker.class to keep track of workers. Not displayed, only for logging purposes.
     */
    public static synchronized int getWorkerNumber() {
	workerNumber++;
	return workerNumber;
    }
    
    /**
     * Add a new Worker to Thread pool. If more than predefine workers are submitted 
     * then they are held in a queue until threads become available.
     */
    public static boolean processJob(Job job)
    {
	try {
	    workersPool.addHeavyWorker();
	    inQueue.put(job);
	} catch (CloneNotSupportedException | InterruptedException e) {
	    Util.logMessage("ERROR adding new Worker to ThreadPool: " + e.getMessage());
	    return false;
	}
	return true;
    }

    /**
     * @return ArrayBlockingQueue<String>. Used by LogService.class.
     */
    public static ArrayBlockingQueue<String> getServLog() {
	return servLog;
    }

    /**
     * @return Integer. Used by Worker.class and passed to MinHash.class
     */
    public static int getHashFunctions() {
	return Config.hashFunctions;
    }

    public static int getShingleSize() {
        return Config.shingleSize;
    }

    /**
     * @return DocumentDao interface. Used by MinHash.class when accessing database.
     */
    public static DocumentDao getDb() {
	return Config.db;
    }

    /**
     * @return Boolean. Determine the state of Logging service, On or Off.
     */
    public static boolean isLoggingOn() {
	return Config.loggingOn;
    }

    public static int getRefreshRate() {
        return Config.refreshRate;
    }

    /**
     * Logging service entry point. Function is called by any class to log,
     *  to console and file, any warnings, errors and such. ArrayBlockingQueue.offer is nonBlocking.
     * @param message String to be logged. 
     */
    public static void logMessage(String message) {
	if (Util.isLoggingOn())
	    servLog.offer(message);
    }
    
    /**
     * Safe shutting down of thread pool, prevent possible memory leaks.
     */
    public static void shutdown() {
	workersPool.shutDown();
	Util.logMessage("ThreadPool Shutdown.");
	Util.logMessage(String.format("Total jobs processed: %d", jobNumber));
	Util.logMessage(String.format("Total workers spawned: %d", workerNumber));
	if (Util.isLoggingOn()) LogService.shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
	shutdown();
	super.finalize();
    }
    
    public static class Config
    {
	private static int numOfWorkers;
	private static int hashFunctions;
	private static int shingleSize;
	private static DocumentDao db;
	private static boolean loggingOn = false;
	private static int refreshRate;
	private static String logFile;
	
	public static void setNumOfWorkers(int numOfWorkers) {
	    Config.numOfWorkers = numOfWorkers;
	}
	
	/**
	* @param shingles Used by UploadHandler Servlet when Initializing. Value read from web.xml
	*/
	public static void setHashFunctions(int hashFunctions) {
	    Config.hashFunctions = hashFunctions;
	}
	public static void setShingleSize(int shingleSize) {
	    Config.shingleSize = shingleSize;
	}
	
	/**
	 * @param db DocumentDao interface type for persistent storage. It set by UploadHandler Servlet
	 */
	public static void setDb(DocumentDao db) {
	    Config.db = db;
	}
	
	/**
	* @param loggingOn Boolean. Set by UploadHandler Servlet, read from web.xml
	*/
	public static void setLoggingOn(boolean loggingOn) {
	    Config.loggingOn = loggingOn;
	}
	public static void setRefreshRate(int refreshRate) {
	    Config.refreshRate = refreshRate;
	}
	public static void setLogFile(String logFile) {
	    Config.logFile = logFile;
	}
	
    }

}
