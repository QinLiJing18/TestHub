package com.testhub.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.testhub.common.exception.BusinessException;
import com.testhub.project.entity.Device;
import com.testhub.project.mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    public List<Device> listDevices(Long projectId) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeleted, 0);
        if (projectId != null) {
            wrapper.eq(Device::getProjectId, projectId);
        }
        wrapper.orderByDesc(Device::getCreateTime);
        return deviceMapper.selectList(wrapper);
    }

    public Device getDeviceById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null || device.getDeleted() == 1) {
            throw new BusinessException(404, "设备不存在");
        }
        return device;
    }

    public Long createDevice(Device device) {
        deviceMapper.insert(device);
        return device.getId();
    }

    public void updateDevice(Device device) {
        deviceMapper.updateById(device);
    }

    public void deleteDevice(Long id) {
        Device device = new Device();
        device.setId(id);
        device.setDeleted(1);
        deviceMapper.updateById(device);
    }
}
