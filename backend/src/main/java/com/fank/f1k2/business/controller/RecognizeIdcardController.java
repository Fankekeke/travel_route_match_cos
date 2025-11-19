package com.fank.f1k2.business.controller;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.core.http.HttpClient;
import com.aliyun.core.http.HttpMethod;
import com.aliyun.core.http.ProxyOptions;
import com.aliyun.httpcomponent.httpclient.ApacheAsyncHttpClientBuilder;
import com.aliyun.sdk.service.ocr_api20210707.models.*;
import com.aliyun.sdk.service.ocr_api20210707.*;
import com.google.gson.Gson;
import darabonba.core.RequestConfiguration;
import darabonba.core.client.ClientOverrideConfiguration;
import darabonba.core.utils.CommonUtil;
import darabonba.core.TeaPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class RecognizeIdcardController {

    public static void main(String[] args) throws Exception {


        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                .accessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"))
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/ocr-api
                                .setEndpointOverride("ocr-api.cn-hangzhou.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // Parameter settings for API request
        InputStream bodyStream = new FileInputStream(new File("<your-file-path>"));
        RecognizeIdcardRequest recognizeIdcardRequest = RecognizeIdcardRequest.builder()
                .outputFigure(true)
                .outputQualityInfo(true)
                .body(bodyStream)
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<RecognizeIdcardResponse> response = client.recognizeIdcard(recognizeIdcardRequest);
        // Synchronously get the return value of the API request
        RecognizeIdcardResponse resp = response.get();
        System.out.println(new Gson().toJson(resp));
        // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/

        // Finally, close the client
        client.close();
    }
}
