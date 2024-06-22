package io.nomard.spoty_api_v1.payments;

import com.flutterwave.bean.*;
import com.flutterwave.services.*;
import com.flutterwave.utility.Environment;
import io.nomard.spoty_api_v1.models.payments.CardModel;
import io.nomard.spoty_api_v1.models.payments.MoMoModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class FlutterWavePayments {
    @Value("${flutterwave.secret-key}")
    private String raveSecretKey;
    @Value("${flutterwave.public-key}")
    private String ravePublicKey;
    @Value("${flutterwave.encryption-key}")
    private String raveEncryptionKey;

    public void initialize() {
        Environment.setSecretKey(raveSecretKey);
        Environment.setPublicKey(ravePublicKey);
        Environment.setEncryptionKey(raveEncryptionKey);
    }

    public Optional<Response> cardPayment(CardModel card) {
        var cardRequest = new CardRequest(card.getCard(),
                card.getCvv(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                "USD",
                new BigDecimal(card.getAmount()),
                card.getFullName(),
                card.getEmail(),
                card.getReference(),
                "https://www,flutterwave.ng",
                null);
        System.out.println("Card Request: " + cardRequest);

        return Optional.ofNullable(new CardCharge()
                .runTransaction(cardRequest)).map(response -> {
            switch (response.getMeta().getAuthorization().getMode()) {
                case PIN -> cardRequest.setAuthorization(new Authorization().pinAuthorization(card.getPin()));
                case AUS_NOAUTH -> cardRequest.setAuthorization(new Authorization().avsAuthorization(
                        card.getCity(),
                        card.getAddress(),
                        card.getState(),
                        card.getCountry(),
                        card.getZipcode()
                ));
                case REDIRECT -> System.out.println("Redirect user...");
            }
            System.out.println("Response: " + response);

            Response authorizeResponse = new CardCharge().runTransaction(cardRequest);
            System.out.println("Authorize Response: " + authorizeResponse);

            //validate
            ValidateCharge validateCharge = validateTransaction(authorizeResponse.getData().getFlw_ref());
            System.out.println("Validate Charge: " + validateCharge);

            //verify
            Response verifyTransaction = verifyTransaction(authorizeResponse.getData().getId());
            System.out.println("Verify Transaction: " + verifyTransaction);

            return verifyTransaction;
        });
    }

    public Response preAuth(CardModel card) {
        var preAuthorization = new PreAuthorization();
        return preAuthorization.runTransaction(new PreAuthorizationRequest(card.getCard(),
                card.getCvv(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                new BigDecimal(card.getAmount()),
                card.getFullName(),
                card.getReference(),
                "USD",
                "UG",
                card.getEmail(),
                "https://www.flutterwave.com/ng/",
                Optional.empty()));
    }

    public Response momoPay(String reference, MoMoModel cardModel) {
        return new MobileMoney()
                .runUgandaMobileMoneyTransaction(new UgandaMobileMoneyRequestRequest(reference/*"MC-158523s09v5050e8"*/,
                        new BigDecimal(cardModel.getAmount()),
                        "UGX",
                        "143256743",
                        "MTN",
                        cardModel.getEmail() /*"stefan.wexler@hotmail.eu"*/,
                        cardModel.getPhoneNumber() /*"054709929220"*/,
                        cardModel.getFullName() /*"Yolande Aglaé Colbert"*/,
                        cardModel.getClientIp() /*"154.123.220.1"*/,
                        cardModel.getDeviceFingerPrint() /*"62wd23423rq324323qew1"*/,
                        Optional.empty()
                ));
    }

    // For recurring flutterWavePayments.
    public Response tokenizeCard(CardModel card, String token) {
        return new TokenizedCharge()
                .runTransaction(new TokenizedChargeRequest(token/*"flw-t1nf-f9b3bf384cd30d6fca42b6df9d27bd2f-m03k"*/,
                        "UG",
                        "USD",
                        new BigDecimal(card.getAmount()),
                        card.getEmail(),
                        card.getFirstName(),
                        card.getLastName(),
                        card.getClientIp()/*"123.876.0997.9"*/,
                        "Tokenize recurrent payment",
                        card.getReference()/*"tokenized-c-001"*/));
    }

    public Response verifyTransaction(int id) {
        return new Transactions()
                .runVerifyTransaction(id);
    }

    public ValidateCharge validateTransaction(String flwRef) {
        var validate = new ValidateCharge();
        validate.setFlw_ref(flwRef);
//        validate.setOtp(flwRef);
        return validate;
    }

    public Response refreshCardToken(String token, String holderEmail, String holderName, String phoneNumber) {
        return new TokenizedCharge()
                .runUpdateToken(token,
                        new UpdateTokenRequest(holderEmail,
                                holderName,
                                phoneNumber));
    }

    public Response getTransactionFee(String transactionId) {
        return new Transactions()
                .runGetTransactionsFees(new BigDecimal(transactionId),
                        "USD");
    }
}
