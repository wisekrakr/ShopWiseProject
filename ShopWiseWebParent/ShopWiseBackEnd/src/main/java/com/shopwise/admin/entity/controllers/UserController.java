package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.services.UserService;
import com.shopwise.admin.processes.FileUpload;
import com.shopwise.admin.processes.export.UserCsvExporter;
import com.shopwise.admin.processes.export.UserExcelExporter;
import com.shopwise.admin.processes.export.UserPdfExporter;
import com.shopwise.admin.utils.UserNotFoundException;
import com.shopwise.common.entity.Role;
import com.shopwise.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User Controller capable of handling the HTTP requests and
 * establish the routing table to know which methods serve which endpoints.
 */

@Controller
public class UserController {

    @Autowired
    private UserService service;

    /**
     * This getter route will show the first set of users, sorted by last name, ascending.
     * @param model container that contains the data of the application, accessible by view page
     * @return page with list of users.
     */
    @GetMapping("/users")
    public String getFirstPageUsers(Model model) {
        return getUsersPerPage(1,  "lastName","asc",null, model);
    }


    @GetMapping("/users/page/{pageNumber}")
    public String getUsersPerPage(@PathVariable(name = "pageNumber") int pageNumber,
                                  @Param("sortField") String sortField,
                                  @Param("sortDirection") String sortDirection,
                                  @Param("keyword") String keyword,
                                  Model model) {

        Page<User> page = service.getPerPage(pageNumber, sortField, sortDirection,keyword);
        List<User> usersList = page.getContent();

        long startCount = (long) (pageNumber - 1) * UserService.USERS_PER_PAGE + 1;
        long endCount = startCount + UserService.USERS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalCount", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("sortDirectionReversed", sortDirection.equals("asc") ? "desc" : "asc");

        model.addAttribute("keyword", keyword);

        model.addAttribute("users", usersList);
        model.addAttribute("pageTitle", "Users");

        return "users/users";
    }

    /**
     * Get route towards creating a new user.
     * Model will add user, list of roles, page title, and a boolean to see if we are updating a existing user.
     * @param model container that contains the data of the application, accessible by view page
     * @return route to page with form to create a new user with.
     */
    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> rolesList = service.getRoles();

        model.addAttribute("user", new User());
        model.addAttribute("rolesList", rolesList);
        model.addAttribute("pageTitle", "Create a new User");
        model.addAttribute("editMode", false);

        return "users/user_form";
    }

    /**
     * Add a new user to the database
     *
     * @param user               the new user that will be saved
     * @param redirectAttributes to add a message to the element
     * @param multipartFile      to obtain the file object, we use @RequestParam. Html input element has name "avatar" attached.
     * @return route to user list page
     */
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setAvatar(fileName);

            User userQuickSave = service.save(user);

            // path to user avatar directory
            String dir = "./user-avatars/" + userQuickSave.getId();

            // clean this user avatar directory
            FileUpload.cleanDir(dir);
            // save the new file/avatar to the user avatar directory
            FileUpload.saveFile(dir, fileName, multipartFile);
        } else {
            if (user.getAvatar().isEmpty()) user.setAvatar(null);

            service.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully!");

        return getRedirectURLofSavedUser(user);
    }

    private String getRedirectURLofSavedUser(User user) {
        String userEmailNamePart = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDirection=asc&keyword=" + userEmailNamePart;
    }

    /**
     * Get route towards editing an existing user.
     * @param id user id
     * @param model container that contains the data of the application, accessible by view page
     * @param redirectAttributes add messages with
     * @return route to page with form to edit user. Form has user info in it.
     */
    @GetMapping("/users/edit/{id}")
    public String updateUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Role> rolesList = service.getRoles();

            User user = service.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("rolesList", rolesList);
            model.addAttribute("pageTitle", "Update your user profile " + user.getFirstName());
            model.addAttribute("editMode", true);

            return "users/user_form";

        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/users";
        }
    }

    /**
     * Get route towards deleting a user.
     * @param id user id
     * @param redirectAttributes to add messages to view with.
     * @return route to user list page.
     */
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        String fullName = getUserFullName(id, redirectAttributes);
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "User : " + fullName + " successfully removed."
            );
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";

    }

    /**
     * Get route to toggle enabled status for user with.
     * @param id user id
     * @param enabled boolean
     * @param redirectAttributes to add messages to the view
     * @return route to user list page.
     */
    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserStatus(@PathVariable(name = "id") Integer id,
                                   @PathVariable(name = "status") boolean enabled,
                                   RedirectAttributes redirectAttributes) {

        service.updateEnabledStatus(id, enabled);

        String fullName = getUserFullName(id, redirectAttributes);
        String message = enabled ? fullName + " is enabled" : fullName + " is disabled";

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportCSV(HttpServletResponse response){
        UserCsvExporter exporter = new UserCsvExporter();

        try {
            exporter.export(service.getAll(),response);
        }catch (Throwable t){
            throw new IllegalStateException("Could not export users CSV file",t);
        }
    }

    @GetMapping("/users/export/pdf")
    public void exportPDF(HttpServletResponse response){
        UserPdfExporter exporter = new UserPdfExporter();

        try {
            exporter.export(service.getAll(),response);
        }catch (Throwable t){
            throw new IllegalStateException("Could not export users CSV file",t);
        }
    }

    @GetMapping("/users/export/excel")
    public void exportExcel(HttpServletResponse response){
        UserExcelExporter exporter = new UserExcelExporter();

        try {
            exporter.export(service.getAll(),response);
        }catch (Throwable t){
            throw new IllegalStateException("Could not export users Excel" +
                    " file",t);
        }
    }

    /**
     * Quickly get user first and last name, for aesthetic purposes.
     * @param id user id
     * @param redirectAttributes to add messages to view with. Error messages.
     * @return user full name.
     */
    private String getUserFullName(Integer id, RedirectAttributes redirectAttributes) {
        String fullName = null;
        try {
            User user = service.getById(id);

            fullName = user.getFullName();

        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return fullName;
    }
}
