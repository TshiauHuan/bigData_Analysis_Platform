<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧聊天区域 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card class="chat-container">
          <div slot="header" class="clearfix">
            <span>数据智能问答系统</span>
          </div>
          
          <!-- 聊天记录区域 -->
          <div class="chat-messages" ref="chatMessages">
            <div v-for="(msg, index) in chatMessages" :key="index" 
                 :class="['message', msg.type === 'user' ? 'user-message' : 'system-message']">
              <div class="message-avatar">
                <i :class="msg.type === 'user' ? 'el-icon-user-solid' : 'el-icon-s-platform'"></i>
              </div>
              <div class="message-content">
                <div class="message-time">{{ msg.time }}</div>
                <div class="message-text" v-html="formatMessage(msg.content)"></div>
              </div>
            </div>
          </div>
          
          <!-- 输入区域 -->
          <div class="chat-input">
            <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入您想查询的数据关键词，例如：智慧城市、空间数据..."
              v-model="inputMessage"
              @keyup.enter.native="sendMessage"
            ></el-input>
            <el-button type="primary" @click="sendMessage" :loading="loading">发送</el-button>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧图谱可视化区域 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card class="graph-container">
          <div slot="header" class="clearfix">
            <span>数据关系图</span>
          </div>
          <div id="graphChart" class="graph-chart"></div>
          
          <!-- 结果数量选择器 -->
          <div class="graph-controls">
            <span>显示结果数量：</span>
            <el-select v-model="resultLimit" @change="updateResultLimit" size="small">
              <el-option v-for="item in limitOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </div>
          
          <!-- 节点详情弹窗 -->
          <el-dialog
            title="数据详情"
            :visible.sync="detailDialogVisible"
            width="60%"
            :before-close="closeDetailDialog">
            <div v-if="selectedNodeData">
              <h3>{{ selectedNodeData.dataObject }}</h3>
              <el-divider></el-divider>
              <div class="node-detail-content">
                <p><strong>数据类型：</strong>{{ selectedNodeData.dataType }}</p>
                <p><strong>网站名称：</strong>{{ selectedNodeData.siteName }}</p>
                <p><strong>浏览次数：</strong>{{ selectedNodeData.viewCount }}</p>
                <p><strong>下载次数：</strong>{{ selectedNodeData.downloadCount }}</p>
                <p><strong>来源链接：</strong>
                  <a :href="selectedNodeData.sourceLink" target="_blank">{{ selectedNodeData.sourceLink }}</a>
                </p>
                <el-divider></el-divider>
                <p><strong>数据质量描述文本：</strong></p>
                <div class="description-content">{{ selectedNodeData.description }}</div>
              </div>
            </div>
          </el-dialog>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import request from '@/utils/request';

export default {
  name: "DataQa",
  data() {
    return {
      inputMessage: '',
      chatMessages: [],
      loading: false,
      myChart: null,
      currentQuery: '',
      resultLimit: 20,
      limitOptions: [10, 20, 30, 50, 100],
      detailDialogVisible: false,
      selectedNodeData: null,
      graphData: null
    };
  },
  mounted() {
    // 初始化欢迎消息
    this.addSystemMessage("您好！我是数据智能助手。请告诉我您想了解什么类型的数据？例如：智慧城市、空间数据等。");
    
    // 初始化图表
    this.initChart();
    
    // 监听窗口大小变化，调整图表尺寸
    window.addEventListener('resize', this.resizeChart);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart);
    if (this.myChart) {
      this.myChart.dispose();
    }
  },
  methods: {
    // 发送消息
    sendMessage() {
      if (!this.inputMessage.trim()) {
        return;
      }
      
      // 添加用户消息到聊天列表
      this.addUserMessage(this.inputMessage);
      
      // 保存当前查询
      this.currentQuery = this.inputMessage;
      
      // 清空输入框
      const message = this.inputMessage;
      this.inputMessage = '';
      
      // 显示加载状态
      this.loading = true;
      
      // 发送请求到后端
      request({
        url: '/system/qa/query',
        method: 'get',
        params: { query: message }
      }).then(response => {
        if (response.code === 200) {
          // 添加系统回复
          this.addSystemMessage(response.data.replyMessage);
          
          // 获取并展示关系图
          this.fetchGraphData(message, this.resultLimit);
        } else {
          this.addSystemMessage("抱歉，处理您的请求时出现了问题。请稍后再试。");
        }
      }).catch(error => {
        console.error("Error:", error);
        this.addSystemMessage("抱歉，系统出现了错误。请稍后再试。");
      }).finally(() => {
        this.loading = false;
      });
    },
    
    // 添加用户消息
    addUserMessage(content) {
      this.chatMessages.push({
        type: 'user',
        content: content,
        time: this.formatTime(new Date())
      });
      this.scrollToBottom();
    },
    
    // 添加系统消息
    addSystemMessage(content) {
      this.chatMessages.push({
        type: 'system',
        content: content,
        time: this.formatTime(new Date())
      });
      this.scrollToBottom();
    },
    
    // 格式化时间
    formatTime(date) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },
    
    // 聊天窗口滚动到底部
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.chatMessages;
        container.scrollTop = container.scrollHeight;
      });
    },
    
    // 格式化消息内容（将换行符转换为HTML）
    formatMessage(message) {
      return message.replace(/\n/g, '<br>');
    },
    
    // 初始化图表
    initChart() {
      const chartDom = document.getElementById('graphChart');
      this.myChart = echarts.init(chartDom);
      
      const option = {
        title: {
          text: '数据关系图',
          subtext: '请输入查询获取相关数据',
          top: 'top',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            // 自定义提示框内容
            if (params.dataType === 'node') {
              const data = params.data;
              // 对于数据对象节点
              if (data.category === 0) {
                return `<div>
                  <p><strong>${data.name}</strong></p>
                  <p>类型：数据对象</p>
                  <p style="color:#409EFF">点击查看详情</p>
                </div>`;
              } else {
                // 对于来源链接节点
                if (data.nodeType === '来源链接') {
                  return `<div>
                    <p><strong>${data.originalName || data.name}</strong></p>
                    <p style="color:#409EFF">点击访问链接</p>
                  </div>`;
                } 
                // 对于其他属性节点
                else {
                  return `<div>
                    <p><strong>${data.nodeType}：</strong></p>
                    <p>${data.originalName || data.name}</p>
                  </div>`;
                }
              }
            } else if (params.dataType === 'edge') {
              // 对于边，只显示关系名称，不显示ID
              return params.name;
            }
          }
        },
        legend: {
          top: 'bottom',
          data: ['数据对象', '属性']
        },
        series: [
          {
            type: 'graph',
            layout: 'force',
            data: [],
            links: [],
            categories: [
              { name: '数据对象' },
              { name: '属性' }
            ],
            roam: true,
            label: {
              show: true,
              position: 'right',
              formatter: (params) => {
                const name = params.data.name || '';
                // 对属性节点限制显示长度
                if (params.data.category === 1) {
                  return name.length > 10 ? name.substring(0, 10) + '...' : name;
                }
                // 数据对象完整显示
                return name;
              }
            },
            force: {
              repulsion: 100,
              edgeLength: 80
            },
            lineStyle: {
              color: 'source',
              curveness: 0.3
            },
            // 添加节点点击事件
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.3)'
              }
            }
          }
        ]
      };
      
      this.myChart.setOption(option);
      
      // 注册节点点击事件
      this.myChart.on('click', this.handleChartClick);
    },
    
    // 处理图表点击事件
    handleChartClick(params) {
      if (params.dataType === 'node') {
        const nodeId = params.data.id;
        
        // 处理数据对象节点点击
        if (params.data.category === 0) {
          const nodeData = this.findNodeData(nodeId);
          if (nodeData) {
            this.selectedNodeData = nodeData;
            this.detailDialogVisible = true;
          }
        } 
        // 处理属性节点点击 - 特别是来源链接
        else if (params.data.category === 1) {
          // 检查是否是来源链接节点
          if (params.data.nodeType === '来源链接' && params.data.originalName) {
            // 打开链接
            window.open(params.data.originalName, '_blank');
          }
        }
      }
    },
    
    // 根据节点ID查找完整数据
    findNodeData(nodeId) {
      if (!this.graphData || !this.graphData.dataMap) return null;
      return this.graphData.dataMap[nodeId];
    },
    
    // 关闭详情对话框
    closeDetailDialog() {
      this.detailDialogVisible = false;
      this.selectedNodeData = null;
    },
    
    // 调整图表大小
    resizeChart() {
      if (this.myChart) {
        this.myChart.resize();
      }
    },
    
    // 获取图谱数据
    fetchGraphData(query, limit) {
      request({
        url: '/system/qa/graph',
        method: 'get',
        params: { query: query, limit: limit }
      }).then(response => {
        if (response.code === 200) {
          this.updateGraph(response.data);
        }
      }).catch(error => {
        console.error("Error fetching graph data:", error);
      });
    },
    
    // 更新图表数据
    updateGraph(graphData) {
      this.graphData = graphData;
      
      // 准备节点数据
      const nodes = graphData.nodes.map(node => {
        // 保存原始名称用于tooltip显示
        const originalName = node.name;
        
        // 对属性节点名称进行限制
        let displayName = node.name;
        if (node.category === 1 && displayName.length > 10) {
          displayName = displayName.substring(0, 10) + '...';
        }
        
        return {
          id: node.id,
          name: displayName,
          originalName: originalName,
          symbolSize: node.symbolSize,
          category: node.category,
          nodeType: node.nodeType // 添加节点类型，用于识别特殊节点
        };
      });
      
      // 准备边数据
      const links = graphData.links.map(link => ({
        source: link.source,
        target: link.target,
        name: link.name
      }));
      
      const option = {
        title: {
          text: `"${this.currentQuery}" 相关数据关系图`,
          subtext: `找到 ${graphData.nodes.length} 个节点和 ${graphData.links.length} 个关系`
        },
        series: [
          {
            data: nodes,
            links: links
          }
        ]
      };
      
      this.myChart.setOption(option);
    },
    
    // 更新结果数量限制
    updateResultLimit() {
      if (this.currentQuery) {
        this.fetchGraphData(this.currentQuery, this.resultLimit);
      }
    }
  }
};
</script>

<style scoped>
.chat-container {
  height: calc(100vh - 150px);
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止内容溢出 */
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  margin-bottom: 5px;
  max-height: calc(100vh - 250px); /* 调整高度，减少空间 */
}

.message {
  display: flex;
  margin-bottom: 15px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background-color: #409EFF;
  color: white;
}

.system-message .message-avatar {
  background-color: #67C23A;
  color: white;
}

.message-content {
  background-color: #f4f4f5;
  padding: 10px;
  border-radius: 4px;
  max-width: 80%;
}

.user-message .message-content {
  background-color: #ecf5ff;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.message-text {
  word-break: break-word;
}

.chat-input {
  padding: 5px 0; /* 减少内边距 */
  display: flex;
  align-items: flex-start; /* 改为顶部对齐 */
  margin-bottom: 5px; /* 减少底部边距 */
  min-height: 60px; /* 减小最小高度 */
}

.chat-input .el-textarea {
  margin-right: 5px; /* 减少右边距 */
  flex: 1;
}

.chat-input .el-textarea /deep/ .el-textarea__inner {
  min-height: 60px !important; /* 减小输入框高度 */
}

.chat-input .el-button {
  height: 60px; /* 减小按钮高度 */
  padding: 0 15px; /* 减小按钮宽度 */
  margin-top: 0; /* 调整按钮位置 */
}

.graph-container {
  height: calc(100vh - 150px);
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止内容溢出 */
}

.graph-chart {
  width: 100%;
  flex: 1;
  min-height: calc(100vh - 280px); /* 调整图表区域高度 */
}

.graph-controls {
  padding: 15px 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-bottom: 10px; /* 添加底部边距 */
  height: 50px; /* 固定高度 */
}

.graph-controls .el-select {
  margin-left: 10px;
  width: 100px;
}

.node-detail-content {
  max-height: 50vh;
  overflow-y: auto;
  padding: 10px;
}

.description-content {
  background-color: #f8f8f8;
  padding: 15px;
  border-radius: 4px;
  margin-top: 10px;
  white-space: pre-line;
}
</style> 