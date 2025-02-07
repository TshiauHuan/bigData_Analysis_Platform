import request from '@/utils/request'

// 查询数据列表
export function listData(query) {
  return request({
    url: '/dataSystem/data/list',
    method: 'get',
    params: query
  })
}

// 查询数据详细
export function getData(id) {
  return request({
    url: '/dataSystem/data/' + id,
    method: 'get'
  })
}

// 新增数据
export function addData(data) {
  return request({
    url: '/dataSystem/data',
    method: 'post',
    data: data
  })
}

// 修改数据
export function updateData(data) {
  return request({
    url: '/dataSystem/data',
    method: 'put',
    data: data
  })
}

// 删除数据
export function delData(id) {
  return request({
    url: '/dataSystem/data/' + id,
    method: 'delete'
  })
}
