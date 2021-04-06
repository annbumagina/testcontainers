package ru.annbumagina.model;

import lombok.Data;

@Data
public class Share {
    private final String company;
    private final int cnt;
    private final double price;
}
