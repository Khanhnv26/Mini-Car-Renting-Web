package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.repository.CarRepository;
import com.assignment.carrentingsystem.service.CarService;
import com.assignment.carrentingsystem.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cars")
public class CarController {

    private final CarService carService;
    private final ProducerService producerService;

    @GetMapping
    public String listAll(Model model) {
        List<Car> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "car/car-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("producers", producerService.findAll());
        return "car/car-form";
    }

    @PostMapping("/save")
    public String saveForm(@ModelAttribute("car") Car car) {
        carService.save(car);
        return "redirect:/admin/cars";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("car", carService.findById(id));
        model.addAttribute("producers", producerService.findAll());
        return "car/car-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteForm(@PathVariable("id") Long id, Model model) {
        carService.deleteById(id);
        return "redirect:/admin/cars";
    }

}
