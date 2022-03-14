package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.mysql.cj.xdevapi.SessionFactory;
import com.mysql.cj.xdevapi.SessionImpl;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.BlogRepository;
import repository.UserRepository;
import javax.inject.Inject;
import java.sql.SQLException;

public class UserController  extends Controller {

    private final FormFactory formFactory;
    UserRepository userRepository;
    BlogRepository blogRepository;

    @Inject
    public UserController(FormFactory formFactory){
        this.formFactory = formFactory;
        userRepository = UserRepository.getInstance();
        blogRepository = BlogRepository.getInstance();
    }

    public Result index(){
        Form<User> userForm = formFactory.form(User.class);
        return ok(views.html.index.render(userForm));
    }

    public Result register() {
        Form<User> form = formFactory.form(User.class);
        return ok(views.html.user.create.render(form));
    }

    public Result login(Http.Request request){
        Form <User> userForm = formFactory.form(User.class).bindFromRequest(request);
        User userRequest = userForm.get();
        User user = null;
        try {
            user = userRepository.findUserByEmail(userRequest.getEmail());
            if(user != null){
                request.session().adding("email", user.getEmail());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return notFound();
        }
        if(!user.getPassword().equals(userRequest.getPassword())){
            return redirect(routes.UserController.index());
        }
        return redirect(routes.BlogController.home(user.getId())).addingToSession(request,"email",user.getEmail());
    }

    public Result saveUser(Http.Request request) throws SQLException {
        Form <User> userForm = formFactory.form(User.class).bindFromRequest(request);
        User user = userForm.get();
        if (userRepository.save(user))
            redirect(routes.UserController.index());
        return ok();
    }

    @Restrict(@Group("ADMIN"))
    public Result deleteUser(Integer userId) throws SQLException {
        User user = userRepository.findUserByID(userId);
        if(user == null){
            return notFound("Sorry User with id: "+userId+" not found");
        }
        return ok("User deleted Successfully");
    }


}
