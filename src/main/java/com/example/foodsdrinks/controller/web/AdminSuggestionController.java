package com.example.foodsdrinks.controller.web;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.RejectSuggestionRequest;
import com.example.foodsdrinks.entity.Suggestion;
import com.example.foodsdrinks.entity.enums.SuggestionStatus;
import com.example.foodsdrinks.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/suggestions")
@RequiredArgsConstructor
public class AdminSuggestionController {

    private final SuggestionService suggestionService;
    private final MessageHelper messageHelper;

    @GetMapping
    public String list(
            @RequestParam(required = false) SuggestionStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        Page<Suggestion> suggestions = suggestionService.getSuggestions(status, pageable);
        model.addAttribute("suggestions", suggestions);
        model.addAttribute("statusOptions", SuggestionStatus.values());
        model.addAttribute("selectedStatus", status);
        return "admin/suggestions/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Suggestion suggestion = suggestionService.getSuggestionDetail(id);
        model.addAttribute("suggestion", suggestion);
        model.addAttribute("rejectRequest", new RejectSuggestionRequest());
        return "admin/suggestions/detail";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        suggestionService.approve(id);
        redirectAttributes.addFlashAttribute("successMessage",
                messageHelper.get("success.admin.suggestion.approved"));
        return "redirect:/admin/suggestions/" + id;
    }

    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable Long id,
            @Valid @ModelAttribute("rejectRequest") RejectSuggestionRequest rejectRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Suggestion suggestion = suggestionService.getSuggestionDetail(id);
            model.addAttribute("suggestion", suggestion);
            return "admin/suggestions/detail";
        }
        suggestionService.reject(id, rejectRequest.getAdminNote());
        redirectAttributes.addFlashAttribute("successMessage",
                messageHelper.get("success.admin.suggestion.rejected"));
        return "redirect:/admin/suggestions/" + id;
    }
}
