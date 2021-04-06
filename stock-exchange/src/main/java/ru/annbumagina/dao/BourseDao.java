package ru.annbumagina.dao;

public interface BourseDao {
    boolean addCompany(String name, int shares, double price);
    boolean changePrice(String name, double price);
    double getPrice(String name);
    int countShares(String name);
    boolean buyShares(String name, int cnt);
    boolean sellShares(String name, int cnt);
    boolean companyExists(String name);
}
