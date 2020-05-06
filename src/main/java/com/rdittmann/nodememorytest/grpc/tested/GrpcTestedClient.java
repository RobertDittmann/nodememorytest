package com.rdittmann.nodememorytest.grpc.tested;

import com.rdittmann.nodememorytest.grpc.tested.generated.GrpcTestedService;
import com.rdittmann.nodememorytest.grpc.tested.generated.TestedServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class GrpcTestedClient {

    @Getter
    private String serviceName = "TestedService";

    private TestedServiceGrpc.TestedServiceBlockingStub service;

    @PostConstruct
    private void init() {
        service = TestedServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 6968).usePlaintext().build());
    }

    public GrpcTestedService.Data getData(String param1, String param2) {
        final GrpcTestedService.DataResponse response = service.getData(GrpcTestedService.DataRequest.newBuilder().setParameter1(param1).setParameter2(param2).build());
        if (!response.getState().equals(GrpcTestedService.ResponseState.OK)) {
            log.error("returned status: " + response.getState().name());
        }
        return null; // not needed
    }

}
