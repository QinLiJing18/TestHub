<template>
  <div class="executions-page">
    <h2>执行记录</h2>

    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>测试执行记录</span>
          <el-button @click="loadExecutions">🔄 刷新</el-button>
        </div>
      </template>

      <el-table
        :data="executions"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="执行ID" width="80" />
        <el-table-column prop="caseId" label="用例ID" width="80" />
        <el-table-column prop="caseName" label="用例名称" />
        <el-table-column label="执行状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="结果" width="80">
          <template #default="scope">
            <span v-if="scope.row.result" :style="{ color: scope.row.result === 'PASSED' ? 'green' : 'red' }">
              {{ scope.row.result === 'PASSED' ? '✓ 通过' : '✗ 失败' }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewReport(scope.row.id)">查看报告</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 报告预览对话框 -->
    <el-dialog v-model="reportDialogVisible" title="执行报告" width="700px">
      <div v-if="currentReport" class="report-content">
        <div class="report-section">
          <h4>执行信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="执行ID">{{ currentReport.id }}</el-descriptions-item>
            <el-descriptions-item label="用例名称">{{ currentReport.caseName }}</el-descriptions-item>
            <el-descriptions-item label="执行状态">
              <el-tag :type="getStatusType(currentReport.status)">
                {{ formatStatus(currentReport.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="执行结果">
              <span :style="{ color: currentReport.result === 'PASSED' ? 'green' : 'red' }">
                {{ currentReport.result === 'PASSED' ? '✓ 通过' : '✗ 失败' }}
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ currentReport.startTime }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ currentReport.endTime }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="report-section" v-if="currentReport.logs">
          <h4>执行日志</h4>
          <div class="log-content">
            <pre>{{ currentReport.logs }}</pre>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="reportDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { testcaseService } from '../services/api'
import { ElMessage } from 'element-plus'

const executions = ref([])
const loading = ref(false)
const reportDialogVisible = ref(false)
const currentReport = ref(null)

const loadExecutions = async () => {
  loading.value = true
  try {
    const data = await testcaseService.getExecutions()
    executions.value = Array.isArray(data) ? data : []
  } catch (error) {
    ElMessage.error('加载执行记录失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const formatStatus = (status) => {
  const statusMap = {
    'PENDING': '待执行',
    'RUNNING': '执行中',
    'COMPLETED': '已完成',
    'FAILED': '执行失败'
  }
  return statusMap[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'info',
    'RUNNING': 'warning',
    'COMPLETED': 'success',
    'FAILED': 'danger'
  }
  return typeMap[status] || 'info'
}

const viewReport = async (executionId) => {
  try {
    const data = await testcaseService.getReport(executionId)
    currentReport.value = data
    reportDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载报告失败：' + error.message)
  }
}

onMounted(() => {
  loadExecutions()
})
</script>

<style scoped lang="scss">
.executions-page {
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

.report-content {
  padding: 20px 0;

  .report-section {
    margin-bottom: 20px;

    h4 {
      margin-bottom: 10px;
      color: #333;
      font-size: 14px;
    }

    .log-content {
      background-color: #f5f5f5;
      padding: 10px;
      border-radius: 4px;
      max-height: 300px;
      overflow-y: auto;

      pre {
        margin: 0;
        font-size: 12px;
        font-family: 'Courier New', monospace;
        color: #333;
        white-space: pre-wrap;
        word-break: break-all;
      }
    }
  }
}
</style>
