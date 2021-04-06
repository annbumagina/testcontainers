package ru.annbumagina;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AppTest {
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private MockMvc mvc;

    @ClassRule
    public static GenericContainer simpleWebServer
            = new FixedHostPortGenericContainer("stock-exchange:1.0-SNAPSHOT")
            .withFixedExposedPort(1123, 8080)
            .withExposedPorts(8080);

    private void addCompany(String company, int shares, double price) {
        String request = String.format(Locale.US, "http://localhost:1123/add-company?name=%s&shares=%d&price=%f", company, shares, price);
        restTemplate.getForEntity(request, String.class);
    }

    private void changePrice(String company, double price) {
        String request = String.format(Locale.US, "http://localhost:1123/change-price?name=%s&price=%f", company, price);
        restTemplate.getForEntity(request, String.class);
    }

    @Test
    public void testBuyOneShare() throws Exception {
        addCompany("google", 200, 15.4);
        MvcResult result = mvc.perform(get("/register?name=Ann"))
            .andExpect(status().isOk())
            .andReturn();
        String id = result.getResponse().getContentAsString();
        mvc.perform(get("/recharge?id=" + id + "&amount=100"))
            .andExpect(status().isOk());
        mvc.perform(get("/buy-shares?id=" + id + "&company=google&cnt=5"))
            .andExpect(status().isOk());

        mvc.perform(get("/get-shares?id=" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].company", is("google")))
            .andExpect(jsonPath("$[0].cnt", is(5)))
            .andExpect(jsonPath("$[0].price", is(15.4)));
    }

    @Test
    public void testMoneyAfterShareRise() throws Exception {
        addCompany("disney", 100, 11.8);
        MvcResult result = mvc.perform(get("/register?name=Sam")).andReturn();
        String id = result.getResponse().getContentAsString();
        mvc.perform(get("/recharge?id=" + id + "&amount=100"));
        mvc.perform(get("/buy-shares?id=" + id + "&company=disney&cnt=5"));
        changePrice("disney", 12.3);

        String money = mvc.perform(get("/get-all-money?id=" + id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(102.5, Double.parseDouble(money), 1e-9);
    }

    @Test
    public void testBuyAndSell() throws Exception {
        addCompany("finn", 50, 1.19);
        addCompany("jake", 50, 1.25);
        MvcResult result = mvc.perform(get("/register?name=Marceline")).andReturn();
        String id = result.getResponse().getContentAsString();
        mvc.perform(get("/recharge?id=" + id + "&amount=100"));
        mvc.perform(get("/buy-shares?id=" + id + "&company=finn&cnt=10"));
        mvc.perform(get("/buy-shares?id=" + id + "&company=jake&cnt=10"));
        mvc.perform(get("/sell-shares?id=" + id + "&company=finn&cnt=5"))
            .andExpect(status().isOk());
        mvc.perform(get("/sell-shares?id=" + id + "&company=jake&cnt=5"))
            .andExpect(status().isOk());

        mvc.perform(get("/get-shares?id=" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].company", containsInAnyOrder("finn", "jake")));

        String money = mvc.perform(get("/get-all-money?id=" + id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(100.0, Double.parseDouble(money), 1e-9);
    }
}
