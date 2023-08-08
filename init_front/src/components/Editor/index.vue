<template>
  <div>
    <div id="seditor" ref="seditor" @input="change"></div>
  </div>
</template>
<script>
import E from 'wangeditor'  // 通过npm 下载并引入wangeditor
export default {
  props: ['value'],   // 组件引用时添加v-model指令，所以value属性就存在，他的值为v-model等于号后面的变量，通过props将他传递过来
  methods: {
    initEditor () {
       // 创建一个editor实例，并将它挂载到，上面的div上，这样这个div就变成了富文本编辑器
      const editor = new E(this.$refs.seditor) 
      editor.create()
       // 将父组件中v-model所绑定的变量传递给value，再将value赋值给编辑器的内容。
      editor.txt.html(this.value)  
       // 监听value，因为数据流是向下的传递的，父组件v-model绑定的属性一变化，那么value就会变化，从而获取到最新的值，更新编辑器中的内容。
      this.$watch('value', () => {  
        editor.txt.html(this.value)
      })
    },
    change (e) {
        // v-model 指令是会给此节点添加一个input事件，我们在给富文本编辑器添加一个input事件，这么做在于，当输入内容后会获取到最新的内容，通过$emit 触发 父组件v-model所添加的input事件并将最新的内容传递过去，传递过去的值默认就会赋给v-model所绑定的属性。
      this.$emit('input', e.target.innerHTML)
    }
  },
  mounted () {
    this.initEditor()
  }
}
</script>
<style>
</style>
