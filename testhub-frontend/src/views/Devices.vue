<template>
  <div class="devices-page">
    <h2>设备管理</h2>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>设备列表</span>
          <el-button type="primary" @click="showCreateDialog">+ 添加设备</el-button>
        </div>
      </template>

      <el-table
        :data="devices"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="deviceType" label="设备类型" width="120" />
        <el-table-column prop="mqttClientId" label="MQTT ID" width="140" />
        <el-table-column label="在线状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.online ? 'success' : 'danger'">
              {{ scope.row.online ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="editDevice(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="deleteDevice(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑设备对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设备' : '添加设备'">
      <el-form :model="form" label-width="120px">
        <el-form-item label="设备名称">
          <el-input v-model="form.deviceName" placeholder="例：扫地机器人-T1000" />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="form.deviceType" placeholder="请选择设备类型">
            <el-option label="扫地机器人" value="ROBOT_VACUUM" />
            <el-option label="智能音箱" value="SMART_SPEAKER" />
            <el-option label="空气净化器" value="AIR_PURIFIER" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="MQTT ID">
          <el-input v-model="form.mqttClientId" placeholder="请输入MQTT客户端ID" />
        </el-form-item>
        <el-form-item label="所属项目">
          <el-select v-model="form.projectId" placeholder="请选择项目">
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.projectName"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDevice">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { projectService } from '../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const devices = ref([])
const projects = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  deviceName: '',
  deviceType: '',
  mqttClientId: '',
  projectId: null
})
const editingId = ref(null)

const loadDevices = async () => {
  loading.value = true
  try {
    const data = await projectService.getDevices()
    devices.value = Array.isArray(data) ? data : []
  } catch (error) {
    ElMessage.error('加载设备列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const loadProjects = async () => {
  try {
    const data = await projectService.getProjects()
    projects.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载项目失败：', error)
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  editingId.value = null
  form.value = {
    deviceName: '',
    deviceType: '',
    mqttClientId: '',
    projectId: null
  }
  dialogVisible.value = true
}

const editDevice = (row) => {
  isEdit.value = true
  editingId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const saveDevice = async () => {
  try {
    if (isEdit.value) {
      await projectService.updateDevice(editingId.value, form.value)
      ElMessage.success('设备更新成功')
    } else {
      await projectService.createDevice(form.value)
      ElMessage.success('设备添加成功')
    }
    dialogVisible.value = false
    loadDevices()
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

const deleteDevice = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此设备吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await projectService.deleteDevice(id)
    ElMessage.success('设备删除成功')
    loadDevices()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

onMounted(() => {
  loadDevices()
  loadProjects()
})
</script>

<style scoped lang="scss">
.devices-page {
  h2 {
    margin-bottom: 20px;
    color: #333;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
