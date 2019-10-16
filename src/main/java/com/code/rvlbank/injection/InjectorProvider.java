package com.code.rvlbank.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class InjectorProvider {
    private static Injector injector;

    static { injector = Guice.createInjector(new ServiceInjectorModule()); }

    public static Injector provide() {
        return injector;
    }
}
