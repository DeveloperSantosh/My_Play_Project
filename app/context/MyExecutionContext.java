package context;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import play.libs.concurrent.CustomExecutionContext;

public class MyExecutionContext extends CustomExecutionContext {

    @Inject
    public MyExecutionContext(ActorSystem actorSystem) {
        // uses a custom thread pool defined in application.conf
        super(actorSystem, "my-dispatcher");
    }
}
