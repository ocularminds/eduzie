/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.api;

import com.ocularminds.eduzie.Fault;
import com.ocularminds.eduzie.SearchPlace;
import com.ocularminds.eduzie.model.Place;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Festus Jejelowo
 */
@RestController
@RequestMapping(value = {"/api/move/location"})
public class Locations {

    @GetMapping(value = {"/{longitude}/{latitude}/{range}"})
    public ResponseEntity<List<String>> locate(
            @PathVariable(name = "longitude", required = true) Long longitude,
            @PathVariable(name = "latitude", required = true) Long latitude,
            @PathVariable(name = "range", required = true) Long range) {

        List<String> all = new ArrayList();
        all.add("Festus, 200");
        all.add("Tolu all");
        return ResponseEntity.ok(all);
    }

    @PostMapping()
    public ResponseEntity<Fault> indicate(@RequestBody Map<String, String> request) {
        Fault fault = new Fault("00", "Success");
        String userid = request.get("userid");
        String longitude = request.get("lon");
        String latitude = request.get("lat");

        System.out.println("updating user " + userid + " location longitude:" + longitude + ",latitude:" + latitude);
        String name = null;//"Oshodi Lagos Nigeria Lagos Lagos Nigeria Apapa Lagos Nigeria Ikorodu Lagos Nigeria Oworonsoki Ojota Ikeja Agege Ogba Somolu OjuElegba Berger Ojodu ";//"Oshodi";
        String type = null;//"neighborhood|political|routes|point_of_interest";
        String distance = "500";
        String s = SearchPlace.search(latitude, longitude, distance, type, name);
        List<Place> places = SearchPlace.parsePlaces(latitude, longitude, s);
        System.out.println("total Places found - " + places.size());

        for (int x = 0; x < places.size(); x++) {

            String d;
            String w;
            double r = places.get(x).getDistance();
            if (r == 0.00) {
                continue;
            }

            if (places.get(x).getDistance() < 1) {

                d = String.format("%.2fm", r * 1000);
                w = String.format("%2dmins walk", SearchPlace.nextArrival(r, SearchPlace.WALK_MODE));

            } else {

                double t = SearchPlace.nextArrival(r, SearchPlace.DRIVE_MODE);
                d = String.format("%.2fkm", r);
                if (t < 1) {
                    w = String.format("%.2fmins drive", t * 60);
                } else {
                    w = String.format("%.2fhrs drive", t);
                }
            }

            System.out.println("You are " + String.format("%.4f", r) + " " + d + " from " + places.get(x).getName() + " " + w);
        }
        return ResponseEntity.ok(fault);

    }
}
