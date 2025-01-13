package io.github.orionlibs.orion_websocket_tools;

import java.security.Principal;

public class TestPrincipal implements Principal
{
    private final String name;


    public TestPrincipal(String name)
    {
        this.name = name;
    }


    @Override
    public String getName()
    {
        return this.name;
    }
}
