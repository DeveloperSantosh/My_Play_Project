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

public class UserController  extends Controller {

    public final UserService userService;

    @Inject
    public UserController(UserService userService){
        this.userService = userService;
    }

    @Unrestricted
    public Result login(Http.Request request){
        return userService.login(request);
    }

    @Unrestricted
    public Result saveUser(Http.Request request){
        return userService.saveUser(request);
    }

    @Restrict(@Group("ADMIN"))
    public Result deleteUser(Integer userId){
        return userService.deleteUser(userId);
    }

    @Restrict(@Group("ADMIN"))
    public Result getAllUsers(){
        return userService.getAllUsers();
    }

    @SubjectPresent
    public Result updateUser(Http.Request request, Integer userId) { return userService.updateUser(request, userId); }

    @SubjectPresent
    public Result logout(Http.Request request, Integer userId){ return userService.logout(request, userId); }

}
