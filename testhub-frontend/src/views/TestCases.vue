<template>
  <div class="testcases-page">
    <h2>测试用例管理</h2>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>测试用例列表</span>
          <el-button type="primary" @click="showCreateDialog">+ 新建用例</el-button>
        </div>
      </template>

      <el-table
        :data="cases"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="用例名称" />
        <el-table-column prop="testType" label="测试类型" width="120">
          <template #default="scope">
            <el-tag>{{ scope.row.testType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceId" label="设备ID" width="80" />
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button link type="success" @click="executeCase(scope.row.id)">执行</el-button>
            <el-button link type="primary" @click="editCase(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="deleteCase(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑用例对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用例' : '新建用例'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用例名称">
          <el-input v-model="form.title" placeholder="例：扫地机器人启动测试" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入用例描述" />
        </el-form-item>
        <el-form-item label="测试类型">
          <el-select v-model="form.testType" placeholder="请选择测试类型">
            <el-option label="冒烟测试" value="SMOKE" />
            <el-option label="功能测试" value="FUNCTIONAL" />
            <el-option label="性能测试" value="PERFORMANCE" />
            <el-option label="兼容性测试" value="COMPATIBILITY" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备">
          <el-select v-model="form.deviceId" placeholder="请选择设备">
            <el-option
              v-for="device in devices"
              :key="device.id"
              :label="device.deviceName"
              :value="device.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCase">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { testcaseService, projectService } from '../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const cases = ref([])
const devices = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  title: '',
  description: '',
  testType: '',
  deviceId: null
})
const editingId = ref(null)

const loadCases = async () => {
  loading.value = true
  try {
    const data = await testcaseService.getCases()
    cases.value = Array.isArray(data) ? data : []
  } catch (error) {
    ElMessage.error('加载用例列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const loadDevices = async () => {
  try {
    const data = await projectService.getDevices()
    devices.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载设备失败：', error)
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  editingId.value = null
  form.value = {
    title: '',
    description: '',
    testType: '',
    deviceId: null
  }
  dialogVisible.value = true
}

const editCase = (row) => {
  isEdit.value = true
  editingId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const saveCase = async () => {
  try {
    if (isEdit.value) {
      await testcaseService.updateCase(editingId.value, form.value)
      ElMessage.success('用例更新成功')
    } else {
      await testcaseService.createCase(form.value)
      ElMessage.success('用例创建成功')
    }
    dialogVisible.value = false
    loadCases()
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

const deleteCase = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此用例吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await testcaseService.deleteCase(id)
    ElMessage.success('用例删除成功')
    loadCases()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

const executeCase = async (id) => {
  try {
    await testcaseService.executeCase(id)
    ElMessage.success('用例执行开始')
    // 可以在这里跳转到执行记录页面
  } catch (error) {
    ElMessage.error('执行失败：' + error.message)
  }
}

onMounted(() => {
  loadCases()
  loadDevices()
})
</script>

<style scoped lang="scss">
.testcases-page {
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
