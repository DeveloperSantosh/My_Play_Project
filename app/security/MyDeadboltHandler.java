package security;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.models.Subject;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import repository.UserRepository;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MyDeadboltHandler extends AbstractDeadboltHandler {

    @Inject
    FormFactory formFactory;

    @Override
    public CompletionStage<Optional<Result>> beforeAuthCheck(Http.RequestHeader requestHeader, Optional<String> content) {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
//        Form<User> userForm = formFactory.form(User.class);
//        return CompletableFuture.completedFuture(Optional.of(ok(views.html.index.render(userForm))));
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Optional<? extends Subject>> getSubject(Http.RequestHeader requestHeader) {
        // in a real application, the user name would probably be in the session following a login process
        String sessionId = requestHeader.session().get("email").orElse("win.santosh205618@gmail.com");
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("From MYDEADOLTHANDLER");
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
        return CompletableFuture.completedFuture(ok(views.html.accessFailed.render()));
    }
}