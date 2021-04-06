package ru.annbumagina.dao;

import org.springframework.stereotype.Component;
import ru.annbumagina.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserInMemoryDao implements UserDao {
    private List<User> users = new ArrayList<>();

    @Override
    public int addUser(String name) {
        users.add(new User(name));
        return users.size() - 1;
    }

    @Override
    public boolean rechargeBalance(int id, double amount) {
        if (hasUser(id)) {
            User user = users.get(id);
            user.setMoney(user.getMoney() + amount);
            return true;
        }
        return false;
    }

    @Override
    public double getBalance(int id) {
        if (hasUser(id)) {
            return users.get(id).getMoney();
        }
        return 0;
    }

    @Override
    public boolean buyShares(int id, String company, int cnt, double price) {
        if (hasUser(id)) {
            User user = users.get(id);
            Map<String, Integer> shares = user.getShares();
            if (shares.containsKey(company)) {
                shares.put(company, shares.get(company) + cnt);
            } else {
                shares.put(company, cnt);
            }
            user.setMoney(user.getMoney() - cnt * price);
            return true;
        }
        return false;
    }

    @Override
    public boolean sellShares(int id, String company, int cnt, double price) {
        if (hasUser(id)) {
            User user = users.get(id);
            Map<String, Integer> shares = user.getShares();
            if (shares.containsKey(company)) {
                if (shares.get(company) < cnt) {
                    return false;
                } else {
                    shares.put(company, shares.get(company) - cnt);
                    user.setMoney(user.getMoney() + cnt * price);
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean hasUser(int id) {
        return id >= 0 && id < users.size();
    }

    @Override
    public Map<String, Integer> getShares(int id) {
        return users.get(id).getShares();
    }
}
