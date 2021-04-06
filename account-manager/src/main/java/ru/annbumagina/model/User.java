package ru.annbumagina.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private final String name;
    private double money = 0;
    private Map<String, Integer> shares = new HashMap<>();
}
