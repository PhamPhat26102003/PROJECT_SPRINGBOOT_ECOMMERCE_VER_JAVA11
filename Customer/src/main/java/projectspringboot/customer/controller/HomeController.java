package projectspringboot.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import projectspringboot.library.model.Category;
import projectspringboot.library.model.Customer;
import projectspringboot.library.model.Laptop;
import projectspringboot.library.model.ShoppingCart;
import projectspringboot.library.repository.ILaptopRepository;
import projectspringboot.library.service.ICategoryService;
import projectspringboot.library.service.ICustomerService;
import projectspringboot.library.service.ILaptopService;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ILaptopService laptopService;
    @Autowired
    private ILaptopRepository laptopRepository;
    @Autowired
    private ICustomerService customerService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Principal principal, HttpSession session){
        if (principal != null){
            session.setAttribute("username", principal.getName());
            Customer customer = customerService.findByUsername(principal.getName());
            ShoppingCart shoppingCart = customer.getShoppingCart();
            session.setAttribute("totalItem", shoppingCart.getTotalItem());
        }else{
            session.removeAttribute("username");
        }
        return "login";
    }

    @GetMapping("/")
    public String homePage(@PageableDefault(size = 8)Pageable pageable, Model model){
        Page<Laptop> laptops = laptopRepository.findAll(pageable);
        model.addAttribute("laptops", laptops);
        model.addAttribute("title", "Laptop Gaming");
        return "index";
    }
}
