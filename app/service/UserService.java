package service;

import models.MyUser;
import models.RequestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.sql.SQLException;
import java.util.List;

import static play.mvc.Results.*;

@Service
public class UserService {

    public BlogRepository blogRepository;
    public UserRepository userRepository;
    public FormFactory formFactory;
    private final Logger logger;

    @Inject
    public UserService(FormFactory formFactory) {
        blogRepository = BlogRepository.getInstance();
        userRepository = UserRepository.getInstance();
        this.formFactory = formFactory;
        logger = LoggerFactory.getLogger(UserService.class);
    }

//    Method to validate credentials and login
    public Result login(Http.Request request){
        Form<RequestUser> requestUserForm =  formFactory.form(RequestUser.class).bindFromRequest(request);
        if(requestUserForm.hasErrors()){ return badRequest("Error in form data."); }
        RequestUser requestUser = requestUserForm.get();

        try {
            MyUser myUser = userRepository.findUserByEmail(requestUser.getEmail());
            if(myUser != null && requestUser.getPassword().equals(myUser.getPassword()) ){
                return ok("Login Successfully").addingToSession(request, "email",myUser.getEmail());
            }
            return notFound("Sorry Username and password not matched");
        } catch (SQLException e) {
            logger.debug(e.getMessage());
            return internalServerError("Something went wrong");
        }
    }

//    Method to save user in database
    public Result saveUser(Http.Request request){
        Form<RequestUser> requestUserForm =  formFactory.form(RequestUser.class).bindFromRequest(request);
        if(requestUserForm.hasErrors()){ return badRequest("Error in form data."); }
        RequestUser requestUser = requestUserForm.get();

        try{
            MyUser user = MyUser.newBuilder()
                    .setEmail(requestUser.getEmail())
                    .setPassword(requestUser.getPassword())
                    .setUsername(requestUser.getUsername())
                    .addRole(RoleRepository.getInstance().findUserRoleByType("USER"))
                    .addAllPermission(PermissionRepository.getInstance().findAllPermissions())
                    .build();
            if(userRepository.save(user))
                return ok("User created successfully with id: " +
                    userRepository.findUserByEmail(user.getEmail()).getId());
        }catch (SQLException  e){
            e.printStackTrace();
        }
        return internalServerError("Could Not create user");
    }

//    Method to delete user by its USER_ID'
    public Result deleteUser(Integer userId){
        try {
            MyUser user = userRepository.findUserByID(userId);
            if(user == null)
                return notFound("Sorry User with id: "+userId+" not found");
            if(userRepository.delete(user))
                return ok("User deleted Successfully");
        }catch (SQLException e){
            logger.debug(e.getMessage());
        }
        return internalServerError("Something went wrong");
    }

//    Method to retrieve all users from database
    public Result getAllUsers(){
        try {
            List<MyUser> users = userRepository.findAllUsers();
            StringBuilder result = new StringBuilder();
            for(MyUser user: users)
                result.append((user.toString())).append("\n");
            return ok(result.toString());
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        return internalServerError("Something went wrong");
    }
}
