package com.ocare.health.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthDataRequest {
    private String recordkey;
    private Data data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String memo;
        private List<Entry> entries;
        private Source source;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        private Period period;
        private Distance distance;
        private Calories calories;
        private Object steps; // String 또는 Integer 또는 Double 가능

        public double getStepsAsDouble() {
            if (steps instanceof String) {
                return Double.parseDouble((String) steps);
            } else if (steps instanceof Integer) {
                return ((Integer) steps).doubleValue();
            }
            return (Double) steps;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Period {
        private String from;
        private String to;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Distance {
        private String unit;
        private double value;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Calories {
        private String unit;
        private double value;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {
        private Integer mode;
        private Product product;
        private String name;
        private String type;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        private String name;
        private String vender;
    }
}
