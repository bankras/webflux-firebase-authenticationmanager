package com.kretar.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SecurityConfig.class)
public class SimpleConfigurationTest {

    @Test
    public void doesItLoad() {

    }
}
