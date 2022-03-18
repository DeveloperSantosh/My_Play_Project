package security;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.models.Subject;
import play.mvc.Http;
import play.mvc.Result;
import repository.UserRepository;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BuggyDeadboltHandler extends AbstractDeadboltHandler {
    @Override
    public CompletionStage<Optional<Result>> beforeAuthCheck(Http.RequestHeader requestHeader, Optional<String> content) {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Optional<? extends Subject>> getSubject(Http.RequestHeader requestHeader) {
        // in a real application, the user name would probably be in the session following a login process
        String email = requestHeader.session().get("email").orElse("empty");
        return CompletableFuture.supplyAsync(() -> {
            if(email.equals("empty"))
                return Optional.empty();
            return Optional.ofNullable(UserRepository.getInstance().findUserByEmail(email));
        });
    }

    @Override
    public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(Http.RequestHeader requestHeader) {
        return CompletableFuture.supplyAsync(() -> Optional.of(new MyDynamicResourceHandler()));
    }

    @Override
    public CompletionStage<Result> onAuthFailure(Http.RequestHeader requestHeader, Optional<String> content) {
        throw new RuntimeException("An exception occurred in onAuthFailure");
    }

}
