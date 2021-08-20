package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class userController {
	
	@Autowired
	private UserRepository userrepository;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String name = principal.getName();
		System.out.println(name);
		User userbyUserName = this.userrepository.getUserbyUserName(name);
		model.addAttribute("user", userbyUserName);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
	
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	//processing contact controller
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("pimage") MultipartFile img,
			Principal principal,
			HttpSession session)
	{
		
			
		try {
			String name=principal.getName();
			User user=this.userrepository.getUserbyUserName(name);
			if(img.isEmpty())
			{
				System.out.println("Empty file");
			}else
			{

				contact.setImage(img.getOriginalFilename());
				File file=new ClassPathResource("static/img").getFile();
				Path path=Paths.get(file.getAbsolutePath()+File.separator+img.getOriginalFilename());
				Files.copy(img.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("File uploaded successfully");
			}
			
			contact.setUser(user);
			user.getContact().add(contact);
			this.userrepository.save(user);
			session.setAttribute("message",new Message("Your contact is Successfully added","success") );
			
		} catch (Exception e) {
			e.printStackTrace();

			session.setAttribute("message",new Message("Something went wrong !!! Try again","danger") );
		}
		return "normal/add_contact_form";
		
	}
}
