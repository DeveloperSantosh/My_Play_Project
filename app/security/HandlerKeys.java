package security;

public enum HandlerKeys {

    DEFAULT("default"),
    ALT("altHandler"),
    BUGGY("buggyHandler"),
    NO_USER("noUserHandler");

    public final String key;

    private HandlerKeys(final String key)
    {
        this.key = key;
    }
}
