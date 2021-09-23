package ru.dexsys.submodule1;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyRateService {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String URL = "http://www.cbr.ru/scripts/XML_daily.asp";

    private final HttpClient httpClient;
    private final JAXBContext jaxbContext;

    public CurrencyRateService(HttpClient httpClient) {
        this.httpClient = httpClient;

        try {
            this.jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Couldn't initialize jaxb context", e);
        }
    }

    public BigDecimal getRate(String currencyCharCode) throws IOException {
        if (StringUtils.isBlank(currencyCharCode)) {
            throw new IllegalArgumentException("Invalid currency: '" + currencyCharCode + "'");
        }

        var request = new HttpGet(URL);
        var response = httpClient.execute(request);
        ValCurs valCurs;
        try (InputStream content = response.getEntity().getContent()) {
            try {
                valCurs = (ValCurs) jaxbContext.createUnmarshaller()
                        .unmarshal(new StreamSource(content));
            } catch (JAXBException e) {
                throw new IOException("Couldn't deserialize response", e);
            }
        }

        log.info("Got {} currency rates for date {}", valCurs.getValute().size(), valCurs.getDate());

        return valCurs.getValute().stream()
                .filter(valute -> currencyCharCode.equals(valute.getCharCode()))
                .map(valute -> new BigDecimal(valute.getValue().replace(',', '.'))
                        .divide(new BigDecimal(valute.getNominal()), RoundingMode.HALF_UP))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid currency: '" + currencyCharCode + "'"));
    }
}
