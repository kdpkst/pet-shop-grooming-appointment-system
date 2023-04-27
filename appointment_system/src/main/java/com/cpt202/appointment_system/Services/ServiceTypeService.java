package com.cpt202.appointment_system.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpt202.appointment_system.Common.Result;
import com.cpt202.appointment_system.Models.ServiceType;
import com.cpt202.appointment_system.Repositories.ServiceTypeRepo;

@Service
public class ServiceTypeService {
    
    @Autowired
    private ServiceTypeRepo serviceTypeRepo;

    public Result<?> addService_M(ServiceType service) {
        serviceTypeRepo.save(service);
        return Result.success("", "Service added succssfully!");
    }


}
