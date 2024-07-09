package com.laundry.version_one.machine;

import com.laundry.version_one.store.Store;
import org.springframework.stereotype.Service;

@Service
public class MachineMapper {
    public Machine toMachine(MachineRequest request){
        Machine machine = new Machine();
        machine.setAverageCostPerHour(request.averageCostPerHour());
        machine.setIsOn(Boolean.FALSE);
        machine.setType(request.type());
        return machine;
    }

    public MachineResponse toMachineResponse(Machine machine) {
        MachineResponse response = new MachineResponse();
        response.setId(machine.getId());
        response.setAverageCostPerHour(machine.getAverageCostPerHour());
        response.setIsOn(machine.getIsOn());
        response.setType(machine.getType());
        return response;
    }
}
