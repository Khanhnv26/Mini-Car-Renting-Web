package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.CustomerDTO;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.service.AccountService;
import com.assignment.carrentingsystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final AccountService accountService;

    @GetMapping
    public String getAllCustomers(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "customerId") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        Page<Customer> customerPage = customerService.findPaginated(keyword, page, 5, sortBy, sortDir);
        model.addAttribute("customers", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalItems", customerPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        return "customer/customer-list";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customerDTO", new CustomerDTO());
        model.addAttribute("accounts", accountService.findAvailableCustomerAccounts(null));
        return "customer/customer-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("customerDTO") CustomerDTO customerDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        Long keepAccountId = customerDTO.getAccountId();
        if (bindingResult.hasErrors()) {
            model.addAttribute("accounts", accountService.findAvailableCustomerAccounts(keepAccountId));
            return "customer/customer-form";
        }

        try {
            customerService.save(customerDTO);
            redirectAttributes.addFlashAttribute("toastMessage", "Lưu khách hàng thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/admin/customers";
        } catch (Exception e) {
            model.addAttribute("accounts", accountService.findAvailableCustomerAccounts(keepAccountId));
            model.addAttribute("error", e.getMessage());
            return "customer/customer-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        CustomerDTO dto = customerService.findDTOById(id);
        model.addAttribute("customerDTO", dto);
        model.addAttribute("accounts", accountService.findAvailableCustomerAccounts(dto.getAccountId()));
        return "customer/customer-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteById(id);
            redirectAttributes.addFlashAttribute("toastMessage", "Xóa khách hàng thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/customers";
    }
}
