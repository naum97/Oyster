package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.List;
import java.util.UUID;

/**
 * Created by User on 11/30/2017.
 */
public interface Database {

    List<Customer> getCustomers();

    boolean isRegisteredId(UUID cardID);
}
