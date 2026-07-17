package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.CarDTO;
import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.service.CarService;
import com.assignment.carrentingsystem.service.ProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cars")
public class CarController {

    private final CarService carService;
    private final ProducerService producerService;

    @GetMapping
    public String listAll(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "producerId", required = false) Long producerId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "carId") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
       Page<Car> carPage =
            carService.findPaginated(name, producerId, status, minPrice, maxPrice, page, 5, sortBy, sortDir);
        model.addAttribute("cars", carPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("totalItems", carPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("producers", producerService.findAll());
        model.addAttribute("name", name);
        model.addAttribute("selectedProducerId", producerId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "car/car-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("carDTO", new CarDTO());
        model.addAttribute("producers", producerService.findAll());
        return "car/car-form";
    }

    @PostMapping("/save")
    public String saveForm(@Valid @ModelAttribute("carDTO") CarDTO car, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("producers", producerService.findAll());
            return "car/car-form";
        }
        try {
            carService.save(car);
            redirectAttributes.addFlashAttribute("toastMessage", "Lưu xe thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/admin/cars";
        } catch (Exception e) {
            model.addAttribute("producers", producerService.findAll());
            model.addAttribute("error", e.getMessage());
            return "car/car-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("carDTO", carService.findDTOById(id));
        model.addAttribute("producers", producerService.findAll());
        return "car/car-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteForm(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            carService.deleteById(id);
            Car after = carService.findById(id);
            if (after != null && "Inactive".equals(after.getStatus())) {
                redirectAttributes.addFlashAttribute("toastMessage", "Xe đã có lịch sử thuê — chuyển trạng thái Inactive (không xóa cứng).");
            } else {
                redirectAttributes.addFlashAttribute("toastMessage", "Xóa xe thành công!");
            }
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/cars";
    }

}
