package service;

import com.google.inject.Inject;
import dto.RequestPermission;
import dto.RequestRole;
import dto.RequestUser;
import exception.UserNotFoundException;
import models.MyPermission;
import models.MyRole;
import models.MyUser;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import repository.*;
import java.util.List;
import java.util.Optional;

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
        if(requestUserForm.hasErrors())
            return badRequest("Error in form data.");
        RequestUser requestUser = requestUserForm.get();
        if (requestUser.getEmail().isBlank())
            return badRequest("Enter email");
        Optional<MyUser> user = Optional.ofNullable(userRepository.findUserByEmail(requestUser.getEmail()));
        MyUser myUser = user.orElseThrow(()-> new UserNotFoundException("User Not found with email: "+requestUser.getEmail()));
        if (BCrypt.checkpw(requestUser.getPassword(), myUser.getPassword())){
            return ok("Login Successfully\n"+myUser)
                    .addingToSession(request, "email", myUser.getEmail());
        }
        return notFound("Sorry Username and password not matched");
    }

//    Method to save user in database
    public Result saveUser(Http.Request request){
        Form<RequestUser> requestUserForm =  formFactory.form(RequestUser.class).bindFromRequest(request);
        if(requestUserForm.hasErrors())
            return badRequest("Error in form data.");
        RequestUser requestUser = requestUserForm.get();
        String result = requestUser.validate();
        if (!result.equals("valid") )
            return badRequest(result);
        if (userRepository.findUserByEmail(requestUser.getEmail()) != null)
            return badRequest("User Already exist with email: "+requestUser.getEmail());
        requestUser.setPermissions(PermissionRepository.getInstance().findAllPermissions());
        requestUser.addRole(RoleRepository.getInstance().findUserRoleByType("USER"));
        requestUser.setPassword(BCrypt.hashpw(requestUser.getPassword(), BCrypt.gensalt()));
        MyUser user = requestUser.getMyUser();
        if(userRepository.save(user))
            return ok("User created successfully\n " + userRepository.findUserByEmail(user.getEmail()));

        return internalServerError("Could Not create user");
    }

//    Method to delete user by its USER_ID'
    public Result deleteUser(Integer userId){
        Optional<MyUser> user = Optional.ofNullable(userRepository.findUserByID(userId));
        MyUser user1 = user.orElseThrow(()-> new UserNotFoundException("User not found with userId: "+userId));
        if(userRepository.delete(user1))
            return ok("User deleted Successfully\n"+user);
        return internalServerError("Something went wrong");
    }

//    Method to retrieve all users from database
    public Result getAllUsers(){
        List<MyUser> users = userRepository.findAllUsers();
        StringBuilder result = new StringBuilder();
        for(MyUser user: users)
            result.append((user)).append("\n");
        return ok(result.toString());
    }

    public Result logout(Http.Request request){
        if (request.session().get("email").isPresent()) {
            return ok("Logout successfully.").removingFromSession(request, "email").withNewSession();
        }
        else return badRequest("User Session not found");
    }

    public Result updateUser(Http.Request request, Integer userId) {
        Form<RequestUser> requestUserForm = formFactory.form(RequestUser.class).bindFromRequest(request);
        if (requestUserForm.hasErrors())
            return internalServerError("Error in form data.");
        RequestUser requestUser = requestUserForm.get();
        if (!requestUser.validate().equals("valid"))
            return badRequest(requestUser.validate());
        MyUser savedUser = userRepository.findUserByID(userId);
        if (savedUser == null ) return notFound();
        requestUser.setRoles(savedUser.getRoleList());
        requestUser.setPermissions(savedUser.getPermissionList());
        requestUser.setId(savedUser.getId());
        requestUser.setPassword((BCrypt.hashpw(requestUser.getPassword(), BCrypt.gensalt())));
        MyUser updatedUser = requestUser.getMyUser();
        if ( userRepository.updateUser(savedUser, updatedUser) )
            return ok("User Updated Successfully.\n"+updatedUser);
        return internalServerError("Could not update user");
    }

    public Result addRolesFor(Integer userId, Http.Request request) {
        MyUser oldUser = UserRepository.getInstance().findUserByID(userId);
        if (oldUser == null)    return notFound("User not found with id: "+userId);
        Form<RequestRole> roleForm = formFactory.form(RequestRole.class).bindFromRequest(request);
        if (roleForm.hasErrors())   return badRequest("Invalid form data");
        RequestRole requestRole = roleForm.get();
        if (!requestRole.validate().equals("valid")) return badRequest(requestRole.validate());
        MyRole role = requestRole.getMyRole();
        if (oldUser.getRoleList().contains(role))
            return badRequest("Role already provided to user with userId: "+userId);
        MyUser newUser = oldUser.toBuilder().addRole(role).build();
        if (UserRoleRepository.getInstance().save(newUser))
            return ok(role.getRoleType()+" role added successfully for userId: "+userId);
        return internalServerError("Something went wrong");
    }

    public Result addPermissionFor(Integer userId, Http.Request request) {
        MyUser savedUser = UserRepository.getInstance().findUserByID(userId);
        if (savedUser == null)
            return notFound("User not found with id: "+userId);
        Form<RequestPermission> permissionForm = formFactory.form(RequestPermission.class).bindFromRequest(request);
        if (permissionForm.hasErrors())
            return badRequest("Invalid form data");
        RequestPermission requestPermission = permissionForm.get();
        if (!requestPermission.validate().equals("valid"))
            return badRequest(requestPermission.validate());
        MyPermission permission = requestPermission.toMyPermission();
        MyPermission finalPermission = permission;
        boolean isPermissionPresent = savedUser.getPermissionList().stream()
                .anyMatch(myPermission-> myPermission.getValue().equals(finalPermission.getValue()));
        if (isPermissionPresent)
            return badRequest("Permission already exist for user with userId: "+userId);
        PermissionRepository.getInstance().save(permission);
        permission = PermissionRepository.getInstance().findPermissionByValue(permission.getValue());
        if (UserPermissionRepository.getInstance().save(permission, savedUser))
            return ok(permission.getValue()+" permission added successfully for userId: "+userId);
        return internalServerError("Something went wrong");
    }

    public Result removePermissionFor(Integer userId, Http.Request request) {
        MyUser savedUser = UserRepository.getInstance().findUserByID(userId);
        if (savedUser == null)
            return notFound("User not found with id: "+userId);
        Form<RequestPermission> permissionForm = formFactory.form(RequestPermission.class).bindFromRequest(request);
        if (permissionForm.hasErrors())
            return badRequest("Invalid form data");
        RequestPermission requestPermission = permissionForm.get();
        if (!requestPermission.validate().equals("valid"))
            return badRequest(requestPermission.validate());
        MyPermission permission = PermissionRepository.getInstance().findPermissionByValue(requestPermission.getValue());
        boolean isPermissionGranted = savedUser.getPermissionList().contains(permission);
        if (!isPermissionGranted)
            return notFound(permission.getValue()+" permission not found for userId: "+userId);
        if (UserPermissionRepository.getInstance().deleteUserPermission(savedUser, permission))
            return ok(permission.getValue()+ " permission revoked successfully for userId: "+userId);
        return internalServerError("Something went wrong");
    }

    public Result removeRoleFor(Integer userId, Http.Request request) {
        MyUser savedUser = UserRepository.getInstance().findUserByID(userId);
        if (savedUser == null)    return notFound("User not found with id: "+userId);
        Form<RequestRole> roleForm = formFactory.form(RequestRole.class).bindFromRequest(request);
        if (roleForm.hasErrors())   return Results.notAcceptable("Invalid form data");
        RequestRole requestRole = roleForm.get();
        if (!requestRole.validate().equals("valid")) return badRequest(requestRole.validate());
        MyRole role = requestRole.getMyRole();
        if (!savedUser.getRoleList().contains(role))
            return notFound(role.getRoleType()+ " role not found for userId: "+userId);
        if (UserRoleRepository.getInstance().deleteUserRole(savedUser, role))
            return ok(role.getRoleType()+" role removed successfully for userId: "+userId);
        return internalServerError("Something went wrong");
    }
}
