package ru.annbumagina.dao;

import java.util.Map;

public interface UserDao {
    int addUser(String name);
    boolean rechargeBalance(int id, double amount);
    double getBalance(int id);
    boolean buyShares(int id, String company, int cnt, double price);
    boolean sellShares(int id, String company, int cnt, double price);
    boolean hasUser(int id);
    Map<String, Integer> getShares(int id);
}
