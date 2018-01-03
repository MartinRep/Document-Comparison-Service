package ie.gmit.sw;

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
