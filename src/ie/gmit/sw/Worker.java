package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Worker implements Runnable 
{

	private static ArrayBlockingQueue<Job> inQueue;
	private static ConcurrentHashMap<Integer, Results> outQueue;
	private static ArrayBlockingQueue<String> servLog;
	private int workerNumber;
	private Job job;
	private Results results;
	
	public Worker() 
	{
	}

	@Override
	public void run() 
	{
		workerNumber = Util.getWorkerNumber();
		inQueue = Util.getInQueue();
		outQueue = Util.getOutQueue();
		servLog = Util.getServLog();
		try {
			job = inQueue.take();
			servLog.offer(String.format("Worker number %d start work on job number: %d", workerNumber, job.getJobNumber()));
			MinHash minHash = new MinHash(Util.getDb(), Util.getShingles());
			results = minHash.processJob(job);
			//add Result into outQueue
			outQueue.put(job.getJobNumber(), results);
		} catch (InterruptedException e) {
			servLog.offer(String.format("Worker number: %d processing job number: %d, Document: %s caused error: %s", workerNumber,
					job.getJobNumber(), job.getTitle(), e.getMessage()));
		}

	}

}
