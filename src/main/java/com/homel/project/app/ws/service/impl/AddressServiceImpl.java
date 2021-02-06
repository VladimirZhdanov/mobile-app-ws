package com.homel.project.app.ws.service.impl;

import com.homel.project.app.ws.exceptions.UserServiceException;
import com.homel.project.app.ws.io.entity.AddressEntity;
import com.homel.project.app.ws.io.entity.UserEntity;
import com.homel.project.app.ws.io.repositories.AddressRepository;
import com.homel.project.app.ws.io.repositories.UserRepository;
import com.homel.project.app.ws.service.AddressService;
import com.homel.project.app.ws.shared.dto.AddressDto;
import com.homel.project.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String id) {
        List<AddressDto> result = new ArrayList<>();

        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        ModelMapper modelMapper = new ModelMapper();

        addresses.forEach(address -> result.add(modelMapper.map(address, AddressDto.class)));

        return result;
    }

    @Override
    public AddressDto getAddress(String addressId) {

        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        return new ModelMapper().map(addressEntity, AddressDto.class);
    }
}
