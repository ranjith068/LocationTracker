package com.locationtracker.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajesh on 30/4/17.
 */

public class DurationModel {
    public String status;

    public List<String> destination_addresses = new ArrayList<>();

    public List<String> origin_addresses = new ArrayList<>();

    public List<Rows> rows = new ArrayList<>();

}
