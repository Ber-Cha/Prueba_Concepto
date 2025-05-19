package com.kafka.provider.springbootprovider.service_impl;

import java.util.UUID;

public class ApiKeyGenerator {
    public static String generateApiKey() {
        return UUID.randomUUID().toString();
    }

}
