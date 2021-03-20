package com.homel.project.app.ws.ui.controller;

import com.homel.project.app.ws.io.entity.AddressEntity;
import com.homel.project.app.ws.service.AddressService;
import com.homel.project.app.ws.service.UserService;
import com.homel.project.app.ws.shared.dto.AddressDto;
import com.homel.project.app.ws.shared.dto.UserDto;
import com.homel.project.app.ws.ui.model.request.PasswordResetModel;
import com.homel.project.app.ws.ui.model.request.PasswordResetRequestModel;
import com.homel.project.app.ws.ui.model.request.UserDetailsRequestModel;
import com.homel.project.app.ws.ui.model.response.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users") //http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;
    

    @Autowired
    AddressService addressService;

    @ApiOperation(value = "Get User Details Web Service Endpoint",
    notes = "This Web Service Endpoint returns User Details. User public user id in URL path. For example: /user/gwer23rfefw")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authHeader.description}", paramType = "header")
    })
    @GetMapping(path="/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {

        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);

        BeanUtils.copyProperties(userDto, returnValue);

        Type listType = new TypeToken<List<AddressRest>>() {}.getType();
        returnValue.setAddresses(new ModelMapper().map(userDto.getAddresses(), listType));

        return  returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authHeader.description}", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {

        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        users.forEach(user -> {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(user, userModel);
            returnValue.add(userModel);
        });

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

        UserRest returnValue = new UserRest();

       // if (userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authHeader.description}", paramType = "header")
    })
    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUserById(id);
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authHeader.description}", paramType = "header")
    })
    @GetMapping(path="/{id}/addresses", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) {

        List<AddressRest> returnValue = new ArrayList<>();
        List<AddressDto> addressesDto = addressService.getAddresses(id);

        if (addressesDto != null && !addressesDto.isEmpty()) {

            Type listType = new TypeToken<List<AddressRest>>() {}.getType();
            returnValue = new ModelMapper().map(addressesDto, listType);

            returnValue.forEach( addressRest -> {
                Link userAddressLink = WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(userAddressLink);
            });
        }

        Link userLink = WebMvcLinkBuilder
                .linkTo(UserController.class)
                .slash(id)
                .withRel("user");

        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
                .withSelfRel();

        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authHeader.description}", paramType = "header")
    })
    @GetMapping(path="/{id}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public EntityModel<AddressRest> getUserAddresses(@PathVariable String id, @PathVariable String addressId) {

        AddressDto addressDto = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();
        AddressRest addressRest = modelMapper.map(addressDto, AddressRest.class);

        // http:/localhost:8080/users/<id>
        Link userLink = WebMvcLinkBuilder
                .linkTo(UserController.class)
                .slash(id)
                .withRel("user");

        // http:/localhost:8080/users/<id>/addresses
        Link userAddressesLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
                .withRel("addresses");

        // http:/localhost:8080/users/<id>/addresses/<addressId>
        Link userAddressLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id, addressId))
                .withSelfRel();

        EntityModel.of(addressRest, Arrays.asList(userLink, userAddressesLink, userAddressLink));

        return EntityModel.of(addressRest, Arrays.asList(userLink, userAddressesLink, userAddressLink));
    }

    /*
     * http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
     * */
    @CrossOrigin(origins = "*") //to work with 8080 + 8090 (2 tomcats)
    @GetMapping(path = "/email-verification",
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if(isVerified) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }

    /*
     * http://localhost:8080/mobile-app-ws/users/password-reset-request
     * */
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

    // http://localhost:8080/mobile-app-ws/users/password-reset
    @CrossOrigin(origins = "*")  //to work with 8080 + 8090 (2 tomcats)
    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }
}
