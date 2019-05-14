/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
