package com.testhub.project.controller;

import com.testhub.common.dto.Result;
import com.testhub.project.entity.Device;
import com.testhub.project.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/devices")
    public Result<List<Device>> listDevices(@RequestParam(required = false) Long projectId) {
        List<Device> devices = deviceService.listDevices(projectId);
        return Result.success(devices);
    }

    @GetMapping("/devices/{id}")
    public Result<Device> getDevice(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return Result.success(device);
    }

    @PostMapping("/devices")
    public Result<Long> createDevice(@RequestBody Device device) {
        Long id = deviceService.createDevice(device);
        return Result.success("创建成功", id);
    }

    @PutMapping("/devices/{id}")
    public Result<String> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        device.setId(id);
        deviceService.updateDevice(device);
        return Result.success("更新成功");
    }

    @DeleteMapping("/devices/{id}")
    public Result<String> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return Result.success("删除成功");
    }

    @GetMapping("/devices/{id}/status")
    public Result<String> getDeviceStatus(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return Result.success(device.getStatus());
    }
}
