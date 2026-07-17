package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.CarProducerDTO;
import com.assignment.carrentingsystem.entity.CarProducer;
import com.assignment.carrentingsystem.service.ProducerService;
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
@RequestMapping("/admin/producers")
public class ProducerController {

    private final ProducerService producerService;

    @GetMapping
    public String listAll(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "producerId") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        Page<CarProducer> producerPage = producerService.findPaginated(keyword, country, page, 5, sortBy, sortDir);
        model.addAttribute("producers", producerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", producerPage.getTotalPages());
        model.addAttribute("totalItems", producerPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCountry", country);
        model.addAttribute("countries", producerService.findCountries());
        return "producer/producer-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("producerDTO", new CarProducerDTO());
        return "producer/producer-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("producerDTO") CarProducerDTO producerDTO,
                       BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "producer/producer-form";
        }
        try {
            producerService.save(producerDTO);
            redirectAttributes.addFlashAttribute("toastMessage", "Lưu hãng xe thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/admin/producers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "producer/producer-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("producerDTO", producerService.findDTOById(id));
        return "producer/producer-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            producerService.deleteById(id);
            redirectAttributes.addFlashAttribute("toastMessage", "Xóa hãng xe thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/producers";
    }
}
