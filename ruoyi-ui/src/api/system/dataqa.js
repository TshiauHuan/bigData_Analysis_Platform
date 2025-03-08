import request from '@/utils/request'

// 查询数据
export function queryData(query) {
  return request({
    url: '/system/qa/query',
    method: 'get',
    params: { query }
  })
}

// 获取关系图数据
export function getGraphData(query, limit) {
  return request({
    url: '/system/qa/graph',
    method: 'get',
    params: { query, limit }
  })
} 