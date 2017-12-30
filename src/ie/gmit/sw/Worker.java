package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Worker implements Runnable 
{

	private static ArrayBlockingQueue<Job> inQueue;
	private static ConcurrentHashMap<Integer, Results> outQueue;
	private static ArrayBlockingQueue<String> servLog;
	private volatile static int workerNumber = 0;
	private int thisWorkerNumber;
	private Job job;
	private Results results;
	
	public Worker() 
	{
		inQueue = WorkersHandler.GetInQueue();
		outQueue = WorkersHandler.GetOutQueue();
		servLog = WorkersHandler.GetServLog();
	}

	@Override
	public void run() 
	{
		thisWorkerNumber = workerNumber;
		workerNumber++;
		servLog.offer(String.format("Worker number %d start work on job number: %d", thisWorkerNumber, job.getJobNumber()));
		try {
			job = inQueue.take();
			JobProcessor jobProcessor = new MinHash();
			results = jobProcessor.processJob(job);
			// add Result into outQueue
			outQueue.put(job.getJobNumber(), results);
		} catch (InterruptedException e) {
			servLog.offer(String.format("Worker number: %d processing job number: %d, Document: %s caused error: %s", thisWorkerNumber,
					job.getJobNumber(), job.getTitle(), e.getMessage()));
		}

	}

}
