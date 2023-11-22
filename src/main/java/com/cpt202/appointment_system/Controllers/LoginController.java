package com.cpt202.appointment_system.Controllers;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import com.cpt202.appointment_system.Common.Result;
import com.cpt202.appointment_system.Models.User;
import com.cpt202.appointment_system.Repositories.UserRepo;
import com.cpt202.appointment_system.Services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cpt202.appointment_system.Repositories.UserRepo;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cpt202.appointment_system.Services.EmailService;

@Controller
@RequestMapping("/appointment-system")

public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserRepo userreposity;
    // @Autowired
    // private AuthenticationManager authenticationManager;
    
    // @GetMapping("")
    // public String showForm() {
    //     return "signup";
    // }

    //登录登录登录
    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            Model model,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if(loginService.loginUser(username, password)==0){
            // 添加用户信息到 session
            User user= userreposity.findByUsername(username);
            Integer Role=user.getType();
            session.setAttribute("user", username);
            session.setAttribute("role", 0);

            redirectAttributes.addFlashAttribute("message", "Login Success");
            return "redirect:/appointment-system";
        } else if(loginService.loginUser(username, password)==1){
            session.setAttribute("user", username);
            session.setAttribute("role", 1);

            redirectAttributes.addFlashAttribute("message", "Administrator Login Success");
            // return "redirect:/maintain/maintainUser";
            return "redirect:/appointment-system";
        }
        
        else {
            redirectAttributes.addFlashAttribute("error", "Login Failed");
            return "redirect:/appointment-system";
        }
    }

    // 注册注册注册
    @PostMapping("/reg")
    public String registerUser(@RequestParam("rename") String username,
                               @RequestParam("reemail") String email,
                               @RequestParam("repass") String password,
                               @RequestParam("repass2") String password2,
                               @RequestParam("phone") String phone,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        // User user = new User(username, password,);
        
        User user=new User(null, username, password, 0, null, "/assets/images/default-user.png", null, phone, email, 0);

        if(loginService.registerUser(user)==1){
            redirectAttributes.addFlashAttribute("error", "Registration failed: Username has been registered or format does not match");
            return "redirect:/appointment-system";
        }
        
        else if(loginService.registerUser(user)==2){
            redirectAttributes.addFlashAttribute("error", "Registration failed: Mailbox has been registered");
            return "redirect:/appointment-system";
        }
        
        else{

            session.setAttribute("user", username);
            session.setAttribute("role", 0);
            redirectAttributes.addFlashAttribute("message", "Successful registration: You are automatically logged in");
            
            return "redirect:/appointment-system";

        }
    }

    //登出
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 从会话中移除用户信息
        session.removeAttribute("user");
        session.removeAttribute("role");

        // 重定向到登录页面
        return "redirect:/appointment-system";
    }

//前端检查用户名和email的api接口

    @GetMapping("/check-unique")
    public ResponseEntity<?> checkUnique(@RequestParam("value") String value) {
        boolean isUnique = loginService.checkUnique(value);

        if (isUnique) {
            return ResponseEntity.ok().body("unique");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate");
        }
    }
    @GetMapping("/check-uniqueem")
    public ResponseEntity<?> checkUniqueem(@RequestParam("value") String value) {
        boolean isUnique = loginService.checkUniqueEmail(value);

        if (isUnique) {
            return ResponseEntity.ok().body("unique");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate");
        }
    }

    //找回密码
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepo userRepository;

    private int generateVerificationCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min + 1) + min;
    }

    // 其他已有的代码

    private Map<String, Integer> verificationCodes = new HashMap<>();

    @GetMapping("/reset-password-form")
    public String showResetPasswordForm() {
        return "reset-password-form";
    }

    @PostMapping("/sendForgotPasswordCode")
    public String resetPassword(@RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User name does not exist. Please check if the input is correct.");
            return "redirect:/reset-password-form";
        }

        String email = user.getEmail();
        int verificationCode = generateVerificationCode();
        verificationCodes.put(username, verificationCode);

        String subject = "Reset password verification code";
        String text = "Dear user " + username + ", your password reset verification code is: " + verificationCode + ". Please keep it in a safe place.";

        emailService.sendSimpleMessage(email, subject, text);
        redirectAttributes.addFlashAttribute("success", "The verification code has been sent to your email, please pay attention to check it.");
        return "redirect:/reset-password-form";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("username") String username,
                                 @RequestParam("verificationCode") int verificationCode,
                                 @RequestParam("newPassword") String newPassword,
                                 RedirectAttributes redirectAttributes) {
        Integer correctVerificationCode = verificationCodes.get(username);

        if (correctVerificationCode == null || correctVerificationCode != verificationCode) {
            redirectAttributes.addFlashAttribute("error", "The verification code is wrong, please check the input or resend the verification code.");
            return "redirect:/reset-password-form";
        }

        User user = userRepository.findByUsername(username);
        user.setPassword(newPassword);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Password reset successfully, please use the new password to login.");
        return "redirect:/appointment-system";
    }

}



