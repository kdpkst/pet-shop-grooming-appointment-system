package com.cpt202.appointment_system.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpt202.appointment_system.Common.Result;
import com.cpt202.appointment_system.Models.Appointment;
import com.cpt202.appointment_system.Models.User;
import com.cpt202.appointment_system.Repositories.UserRepo;
import com.cpt202.appointment_system.Services.UserService;


@RestController // a RESTfull API
@RequestMapping("/appointment-system")
public class UserController {

    @Autowired
    private UserService userService;

    // Manager Part
    @GetMapping("/manager/customerList")  
    public Result<?> getAllCustomers(){
        return userService.listAllCustomers_m();
    }

    @GetMapping("manager/customerList/search")
    public Result<?> getCustomerByName(@RequestParam User user){
        return userService.searchCustomerByName_m(user);
    }

    // @GetMapping("customer-management/customer")
    // public Result<?> viewCustomer(@RequestParam User user){
    //     return userService.viewOneCustomer(user);
    // }

    // Manager can view all of appointments
    @GetMapping("/manager/appointmentList")
    public List<Appointment> getAllAppointment(){
        return userService.getAppointmentList_m();
    }
    
    // TODO: Manager view their appointments detial
    //@GetMapping("/customer/appointmentList/view")



    //Customer Part
    
    // Customer can view all of history appointments - Demo may have error now
    @GetMapping("/customer/appointmentList")
    public List<Appointment> getUserAppointment_c(@RequestParam User user){
        return userService.getAppointmentList_c(user);
    }

}
