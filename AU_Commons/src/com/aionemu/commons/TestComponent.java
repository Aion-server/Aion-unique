package com.aionemu.commons;

import com.aionemu.commons.services.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Author: SoulKeeper
 */
@Component
public class TestComponent {

    String descriptor = "./data/scripts/custom/test1/test1.xml";

    @Autowired
    private ScriptService scriptService;

    @PostConstruct
    public void doSomething(){
        System.out.println("Hello Shining World!!!");
        scriptService.load(descriptor);
    }

    @PreDestroy
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void doSomethingOnDestroy(){
        System.out.println("Bye-bye cruel world");
        scriptService.unload(descriptor);
    }
}
