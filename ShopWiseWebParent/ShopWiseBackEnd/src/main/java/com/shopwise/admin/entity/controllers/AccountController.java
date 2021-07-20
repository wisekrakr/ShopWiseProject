package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.services.UserService;
import com.shopwise.admin.processes.FileUpload;
import com.shopwise.admin.security.UserAccountDetails;
import com.shopwise.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private UserService service;

    /**
     * AuthenticationPrincipal will bind the details of the currently
     * authenticated principal (User) into a special Jwt object
     *
     * @return route to account details page
     */
    @GetMapping("/account")
    public String viewUserAccountDetails(@AuthenticationPrincipal UserAccountDetails currentUser, Model model) {
        String email = currentUser.getUsername();

        User user = service.getByEmail(email);

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", user.getFirstName() + "'s account");

        return "users/account_form";
    }

    /**
     * Update the user account details
     *
     * @param user               the user that will be updated
     * @param redirectAttributes to add a message to the element
     * @param multipartFile      to obtain the file object, we use @RequestParam. Html input element has name "avatar" attached.
     * @return route to user list page
     */
    @PostMapping("/account/update")
    public String saveUserAccountDetails(User user,
                                         RedirectAttributes redirectAttributes,
                                         @AuthenticationPrincipal UserAccountDetails currentUser,
                                         @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setAvatar(fileName);

            User userQuickSave = service.updateAccount(user);

            // path to user avatar directory
            String dir = "./user-avatars/" + userQuickSave.getId();

            // clean this user avatar directory
            FileUpload.cleanDir(dir);
            // save the new file/avatar to the user avatar directory
            FileUpload.saveFile(dir, fileName, multipartFile);
        } else {
            if (user.getAvatar().isEmpty()) user.setAvatar(null);

            service.updateAccount(user);
        }

        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setEnabled(user.isEnabled());
        currentUser.setPhoneNumber(user.getPhoneNumber());

        redirectAttributes.addFlashAttribute("message", "Your account has been updated successfully!");

        return "redirect:/account";
    }
}
