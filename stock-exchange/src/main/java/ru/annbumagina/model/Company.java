package ru.annbumagina.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Company {
    private String name;
    private int cnt;
    private double price;
}
