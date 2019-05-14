/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.jobs;

/**
 *
 * @author Director
 */
public interface Job {

    /**
     *
     * @throws JobProcessException
     */
    void process() throws JobProcessException;
}
