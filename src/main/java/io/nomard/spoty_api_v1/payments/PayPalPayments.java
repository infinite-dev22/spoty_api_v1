package io.nomard.spoty_api_v1.payments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

public class PayPalPayments {
    private static @NotNull OutputStreamWriter getOutputStreamWriter(HttpURLConnection httpConn) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write("{ \"payment_source\": { \"paypal\": { \"name\": { \"given_name\": \"John\", \"surname\": \"Doe\" }, \"email_address\": \"customer@example.com\", \"experience_context\": { \"payment_method_preference\": \"IMMEDIATE_PAYMENT_REQUIRED\", \"brand_name\": \"EXAMPLE INC\", \"locale\": \"en-US\", \"landing_page\": \"LOGIN\", \"shipping_preference\": \"SET_PROVIDED_ADDRESS\", \"user_action\": \"PAY_NOW\", \"return_url\": \"https://example.com/returnUrl\", \"cancel_url\": \"https://example.com/cancelUrl\" } } } }");
        writer.flush();
        return writer;
    }

    private static @NotNull HttpURLConnection getHttpURLConnection(String urlPath, @Nullable String paypalRequestId) throws IOException {
        URL url = new URL(urlPath);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        httpConn.setRequestProperty("Content-Type", "application/json");
        if (Objects.nonNull(paypalRequestId)) {
            httpConn.setRequestProperty("PayPal-Request-Id", paypalRequestId);
        }
        httpConn.setRequestProperty("Authorization", "Bearer 6V7rbVwmlM1gFZKW_8QtzWXqpcwQ6T5vhEGYNJDAAdn3paCgRpdeMdVYmWzgbKSsECednupJ3Zx5Xd-g");

        httpConn.setDoOutput(true);
        return httpConn;
    }

    public void createOrder() throws IOException {
        HttpURLConnection httpConn = getHttpURLConnection("https://api-m.sandbox.paypal.com/v2/checkout/orders", "7b92603e-77ed-4896-8e78-5dea2050476a");
        OutputStreamWriter writer = getOutputStreamWriter(httpConn);
        writer.close();
        httpConn.getOutputStream().close();

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);
    }

    public void confirmOrder() throws IOException {
        HttpURLConnection httpConn = getHttpURLConnection("https://api-m.sandbox.paypal.com/v2/checkout/orders/5O190127TN364715T/confirm-payment-source", null);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write("{ \"payment_source\": { \"paypal\": { \"name\": { \"given_name\": \"John\", \"surname\": \"Doe\" }, \"email_address\": \"customer@example.com\", \"experience_context\": { \"payment_method_preference\": \"IMMEDIATE_PAYMENT_REQUIRED\", \"brand_name\": \"EXAMPLE INC\", \"locale\": \"en-US\", \"landing_page\": \"LOGIN\", \"shipping_preference\": \"SET_PROVIDED_ADDRESS\", \"user_action\": \"PAY_NOW\", \"return_url\": \"https://example.com/returnUrl\", \"cancel_url\": \"https://example.com/cancelUrl\" } } } }");
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);
    }

    public void authorizeOrderPayment() throws IOException {
        HttpURLConnection httpConn = getHttpURLConnection("https://api-m.sandbox.paypal.com/v2/checkout/orders/5O190127TN364715T/authorize", "7b92603e-77ed-4896-8e78-5dea2050476a");

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);
    }

    public void captureOrderPayment() throws IOException {
        HttpURLConnection httpConn = getHttpURLConnection("https://api-m.sandbox.paypal.com/v2/checkout/orders/5O190127TN364715T/capture", "7b92603e-77ed-4896-8e78-5dea2050476a");

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);
    }

    public void createFreeTrial(){}
}
