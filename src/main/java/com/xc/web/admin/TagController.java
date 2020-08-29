package com.xc.web.admin;

import com.xc.po.Tag;
import com.xc.service.interfaces.TagService;
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
public class TagController {

    private final String TAGS_PAGE = "admin/tags";
    private final String REDIRECT_PAGE = "redirect:/admin/tags";
    private final String INPUT_PAGE = "admin/tags-input";

    @Autowired
    private TagService tagService;

    @GetMapping("/tags")
    public String tags(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model) {
        model.addAttribute("page",tagService.listTag(pageable));
        return TAGS_PAGE;
    }

    @GetMapping("/tags/input")
    public String input(Model model) {
        model.addAttribute("tag", new Tag());
        return INPUT_PAGE;
    }

    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("tag", tagService.getTag(id));
        return INPUT_PAGE;
    }

    @PostMapping("/tags")
    public String post(@Valid Tag tag, BindingResult result, RedirectAttributes attributes) {
        Tag t = tagService.getTagByName(tag.getName());
        if (t != null) {
            result.rejectValue("name","nameError","The tag is already existed");
        }

        if (result.hasErrors()) {
            return INPUT_PAGE;
        }

        t = tagService.saveTag(tag);
        if (t == null ) {
            attributes.addFlashAttribute("message", "Failed to add");
        } else {
            attributes.addFlashAttribute("message", "The tag is added");
        }
        return REDIRECT_PAGE;
    }

    @PostMapping("/tags/{id}")
    public String editPost(@Valid Tag tag, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        Tag t = tagService.getTagByName(tag.getName());
        if (t != null) {
            return REDIRECT_PAGE;
        }
        if (result.hasErrors()) {
            return INPUT_PAGE;
        }
        t = tagService.updateTag(id,tag);
        if (t == null ) {
            attributes.addFlashAttribute("message", "Failed to change");
        } else {
            attributes.addFlashAttribute("message", "Change is done");
        }
        return REDIRECT_PAGE;
    }

    @GetMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message", "That tag is gone");
        return REDIRECT_PAGE;
    }


}
