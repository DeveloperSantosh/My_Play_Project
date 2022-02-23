package controllers;

import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Set;

public class UserController  extends Controller {

    private final FormFactory formFactory;
    private final HttpExecutionContext ec;

    @Inject
    public UserController(FormFactory formFactory, HttpExecutionContext ec){
        this.formFactory = formFactory;
        this.ec = ec;
    }

    public Result index(){
        Set<User> users = User.allUsers();
        return ok(views.html.user.index.render(users));
    }

    public Result createUser(){
        Form <User> userForm = formFactory.form(User.class);
        return ok(views.html.user.create.render(userForm));
    }

    public Result saveUser(Http.Request request){
        Form <User> userForm = formFactory.form(User.class).bindFromRequest(request);
        User user = userForm.get();
        User.add(user);
        return redirect(routes.UserController.index());
    }

    public Result editUser(Integer id){
        User user = User.findById(id);
        if(user == null){
            return notFound("Sorry! User not found");
        }
        Form <User> userForm = formFactory.form(User.class).fill(user);

        return ok(views.html.user.edit.render(userForm));
    }

    public Result updateUser(Http.Request request){
        Form <User> userForm = formFactory.form(User.class).bindFromRequest(request);
        User user = userForm.get();
        User oldUser = User.findById(user.id);
        if(oldUser == null){
            return notFound("Sorry! User Not Found");
        }
        oldUser.name = user.name;
        oldUser.surname = user.surname;
        return redirect(routes.UserController.index());
    }

    public Result showUser(Integer id){
        User user = User.findById(id);
        if(user == null){
            return notFound("Sorry! User Not Found.");
        }
        return ok(views.html.user.show.render(user));
    }

    public Result deleteUser(Integer id){
        User user = User.findById(id);
        if(user == null){
            return notFound("Sorry! user not found");
        }
        User.remove(user);
        return redirect(routes.UserController.index());
    }
}
