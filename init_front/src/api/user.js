import request from '@/utils/request'
const api_name = '/user'
export function login(data) {
  return request({
    url: `${api_name}/login`,
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    url: `${api_name}/get/login`,
    method: 'get'
  })
}

export function logout() {
  return request({
    url: `${api_name}/logout`,
    method: 'post'
  })
}

export function pageUserList(data) {
  return request({
    url: `${api_name}/page`,
    method: 'post',
    data
  })
}
export function changeEnable(data) {
  return request({
    url: `${api_name}/change/enable`,
    method: 'post',
    data
  })
}
export function updateUser(data) {
  return request({
    url: `${api_name}/update`,
    method: 'post',
    data
  })
}
export function updateCurrentUser(data) {
  return request({
    url: `${api_name}/update/current`,
    method: 'post',
    data
  })
}

export function resetPassword(data) {
  return request({
    url: `${api_name}/resetPassword`,
    method: 'post',
    data
  })
}

export function deleteBatch(list) {
  return request({
    url: `${api_name}/delete?userIds=${list}`,
    method: 'post'
  })
}

export function register(data) {
  return request({
    url: `${api_name}/register`,
    method: 'post',
    data
  })
}
export function getById(id) {
  return request({
    url: `${api_name}/get/${id}`,
    method: 'get'
  })
}
export function setAvatar(data) {
  return request({
    url: `${api_name}/uploadAvatar`,
    method: 'post',
		data
  })
}
