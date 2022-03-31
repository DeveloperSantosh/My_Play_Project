package security;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class MyHandlerCache implements HandlerCache {

    private final DeadboltHandler defaultHandler;
    private final Map<String, DeadboltHandler> handlers = new HashMap<>();

    public MyHandlerCache() {
        defaultHandler = new MyDeadboltHandler();
        handlers.put(HandlerKeys.DEFAULT.key, defaultHandler);
        handlers.put(HandlerKeys.ALT.key, new MyAlternativeDeadboltHandler());
        handlers.put(HandlerKeys.BUGGY.key, new BuggyDeadboltHandler());
        handlers.put(HandlerKeys.NO_USER.key, new NoUserDeadboltHandler());
    }

    @Override
    public DeadboltHandler apply(String s) {
        return handlers.get(s);
    }

    @Override
    public DeadboltHandler get() {
        return defaultHandler;
    }
}
