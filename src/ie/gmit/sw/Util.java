package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Utility class. Singleton. Initialize and share application wide variables.
 * Servlet initialized variables, Logging queue, Jobs inQueue, Results outQueue, etc..
 * 
 * @author Martin Repicky G00328337@gmit.ie
 *
 */
public class Util {
    private static ArrayBlockingQueue<Job> inQueue;
    private static ConcurrentHashMap<Integer, Results> outQueue;
    private static Util instance;
    private static ExecutorService executor;
    private static volatile int jobNumber = 0;
    private static volatile int workerNumber = 0;
    private static ArrayBlockingQueue<String> servLog;
    private static int hashFunctions;
    private static int shingleSize;
    private static DocumentDao db;
    private static boolean loggingOn = false;
    private static int refreshRate;

    private Util() {
    }

    /**
     * Util.class Singleton initialize function. 
     */
    public static synchronized Util init() {
	if (instance == null) {
	    instance = new Util();
	}
	return instance;
    }
    
    /**
     * Initialize application wide, concurrent variables as well as ThreadPool 
     * @param numOfWorkers Size of ThreadPool of Worker.class, inQueue of Job.class and Logging queue for String message logs.
     */
    public static void initThreadPool(int numOfWorkers) {
	inQueue = new ArrayBlockingQueue<>(numOfWorkers);
	servLog = new ArrayBlockingQueue<>(numOfWorkers);
	outQueue = new ConcurrentHashMap<>();
	// initialization of workers thread pool. The size determined in web.xml.
	try {
	    executor = ThreadPoolService.initThreadPool(Worker.class, numOfWorkers);
	} catch (Exception e) {
	    Util.logMessage("ERROR: ThreadPool failed to initialize with Error message: " + e.getMessage());
	}
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
    
    public static ExecutorService getExecutor() {
        return executor;
    }

    public static void setExecutor(ExecutorService executor) {
        Util.executor = executor;
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
     * @return ArrayBlockingQueue<String>. Used by LogService.class.
     */
    public static ArrayBlockingQueue<String> getServLog() {
	return servLog;
    }

    /**
     * @return Integer. Used by Worker.class and passed to MinHash.class
     */
    public static int getHashFunctions() {
	return hashFunctions;
    }

    /**
     * @param shingles Used by UploadHandler Servlet when Initializing. Value read from web.xml
     */
    public static void setHashFunctions(int shingles) {
	Util.hashFunctions = shingles;
    }
    
    public static int getShingleSize() {
        return shingleSize;
    }

    public static void setShingleSize(int shingleCount) {
        Util.shingleSize = shingleCount;
    }

    /**
     * @return DocumentDao interface. Used by MinHash.class when accessing database.
     */
    public static DocumentDao getDb() {
	return db;
    }

    /**
     * @param db DocumentDao interface type for persistent storage. It set by UploadHandler Servlet
     */
    
    public static void setDb(DocumentDao db) {
	Util.db = db;
    }

    /**
     * @return Boolean. Determine the state of Logging service, On or Off.
     */
    public static boolean isLoggingOn() {
	return loggingOn;
    }

    /**
     * @param loggingOn Boolean. Set by UploadHandler Servlet, read from web.xml
     */
    public static void setLoggingON(boolean loggingOn) {
	Util.loggingOn = loggingOn;
    }
    
    public static int getRefreshRate() {
        return refreshRate;
    }

    public static void setRefreshRate(int refreshRate) {
        Util.refreshRate = refreshRate;
    }

    /**
     * Logging service entry point. Function is called by any class to log,
     *  to console and file, any warnings, errors and such. 
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
	ThreadPoolService.shutDown();
	logMessage(String.format("Total jobs processed: %d", jobNumber));
	logMessage(String.format("Total workers spawned: %d", workerNumber));
	if (Util.isLoggingOn())
	    LogService.shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
	shutdown();
	super.finalize();
    }

}
