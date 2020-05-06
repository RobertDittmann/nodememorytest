package com.rdittmann.nodememorytest.services;

import com.rdittmann.nodememorytest.grpc.tested.GrpcTestedClient;
import com.rdittmann.nodememorytest.grpc.tested.generated.GrpcTestedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestedService {

    @Autowired
    private GrpcTestedClient testedClient;

    public GrpcTestedService.Data getData(String param1, String param2) {
        return testedClient.getData(param1, param2);
    }

}
