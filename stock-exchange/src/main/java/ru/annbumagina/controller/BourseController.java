package ru.annbumagina.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.annbumagina.dao.BourseDao;

@Controller
public class BourseController {
    private BourseDao bourseDao;

    public BourseController(BourseDao bourseDao) {
        this.bourseDao = bourseDao;
    }

    @RequestMapping(value = "/add-company")
    public ResponseEntity addCompany(@RequestParam String name,
                                     @RequestParam int shares,
                                     @RequestParam double price) {
        if (bourseDao.addCompany(name, shares, price))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/change-price")
    public ResponseEntity changePrice(@RequestParam String name,
                                      @RequestParam double price) {
        if (bourseDao.changePrice(name, price))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/get-price")
    public ResponseEntity getPrice(@RequestParam String name) {
        if (bourseDao.companyExists(name))
            return ResponseEntity.ok(bourseDao.getPrice(name));
        else
            return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/count-shares")
    public ResponseEntity countShares(@RequestParam String name) {
        if (bourseDao.companyExists(name))
            return ResponseEntity.ok(bourseDao.countShares(name));
        else
            return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/buy-shares")
    public ResponseEntity buyShares(@RequestParam String name, @RequestParam int cnt) {
        if (bourseDao.buyShares(name, cnt))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/sell-shares")
    public ResponseEntity sellShares(@RequestParam String name, @RequestParam int cnt) {
        if (bourseDao.sellShares(name, cnt))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}
