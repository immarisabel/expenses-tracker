package nl.marisabel.backend.security;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.security.Principal;
import java.util.logging.Logger;

@Controller
public class SecurityController {



 @GetMapping("/login")
 public String login() {
  return "login";
 }


 /**
  * simple Error page.
  */
 @RequestMapping("/403")
 public String forbidden() {
  return "403";
 }

 @ExceptionHandler({Exception.class})
 @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 public String handleException(Exception ex, Model model) {

  //logger.error("Exception during execution of SpringSecurity application", ex);
  String errorMessage = (ex != null ? ex.getMessage() : "Unknown error");

  model.addAttribute("errorMessage", errorMessage);
  return "error";
 }
}