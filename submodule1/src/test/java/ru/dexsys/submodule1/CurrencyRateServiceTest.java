package ru.dexsys.submodule1;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CurrencyRateServiceTest {
    @Test
    public void testGetRate() throws IOException {
        var httpClient = Mockito.mock(HttpClient.class);

        var response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        response.setHeader("Content-Type", "application/xml");
        response.setEntity(new StringEntity("""
                <ValCurs Date="23.09.2021" name="Foreign Currency Market">
                    <Valute ID="R01235">
                        <NumCode>840</NumCode>
                        <CharCode>USD</CharCode>
                        <Nominal>1</Nominal>
                        <Name>Доллар США</Name>
                        <Value>72,8806</Value>
                    </Valute>
                </ValCurs>"""));
        when(httpClient.execute(any())).thenReturn(response);


        var service = new CurrencyRateService(httpClient);

        assertEquals(new BigDecimal("72.8806"), service.getRate("USD"));
    }
}