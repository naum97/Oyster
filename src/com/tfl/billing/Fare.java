package com.tfl.billing;

import java.math.BigDecimal;
import java.util.List;


public interface Fare {
    BigDecimal customerTotalCost(List<Journey> journeys);
}
