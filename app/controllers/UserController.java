package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import be.objectify.deadbolt.java.actions.Unrestricted;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.UserService;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@SubjectPresent
public class UserController  extends Controller {

    public final UserService userService;
    public static final int TIMEOUT = 120;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Unrestricted
    public CompletionStage<Result> login(Http.Request request) {
        return CompletableFuture
                .supplyAsync(()->userService.login(request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Unrestricted
    public CompletionStage<Result> saveUser(Http.Request request) {
        return CompletableFuture
                .supplyAsync(()->userService.saveUser(request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Restrict(@Group("ADMIN"))
    public CompletionStage<Result> deleteUser(Integer userId) {
        return CompletableFuture
                .supplyAsync(()->userService.deleteUser(userId))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Restrict(@Group("ADMIN"))
    public CompletionStage<Result> getAllUsers() {
        return CompletableFuture
                .supplyAsync(userService::getAllUsers)
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    public CompletionStage<Result> updateUser(Http.Request request, Integer userId) {
        return CompletableFuture
                .supplyAsync(()->userService.updateUser(request, userId))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    public Result logout(Http.Request request){
        return userService.logout(request);
    }

    @Restrict(@Group("ADMIN"))
    public CompletionStage<Result> addRole(Integer userId, Http.Request request){
        return CompletableFuture
                .supplyAsync(()->userService.addRolesFor(userId, request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Restrict(@Group("ADMIN"))
    public CompletionStage<Result> addPermission(Integer userId, Http.Request request){
        return CompletableFuture
                .supplyAsync(()->userService.addPermissionFor(userId, request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Restrict(@Group("ADMIN"))
    public CompletionStage<Result> revokePermission(Integer userId, Http.Request request){
        return CompletableFuture
                .supplyAsync(()->userService.removePermissionFor(userId, request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @Restrict(@Group("ADMIN"))
    public CompletionStage<Result> dismissRole(Integer userId, Http.Request request){
        return CompletableFuture
                .supplyAsync(()-> userService.removeRoleFor(userId, request))
                .orTimeout(TIMEOUT, TimeUnit.SECONDS);
    }
}
