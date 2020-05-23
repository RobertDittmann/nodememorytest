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

@Slf4j
@Component
public class TestedService {

    @Autowired
    private GrpcTestedClient testedClient;

    public GrpcTestedService.Data getData(String param1, String param2) {
        return testedClient.getData(param1, param2);
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
            log.error(ex.getMessage());
        }

        return null;
    }

}
