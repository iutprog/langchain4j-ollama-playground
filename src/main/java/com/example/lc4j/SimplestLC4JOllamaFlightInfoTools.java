package com.example.lc4j;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Same tools as the course's FlightInfoTools. Changes:
 *   - logger uses FlightInfoTools.class (the original pointed at a different class, FlightInfo)
 *   - @P describes the parameter: small local models need it more than GPT-4o does
 * The @Tool text, method name and parameter description are ALL the model sees.
 * It never sees the method body.
 */

public class SimplestLC4JOllamaFlightInfoTools {
    private static final Logger logger = LoggerFactory.getLogger(SimplestLC4JOllamaFlightInfo.class);

    @Tool("Retrieves the flight status of a given flight")
    public String getFlightStatus(String flightId) {
        logger.info("Get flight status of flight {}", flightId);
        return flightId + " status: IN THE AIR. 2 hours from destination";
    }

    @Tool("Returns the type of aircraft")
    public String getAircraftType(String flightId) {
        logger.info("Get aircraft type of flight {}", flightId);
        return flightId + " type: Boeing 747";
    }

    @Tool("Returns the cost of a flight")
    public String getFlightCost(String flightId) {
        logger.info("Returns the cost of a flight {}", flightId);
        return "1234.00 USD";
    }
}
