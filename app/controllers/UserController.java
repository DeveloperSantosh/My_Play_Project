package controllers;

import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;
import java.util.List;

public class UserController  extends Controller {

    private final FormFactory formFactory;
    UserSQLController userSQLController;
    List<User> users;

    @Inject
    public UserController(FormFactory formFactory){
        this.formFactory = formFactory;
        userSQLController = new UserSQLController();

    }

    public Result index(){
//        Set<User> users = User.allUsers();
        users = userSQLController.retrieveUsers();
        return ok(views.html.user.index.render(users));
    }

    public Result createUser(){
        Form <User> userForm = formFactory.form(User.class);
        return ok(views.html.user.create.render(userForm));
    }

    public Result saveUser(Http.Request request){
        Form <User> userForm = formFactory.form(User.class).bindFromRequest(request);
        User user = userForm.get();
        userSQLController.insertUser(user);
        users = userSQLController.retrieveUsers();
        return redirect(routes.UserController.index());
    }

    public Result editUser(Integer id){
        User user = userSQLController.retrieveUserById(id);
        if(user == null){
            return notFound("Sorry! User not found");
        }
        Form <User> userForm = formFactory.form(User.class).fill(user);
        return ok(views.html.user.edit.render(userForm));
    }

    public Result updateUser(Http.Request request){
        Form <User> userForm = formFactory.form(User.class).bindFromRequest(request);
        User newUser = userForm.get();
        userSQLController.updateUser(newUser.getId(), newUser);
        return redirect(routes.UserController.index());
    }

    public Result showUser(Integer id){
        User user = userSQLController.retrieveUserById(id);
        if(user == null){
            return notFound("Sorry! User Not Found.");
        }
        return ok(views.html.user.show.render(user));
    }

    public Result deleteUser(Integer id){
        User user = userSQLController.retrieveUserById(id);
        if(user == null){
            return notFound("Sorry! user not found");
        }
        userSQLController.deleteUser(user);
        return redirect(routes.UserController.index());
    }
}
