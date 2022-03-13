package security;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import play.mvc.Http;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MyAlternativeDynamicResourceHandler implements DynamicResourceHandler {
    @Override
    public CompletionStage<Boolean> isAllowed(String name, Optional<String> meta, DeadboltHandler deadboltHandler, Http.RequestHeader requestHeader) {
        // look something up in an LDAP directory, etc, and the answer isn't good for the user
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletionStage<Boolean> checkPermission(String permissionValue, Optional<String> meta, DeadboltHandler deadboltHandler, Http.RequestHeader requestHeader) {
        // Computer says no
        return CompletableFuture.completedFuture(false);
    }

}
