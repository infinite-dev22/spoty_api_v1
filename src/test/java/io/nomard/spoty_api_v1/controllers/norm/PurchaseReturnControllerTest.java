package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import org.junit.jupiter.api.Test;

class PurchaseReturnControllerTest {
    @Test
    public void testDeserialization() throws Exception {
        String jsonPayload = """
                  {
                  "date": "2024-11-17",
                  "supplier": { "id": 1 },
                  "branch": { "id": 1 },
                  "tenant": { "id": 1 },
                  "purchaseReturnDetails": [
                    {
                      "quantity": 10,
                      "product": { "id": 1 }
                    },
                    {
                      "quantity": 20,
                      "product": { "id": 2 }
                    }
                  ],
                  "tax": { "id": 1 },
                  "discount": { "id": 1 },
                  "shippingFee": 10.0,
                  "amountPaid": 100.0,
                  "total": 3000.0,
                  "subTotal": 2900.0,
                  "amountDue": 2800.0,
                  "purchaseStatus": "Completed",
                  "paymentStatus": "Paid",
                  "notes": "Test purchase return"
                }
                """;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        PurchaseReturnMaster master = objectMapper.readValue(jsonPayload, PurchaseReturnMaster.class);

        System.out.println(master);
    }

}