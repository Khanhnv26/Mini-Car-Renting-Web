package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.CustomerDTO;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.service.AccountService;
import com.assignment.carrentingsystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final AccountService accountService;

    @GetMapping
    public String getAllCustomers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customer/customer-list";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customerDTO", new CustomerDTO());
        model.addAttribute("accounts", accountService.findAllCustomers());
        return "customer/customer-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("customerDTO") CustomerDTO customerDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("accounts", accountService.findAllCustomers());
            return "customer/customer-form";
        }

        try {
            customerService.save(customerDTO);
            return "redirect:/admin/customers";
        } catch (Exception e) {
            model.addAttribute("accounts", accountService.findAllCustomers());
            model.addAttribute("error", e.getMessage());
            return "customer/customer-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("customerDTO", customerService.findDTOById(id));
        model.addAttribute("accounts", accountService.findAllCustomers());
        return "customer/customer-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model) {
        try {
            customerService.deleteById(id);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accounts", accountService.findAllCustomers());
            return "customer/customer-form";
        }
        return "redirect:/admin/customers";
    }
}
