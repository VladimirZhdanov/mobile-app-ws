package com.homel.project.app.ws.service;

import com.homel.project.app.ws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String id);
    AddressDto getAddress(String addressId);
}
