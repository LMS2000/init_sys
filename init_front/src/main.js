import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets
import '@/assets/styles/ruoyi.scss' // ruoyi css
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n
// 文件上传组件
import '@/styles/index.scss' // global css
import App from './App'
import store from './store'
import router from './router'
import Pagination from "@/components/Pagination";
import '@/icons' // icon
import '@/assets/icons' // icon
import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, handleTree } from "@/utils/ruoyi";
import WangEditor from 'wangeditor'
import { download } from '@/utils/request'
// 将WangEditor绑定到Vue实例的原型上，使它可以在全局范围内使用
// Vue.prototype.$WangEditor = WangEditor

// Vue.component('WangEditor', WangEditor)
/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
import modal from '@/plugins/modal'

Vue.prototype.$modal = modal
if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}

// set ElementUI lang to EN
// Vue.use(ElementUI, { locale })
// 如果想要中文版 element-ui，按如下方式声明
Vue.use(ElementUI)
Vue.prototype.resetForm = resetForm
Vue.config.productionTip = false
Vue.component('Pagination', Pagination)
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
