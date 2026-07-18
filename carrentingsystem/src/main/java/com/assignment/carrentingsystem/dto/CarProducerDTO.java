package com.assignment.carrentingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarProducerDTO {
    private Integer producerId;

    @NotBlank(message = "Tên hãng không được trống")
    @Size(max = 100, message = "Tên hãng tối đa 100 ký tự")
    private String producerName;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 200, message = "Địa chỉ tối đa 200 ký tự")
    private String address;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 100, message = "Quốc gia tối đa 100 ký tự")
    private String country;
}
