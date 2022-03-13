package security;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.models.Subject;
import play.mvc.Http;
import play.mvc.Result;
import repository.UserRepository;
import views.html.accessFailed;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MyAlternativeDeadboltHandler extends AbstractDeadboltHandler {

    @Override
    public CompletionStage<Optional<Result>> beforeAuthCheck(Http.RequestHeader requestHeader, Optional<String> content) {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Optional<? extends Subject>> getSubject(Http.RequestHeader requestHeader) {
        // in a real application, the user name would probably be in the session following a login process
        String sessionId = requestHeader.session().get("sessionId").get();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.ofNullable(UserRepository.getInstance().findUserByEmail(sessionId));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletionStage<Result> onAuthFailure(Http.RequestHeader requestHeader, Optional<String> content) {
        // you can return any result from here - forbidden, etc
        return CompletableFuture.supplyAsync(() -> ok(accessFailed.render()));
    }

    @Override
    public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(Http.RequestHeader requestHeader) {
        return CompletableFuture.supplyAsync(() -> Optional.of(new MyAlternativeDynamicResourceHandler()));
    }
}
