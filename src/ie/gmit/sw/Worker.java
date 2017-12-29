package ie.gmit.sw;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Worker implements Runnable 
{

	private static ArrayBlockingQueue<Job> inQueue;
	private static ConcurrentHashMap<Integer, Results> outQueue;
	private volatile static int workerNumber = 0;
	private int thisWorkerNumber;
	private Job job;
	private Results resutls;
	
	public Worker() 
	{
		inQueue = JobHandler.GetInQueue();
		outQueue = JobHandler.GetOutQueue();
		thisWorkerNumber = workerNumber;
		workerNumber++;
	}

	@Override
	public void run() 
	{
		try {
			job = inQueue.take();
			// Process document, make singles
			// Compare it to the rest of documents from db
			// add compare results into Result object
			// add Result into outQueue
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
