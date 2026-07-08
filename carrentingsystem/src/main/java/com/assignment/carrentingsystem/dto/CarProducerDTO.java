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

    private Long producerId;

    @NotBlank(message = "Tên hãng không được trống")
    @Size(max = 255, message = "Tên hãng tối đa 255 ký tự")
    private String producerName;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ tối đa 255 kí tự")
    private String address;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 255, message =  "Quốc gia tối đa 255 ký tự")
    private String country;
}
