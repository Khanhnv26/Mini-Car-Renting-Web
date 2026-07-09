package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.CarDTO;
import com.assignment.carrentingsystem.service.CarService;
import com.assignment.carrentingsystem.service.ProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cars")
public class CarController {

    private final CarService carService;
    private final ProducerService producerService;

    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("cars", carService.findAll());
        return "car/car-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("carDTO", new CarDTO());
        model.addAttribute("producers", producerService.findAll());
        return "car/car-form";
    }

    @PostMapping("/save")
    public String saveForm(@Valid @ModelAttribute("carDTO") CarDTO car, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("producers", producerService.findAll());
            return "car/car-form";
        }
        carService.save(car);
        return "redirect:/admin/cars";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("carDTO", carService.findDTOById(id));
        model.addAttribute("producers", producerService.findAll());
        return "car/car-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteForm(@PathVariable("id") Long id, Model model) {
        carService.deleteById(id);
        return "redirect:/admin/cars";
    }

}
