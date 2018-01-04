package ie.gmit.sw;

/**
 * Abstract class to be used by ThreadPoolService. Worker.class extends this as 
 * ThreadPoolService uses Prototype design pattern to save resources when re-populating the ThreadPool. 
 * 
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public abstract class HeavyWorker implements Cloneable, Runnable {

    @Override
    public void run() {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
	Object clone = null;
	try {
	    clone = super.clone();
	} catch (CloneNotSupportedException e) {
	    Util.logMessage("ERROR Cloning HeavyWorker caused Error:" + e.getMessage());
	}
	return clone;
    }
}
