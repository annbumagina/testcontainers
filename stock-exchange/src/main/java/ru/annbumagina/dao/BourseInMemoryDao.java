package ru.annbumagina.dao;

import org.springframework.stereotype.Component;
import ru.annbumagina.model.Company;

import java.util.HashMap;
import java.util.Map;

@Component
public class BourseInMemoryDao implements BourseDao {
    private Map<String, Company> companies = new HashMap<>();

    public BourseInMemoryDao() {}

    @Override
    public boolean addCompany(String name, int shares, double price) {
        if (!companyExists(name)) {
            companies.put(name, new Company(name, shares, price));
            return true;
        }
        return false;
    }

    @Override
    public boolean changePrice(String name, double price) {
        if (companyExists(name)) {
            Company company = companies.get(name);
            company.setPrice(price);
            return true;
        }
        return false;
    }

    @Override
    public double getPrice(String name) {
        return companies.get(name).getPrice();
    }

    @Override
    public int countShares(String name) {
        return companies.get(name).getCnt();
    }

    @Override
    public boolean buyShares(String name, int cnt) {
        if (companyExists(name)) {
            Company company = companies.get(name);
            if (company.getCnt() < cnt)
                return false;
            company.setCnt(company.getCnt() - cnt);
            return true;
        }
        return false;
    }

    @Override
    public boolean sellShares(String name, int cnt) {
        if (companyExists(name)) {
            Company company = companies.get(name);
            company.setCnt(company.getCnt() + cnt);
            return true;
        }
        return false;
    }

    @Override
    public boolean companyExists(String name) {
        return companies.containsKey(name);
    }
}
