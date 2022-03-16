package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.MyUser;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import repository.PermissionRepository;
import repository.RoleRepository;
import repository.UserRepository;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class UserController  extends Controller {

    UserRepository userRepository;
    BlogRepository blogRepository;
    HttpExecutionContext ec;

    @Inject
    public UserController(HttpExecutionContext ec){
        this.ec = ec;
        userRepository = UserRepository.getInstance();
        blogRepository = BlogRepository.getInstance();
    }

//    public Result index(){
//
//        Form<User> userForm = formFactory.form(User.class);
//        return ok(views.html.index.render(userForm));
//    }
//
//    public Result register() {
//        Form<User> form = formFactory.form(User.class);
//        return ok(views.html.user.create.render(form));
//    }

    public Result login(Http.Request request){
        String email = request.body().asJson().get("email").textValue();
        String password = request.body().asJson().get("password").textValue();

        MyUser myUser = null;
        try {
            myUser= userRepository.findUserByEmail(email);
            if(myUser != null && password.equals(myUser.getPassword()) ){
                request.session().adding("email", myUser.getEmail());
            }
            else return notFound("Sorry Username and password not matched");
        } catch (SQLException e) {
            e.printStackTrace();
            return notFound("Sorry user not found");
        }
        return ok("Login Successfully").addingToSession(request, "email",email);
    }

    public Result saveUser(Http.Request request) throws SQLException {
        String email = request.body().asJson().get("email").textValue();
        String password = request.body().asJson().get("password").textValue();
        String username = request.body().asJson().get("username").textValue();
        MyUser user = MyUser.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .setUsername(username)
                .addRole(RoleRepository.getInstance().findUserRoleByType("USER"))
                .addAllPermission(PermissionRepository.getInstance().findAllPermissions())
                .build();
        try{
            userRepository.save(user);
            MyUser savedUser = userRepository.findUserByEmail(email);
            return ok("User created successfully with id: " +
                    userRepository.findUserByEmail(email).getId());
        }catch (SQLException  e){
            e.printStackTrace();
            return internalServerError("Could Not create user");
        }
    }

    @Restrict(@Group("ADMIN"))
    public Result deleteUser(Integer userId) throws SQLException {
        MyUser user = userRepository.findUserByID(userId);
        if(user == null){
            return notFound("Sorry User with id: "+userId+" not found");
        }
        return ok("User deleted Successfully");
    }

    @Restrict(@Group("ADMIN"))
    public CompletionStage<Result> getAllUsers() throws SQLException {
        List<MyUser> users = userRepository.findAllUsers();
        StringBuilder result = new StringBuilder();
        for(MyUser user: users){
            result.append((user.toString())).append("\n");
        }
//        return ok(result.toString());
        return CompletableFuture.supplyAsync(()-> ok(result.toString()));
    }


}
