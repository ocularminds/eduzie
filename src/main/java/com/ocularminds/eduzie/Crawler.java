/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie;

import java.util.List;

/**
 *
 * @author Director
 */
public interface Crawler {

    /**
     * This is the only method exposed as the API call loads multiple
     * configuration files from specified URI an invokes a background
     * {@link SearchAgent} callable task which in turn does the action property
     * collection from various streams. This is the method that is exposed as
     * API call.
     *
     * @throws java.lang.InterruptedException
     * @params resources String containing space separated URI where data will
     * be extracted from.
     * @params attribute String containing space separated keywords to look for.
     * @return List class containing the loaded SearchObjectCache.
     */
    List<SearchObjectCache> crawl() throws InterruptedException;
}
