package org.protege.owlapi.util;

import java.util.concurrent.Callable;

public class SaveResultsRunnable<X> implements Runnable {
    private Callable<X> call;
    private X result;
    private Exception exception;
    
    public SaveResultsRunnable(Callable<X> call) {
        this.call = call;
    }
    
    public X getResult() {
        return result;
    }
    
    public Exception getException() {
        return exception;
    }

    public void run() {
        try {
            result = call.call();
        }
        catch (Exception e) {
            exception = e;
        }
    }

}
