package service;

import models.MyUser;
import models.RequestUser;
import org.springframework.stereotype.Service;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import repository.PermissionRepository;
import repository.RoleRepository;
import repository.UserRepository;
import javax.inject.Inject;
import java.util.List;
import static play.mvc.Results.*;

@Service
public class UserService {

    public BlogRepository blogRepository;
    public UserRepository userRepository;
    public FormFactory formFactory;

    @Inject
    public UserService(FormFactory formFactory) {
        blogRepository = BlogRepository.getInstance();
        userRepository = UserRepository.getInstance();
        this.formFactory = formFactory;
    }

//    Method to validate credentials and login
    public Result login(Http.Request request){
        Form<RequestUser> requestUserForm =  formFactory.form(RequestUser.class).bindFromRequest(request);
        if(requestUserForm.hasErrors()){ return badRequest("Error in form data."); }
        RequestUser requestUser = requestUserForm.get();

        MyUser myUser = userRepository.findUserByEmail(requestUser.getEmail());
        if(myUser != null && requestUser.getPassword().equals(myUser.getPassword()) ){
            return ok("Login Successfully\n"+myUser).addingToSession(request, "email",myUser.getEmail());
        }
        return notFound("Sorry Username and password not matched");
    }

//    Method to save user in database
    public Result saveUser(Http.Request request){
        Form<RequestUser> requestUserForm =  formFactory.form(RequestUser.class).bindFromRequest(request);
        if(requestUserForm.hasErrors()) return badRequest("Error in form data.");
        RequestUser requestUser = requestUserForm.get();
        MyUser user = MyUser.newBuilder()
                .setEmail(requestUser.getEmail())
                .setPassword(requestUser.getPassword())
                .setUsername(requestUser.getUsername())
                .addRole(RoleRepository.getInstance().findUserRoleByType("USER"))
                .addAllPermission(PermissionRepository.getInstance().findAllPermissions())
                .build();
        if(userRepository.save(user))
            return ok("User created successfully\n " +
                userRepository.findUserByEmail(user.getEmail()));
        return internalServerError("Could Not create user");
    }

//    Method to delete user by its USER_ID'
    public Result deleteUser(Integer userId){
        MyUser user = userRepository.findUserByID(userId);
        if(user == null)
            return notFound("Sorry User with id: "+userId+" not found");
        if(userRepository.delete(user))
            return ok("User deleted Successfully\n"+user);
        return internalServerError("Something went wrong");
    }

//    Method to retrieve all users from database
    public Result getAllUsers(){
        List<MyUser> users = userRepository.findAllUsers();
        StringBuilder result = new StringBuilder();
        for(MyUser user: users)
            result.append((user.toString())).append("\n");
        return ok(result.toString());
    }

    public Result logout(Http.Request request, Integer userId){
        MyUser user = userRepository.findUserByID(userId);
        if (user!=null) {
            if (request.session().get("email").isPresent()) {
                return ok("Logout successfully.").removingFromSession(request, "email");
            }
            else return badRequest("User Session not found");
        }
        else return notFound("Could not find user");
    }
}
