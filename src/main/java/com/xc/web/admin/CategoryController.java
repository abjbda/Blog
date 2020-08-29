package com.xc.web.admin;

import com.xc.po.Category;
import com.xc.service.interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    private final String CATEGORIES_PAGE = "admin/categories";
    private final String REDIRECT_PAGE = "redirect:/admin/categories";
    private final String INPUT_PAGE = "admin/categories-input";

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String categories(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model) {
        model.addAttribute("page", categoryService.listCategory(pageable));
        return CATEGORIES_PAGE;
    }

    @GetMapping("/categories/input")
    public String input(Model model) {
        model.addAttribute("category", new Category());
        return INPUT_PAGE;
    }

    @GetMapping("/categories/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategory(id));
        return INPUT_PAGE;
    }


    @PostMapping("/categories")
    public String post(@Valid Category category, BindingResult result, RedirectAttributes attributes) {
        Category c = categoryService.getCategoryByName(category.getName());
        if (c != null) {
            result.rejectValue("name","nameError","The category is already existed");
        }
        if (result.hasErrors()) {
            return INPUT_PAGE;
        }

        c = categoryService.saveCategory(category);
        if (c == null ) {
            attributes.addFlashAttribute("message", "Failed to add");
        } else {
            attributes.addFlashAttribute("message", "The category is added");
        }
        return REDIRECT_PAGE;
    }


    @PostMapping("/categories/{id}")
    public String editPost(@Valid Category category, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        Category c = categoryService.getCategoryByName(category.getName());
        if (c != null) {
            return REDIRECT_PAGE;
        }

        if (result.hasErrors()) {
            return INPUT_PAGE;
        }

        c = categoryService.updateCategory(id, category);
        if (c == null ) {
            attributes.addFlashAttribute("message", "Failed to change");
        } else {
            attributes.addFlashAttribute("message", "Change is done");  // 成功
        }
        return REDIRECT_PAGE;
    }

    @GetMapping("/categories/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes) {
        categoryService.deleteCategory(id);
        attributes.addFlashAttribute("message", "That category is gone");
        return REDIRECT_PAGE;
    }


}
