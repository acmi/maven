package ru.dexsys.app;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.dexsys.submodule1.CurrencyRateService;

import java.lang.invoke.MethodHandles;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            var service = new CurrencyRateService(httpClient);
            var usdRate = service.getRate("USD");
            log.info("USD rate = {}", usdRate);
        }
    }
}
