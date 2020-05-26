package com.rdittmann.nodememorytest.services;

import com.rdittmann.nodememorytest.grpc.tested.GrpcTestedClient;
import com.rdittmann.nodememorytest.grpc.tested.generated.GrpcTestedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class TestedService {

    public static AtomicInteger okRequests = new AtomicInteger(0);
    public static AtomicInteger failedRequests = new AtomicInteger(0);

    @Autowired
    private GrpcTestedClient testedClient;

    public GrpcTestedService.Data getData(String param1, String param2) {
        if (testedClient.getData(param1, param2).equals(GrpcTestedService.ResponseState.OK)) {
            okRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }
        return null;
    }


    public GrpcTestedService.Data restCall(String param1, String param2) {
        try {
            URL url = new URL("http://localhost:8210/v1/test/" + param1 + '/' + param2);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            if (status != 200) {
                log.error("returned status: " + status);
                failedRequests.incrementAndGet();
            } else {
                okRequests.incrementAndGet();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();


        } catch (Exception ex) {
            failedRequests.incrementAndGet();
        }

        return null;
    }

}
