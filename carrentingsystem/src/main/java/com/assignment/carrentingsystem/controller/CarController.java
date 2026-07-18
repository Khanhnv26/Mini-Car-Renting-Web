package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.CarDTO;
import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.service.CarService;
import com.assignment.carrentingsystem.service.ProducerService;
import com.assignment.carrentingsystem.util.PriceRangeRules;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cars")
public class CarController {

    private final CarService carService;
    private final ProducerService producerService;

    private void addFormModel(Model model) {
        model.addAttribute("producers", producerService.findAll());
        model.addAttribute("today", LocalDate.now());
    }

    @GetMapping
    public String listAll(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "producerId", required = false) Integer producerId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "carName") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        int pageSize = 5;
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("producers", producerService.findAll());
        model.addAttribute("name", name);
        model.addAttribute("selectedProducerId", producerId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        String filterError = PriceRangeRules.validateOptionalRange(minPrice, maxPrice);
        if (filterError != null) {
            model.addAttribute("toastMessage", filterError);
            model.addAttribute("toastType", "error");
            model.addAttribute("cars", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            return "car/car-list";
        }

        Page<Car> carPage =
                carService.findPaginated(name, producerId, status, minPrice, maxPrice, page, pageSize, sortBy, sortDir);
        model.addAttribute("cars", carPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("totalItems", carPage.getTotalElements());
        return "car/car-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("carDTO", new CarDTO());
        addFormModel(model);
        return "car/car-form";
    }

    @PostMapping("/save")
    public String saveForm(@Valid @ModelAttribute("carDTO") CarDTO car, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addFormModel(model);
            return "car/car-form";
        }
        try {
            carService.save(car);
            redirectAttributes.addFlashAttribute("toastMessage", "Lưu xe thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/admin/cars";
        } catch (Exception e) {
            addFormModel(model);
            model.addAttribute("error", e.getMessage());
            return "car/car-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("carDTO", carService.findDTOById(id));
        addFormModel(model);
        return "car/car-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteForm(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Car before = carService.findById(id);
            boolean wasRented = before != null && "Rented".equalsIgnoreCase(before.getStatus());
            carService.deleteById(id);
            Car after = carService.findById(id);
            if (after != null && "Inactive".equals(after.getStatus())) {
                if (wasRented) {
                    redirectAttributes.addFlashAttribute("toastMessage",
                            "Xe đang được thuê — đã chuyển sang trạng thái Ngừng hoạt động.");
                } else {
                    redirectAttributes.addFlashAttribute("toastMessage",
                            "Xe đã có lịch sử thuê — đã chuyển sang trạng thái Ngừng hoạt động.");
                }
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
