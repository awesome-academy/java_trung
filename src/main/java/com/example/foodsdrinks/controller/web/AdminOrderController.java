package com.example.foodsdrinks.controller.web;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.OrderFilterRequest;
import com.example.foodsdrinks.dto.request.UpdateOrderStatusRequest;
import com.example.foodsdrinks.entity.Order;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import com.example.foodsdrinks.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final MessageHelper messageHelper;

    @GetMapping
    public String list(
            @ModelAttribute("filter") OrderFilterRequest filter,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        Page<Order> orders = orderService.getOrders(filter, pageable);
        model.addAttribute("orders", orders);
        model.addAttribute("statusOptions", OrderStatus.values());
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderDetailForAdmin(id);
        model.addAttribute("order", order);
        return "admin/orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @ModelAttribute UpdateOrderStatusRequest request,
            RedirectAttributes redirectAttributes) {
        orderService.adminUpdateStatus(id, request.getStatus());
        redirectAttributes.addFlashAttribute("successMessage",
                messageHelper.get("success.admin.order.status.updated"));
        return "redirect:/admin/orders/" + id;
    }
}
