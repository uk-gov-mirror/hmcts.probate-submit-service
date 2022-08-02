package uk.gov.hmcts.probate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenExchangeResponseTest {
    TokenExchangeResponse tokenExchangeResponse;

    @BeforeEach
    public void setup() {
        tokenExchangeResponse = new TokenExchangeResponse("123");
    }

    @Test
    void testGetCode() {
        assertEquals("123", tokenExchangeResponse.getAccessToken());
    }
}