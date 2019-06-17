package testservice.impl;

import testservice.TestService;

public class TestServiceImpl implements TestService {
    @Override
    public String helloWorld(String name) {
        return name;
    }
}
