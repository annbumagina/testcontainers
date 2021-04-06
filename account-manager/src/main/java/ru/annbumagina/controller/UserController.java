package ru.annbumagina.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Controller;
import ru.annbumagina.dao.UserDao;
import java.util.Map;


@Controller
public class UserController {
    private final String URL = "http://localhost:1123";
    private UserDao userDao;
    private RestTemplate restTemplate;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
        restTemplate = new RestTemplate();
    }

    @RequestMapping(value = "/register")
    public ResponseEntity register(@RequestParam String name) {
        return ResponseEntity.ok(userDao.addUser(name));
    }

    @RequestMapping(value = "/recharge")
    public ResponseEntity recharge(@RequestParam int id, @RequestParam double amount) {
        if (userDao.rechargeBalance(id, amount)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/buy-shares")
    public ResponseEntity buyShares(@RequestParam int id,
                                   @RequestParam String company,
                                   @RequestParam int cnt) {
        String request = String.format(URL + "/get-price?name=%s", company);
        ResponseEntity<Double> response1 = restTemplate.getForEntity(request, Double.class);
        double price = response1.getBody();
        if (userDao.getBalance(id) < cnt * price) {
            return ResponseEntity.badRequest().build();
        }

        request = String.format(URL + "/buy-shares?name=%s&cnt=%d", company, cnt);
        ResponseEntity<String> response2 = restTemplate.getForEntity(request, String.class);
        if (response2.getStatusCode().equals(HttpStatus.OK)
            && userDao.buyShares(id, company, cnt, price)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/sell-shares")
    public ResponseEntity sellShares(@RequestParam int id,
                                   @RequestParam String company,
                                   @RequestParam int cnt) {
        String request = String.format(URL + "/get-price?name=%s", company);
        ResponseEntity<Double> response1 = restTemplate.getForEntity(request, Double.class);
        double price = response1.getBody();
        if (!userDao.sellShares(id, company, cnt, price)) {
            return ResponseEntity.badRequest().build();
        }

        request = String.format(URL + "/sell-shares?name=%s&cnt=%d", company, cnt);
        ResponseEntity<String> response2 = restTemplate.getForEntity(request, String.class);
        if (response2.getStatusCode().equals(HttpStatus.OK)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/get-shares")
    public ResponseEntity getShares(@RequestParam int id) {
        if (!userDao.hasUser(id)) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Integer> shares = userDao.getShares(id);
        JSONArray ja = new JSONArray();
        for (Map.Entry<String, Integer> share: shares.entrySet()) {
            String request = String.format(URL + "/get-price?name=%s", share.getKey());
            ResponseEntity<Double> response1 = restTemplate.getForEntity(request, Double.class);
            double price = response1.getBody();

            JSONObject jo = new JSONObject();
            jo.put("company", share.getKey());
            jo.put("cnt", share.getValue());
            jo.put("price", price);
            ja.put(jo);
        }
        return ResponseEntity.ok(ja.toString());
    }

    @RequestMapping(value = "/get-all-money")
    public ResponseEntity getAllMoney(@RequestParam int id) {
        if (!userDao.hasUser(id)) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Integer> shares = userDao.getShares(id);
        double money = userDao.getBalance(id);
        for (Map.Entry<String, Integer> share: shares.entrySet()) {
            String request = String.format(URL + "/get-price?name=%s", share.getKey());
            ResponseEntity<Double> response1 = restTemplate.getForEntity(request, Double.class);
            double price = response1.getBody();
            money += price * share.getValue();
        }
        return ResponseEntity.ok(money);
    }
}
