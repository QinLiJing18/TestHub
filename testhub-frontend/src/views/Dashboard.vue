<template>
  <div class="dashboard">
    <h2>仪表板</h2>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-icon projects">📦</div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.projects }}</div>
            <div class="stat-label">项目总数</div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-icon devices">🤖</div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.devices }}</div>
            <div class="stat-label">设备总数</div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-icon cases">📝</div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.testcases }}</div>
            <div class="stat-label">测试用例</div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-icon executions">✅</div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.executions }}</div>
            <div class="stat-label">执行记录</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card class="box-card recent-activity">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
        </div>
      </template>
      
      <el-empty v-if="recentActivities.length === 0" description="暂无活动记录" />
      
      <el-timeline v-else>
        <el-timeline-item
          v-for="(activity, index) in recentActivities"
          :key="index"
          :timestamp="activity.time"
          placement="top"
        >
          {{ activity.description }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { projectService, testcaseService } from '../services/api'

const stats = ref({
  projects: 0,
  devices: 0,
  testcases: 0,
  executions: 0
})

const recentActivities = ref([
  {
    time: '今天 16:30',
    description: '欢迎使用 TestHub IoT 测试管理平台'
  },
  {
    time: '今天 16:20',
    description: '后端服务已启动，所有API接口就绪'
  },
  {
    time: '今天 16:10',
    description: '数据库初始化完成'
  }
])

onMounted(async () => {
  try {
    // 获取项目数据
    const projects = await projectService.getProjects()
    stats.value.projects = Array.isArray(projects) ? projects.length : 0

    // 获取设备数据
    const devices = await projectService.getDevices()
    stats.value.devices = Array.isArray(devices) ? devices.length : 0

    // 获取测试用例
    const cases = await testcaseService.getCases()
    stats.value.testcases = Array.isArray(cases) ? cases.length : 0

    // 获取执行记录
    const executions = await testcaseService.getExecutions()
    stats.value.executions = Array.isArray(executions) ? executions.length : 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
})
</script>

<style scoped lang="scss">
.dashboard {
  h2 {
    margin-bottom: 20px;
    color: #333;
  }
}

.stats-row {
  margin-bottom: 30px;

  .stat-card {
    display: flex;
    align-items: center;
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
    }

    .stat-icon {
      font-size: 40px;
      margin-right: 15px;
      min-width: 50px;
    }

    .stat-content {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        color: #333;
      }

      .stat-label {
        font-size: 14px;
        color: #999;
        margin-top: 5px;
      }
    }
  }
}

.box-card {
  background: white;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.recent-activity {
  margin-top: 20px;
}
</style>
