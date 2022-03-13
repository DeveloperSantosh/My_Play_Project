package security;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.models.Subject;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import java.util.Optional;
import views.html.accessFailed;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NoUserDeadboltHandler extends AbstractDeadboltHandler {
    @Override
    public CompletionStage<Optional<Result>> beforeAuthCheck(Http.RequestHeader requestHeader, Optional<String> content) {
        // if the API calls for an Optional, don't return a null!
        // THIS IS A PURPOSEFUL ERROR - DO NOT REPEAT IN YOUR CODE!
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Optional<? extends Subject>> getSubject(Http.RequestHeader requestHeader) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Result> onAuthFailure(Http.RequestHeader requestHeader, Optional<String> content) {
        return CompletableFuture.supplyAsync(Results::badRequest);
    }

    @Override
    public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(Http.RequestHeader requestHeader) {
        return CompletableFuture.supplyAsync(() -> Optional.of(new MyAlternativeDynamicResourceHandler()));
    }

}
