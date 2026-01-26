<template>
  <div class="projects-page">
    <h2>项目管理</h2>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>项目列表</span>
          <el-button type="primary" @click="showCreateDialog">+ 新建项目</el-button>
        </div>
      </template>

      <el-table
        :data="projects"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="projectName" label="项目名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="editProject(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="deleteProject(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑项目对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑项目' : '新建项目'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入项目描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProject">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { projectService } from '../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const projects = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  projectName: '',
  description: ''
})
const editingId = ref(null)

const loadProjects = async () => {
  loading.value = true
  try {
    const data = await projectService.getProjects()
    projects.value = Array.isArray(data) ? data : []
  } catch (error) {
    ElMessage.error('加载项目失表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  editingId.value = null
  form.value = { projectName: '', description: '' }
  dialogVisible.value = true
}

const editProject = (row) => {
  isEdit.value = true
  editingId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const saveProject = async () => {
  try {
    if (isEdit.value) {
      await projectService.updateProject(editingId.value, form.value)
      ElMessage.success('项目更新成功')
    } else {
      await projectService.createProject(form.value)
      ElMessage.success('项目创建成功')
    }
    dialogVisible.value = false
    loadProjects()
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

const deleteProject = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此项目吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await projectService.deleteProject(id)
    ElMessage.success('项目删除成功')
    loadProjects()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

onMounted(() => {
  loadProjects()
})
</script>

<style scoped lang="scss">
.projects-page {
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
