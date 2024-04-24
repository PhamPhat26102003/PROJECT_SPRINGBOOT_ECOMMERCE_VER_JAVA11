package projectspringboot.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projectspringboot.library.dto.AdminDto;
import projectspringboot.library.model.Admin;
import projectspringboot.library.service.IAdminService;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class LoginController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private IAdminService adminService;

   @GetMapping("/login")
    public String loginPage(Model model){
       model.addAttribute("title", "Login page");
       return "login";
   }

   @RequestMapping("/index")
   public String indexPage(Model model, Principal principal){
       model.addAttribute("title", "Admin page");
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
           return "redirect:/login";
       }
       return "index";
   }

   @GetMapping("/register")
    public String registerPage(Model model){
       model.addAttribute("title", "Register page");
       model.addAttribute("adminDto", new AdminDto());
       return "register";
   }

   @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model){
       model.addAttribute("title", "Forgot password page");
       return "forgot-password";
   }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes){
        try{
            if(result.hasErrors()){
                model.addAttribute("adminDto", adminDto);
                result.toString();
                return "register";
            }
            String username = adminDto.getUsername();
            Admin admin = adminService.findByUsername(username);
            if(admin != null){
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("emailError", "Your email has been registered!");
                return "register";
            }
            if(adminDto.getPassword().equals(adminDto.getRepeatPassword())){
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminService.save(adminDto);
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("success", "Register successfully!!!");
            }else{
                model.addAttribute("adminDto", adminDto);
                redirectAttributes.addFlashAttribute("message", "Password is not the same!!!!");
                model.addAttribute("passwordError", "Password is not the same!!!!");
                return "register";
            }
        }catch(Exception e){
            model.addAttribute("serverError", "Something wrong here, can not register!!!");
            System.out.println("Error " + e.getMessage());
        }
        return "register";
    }
}
