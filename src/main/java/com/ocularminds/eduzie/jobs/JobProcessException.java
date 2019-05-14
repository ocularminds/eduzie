
package com.ocularminds.eduzie.jobs;

/**
 *
 * @author Festus Jejelowo
 */
public class JobProcessException extends Exception {

    public JobProcessException(String message) {
        super(message);
    }

    public JobProcessException(String error, Exception ex) {
        super(error, ex);
    }
}
