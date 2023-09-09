<template>
  <div>
    <el-form ref="resetForm" :model="resetVo" :rules="rules" label-width="80px">

      <el-form-item label="校验码" prop="emailCode">
        <div class="check-code-panel">
          <el-input v-model="resetVo.emailCode" placeholder="请输入邮箱校验码" />
          </el-input>
          <el-button type="primary" @click="handleSendEmail(user.email)">获取
          </el-button>
        </div>
      </el-form-item>

      <el-form-item label="旧密码" prop="oldPassword">
        <el-input v-model="resetVo.oldPassword" placeholder="请输入旧密码" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="resetVo.newPassword" placeholder="请输入新密码" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="resetVo.confirmPassword" placeholder="请确认新密码" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" @click="submit">保存</el-button>
        <el-button type="danger" size="mini" @click="close">关闭</el-button>
      </el-form-item>
    </el-form>

		<!-- 发送邮件 -->
		<el-dialog title="发送验证码" :visible="dialogSendEmailVisiable">


			<el-form ref="sendEmailInfo" :model="sendEmailInfo" :rules="sendEmailRule">

				<el-form-item prop="email" >
					<el-input ref="email" v-model="sendEmailInfo.email" disabled></el-input>
				</el-form-item>

			<el-form-item prop="code">
				<div class="check-code-panel">
					<el-input ref="code" size="large" placeholder="请输入验证码" v-model="sendEmailInfo.code">
					</el-input>
					<img :src="sendCheckCodeUrl" class="check-code" @click="changeCheckCode(1)" />
				</div>

			</el-form-item>
			</el-form>


			<div slot="footer" class="dialog-footer">
				<el-button @click="closeSendEmail()">取 消</el-button>
				<el-button type="primary" @click="doSendEmail()">发 送</el-button>
			</div>
		</el-dialog>
  </div>

</template>

<script>
  import {
    resetPassword,
    sendEmail
  } from "../../../api/user.js";

  export default {
    props: {
      user: {
        type: Object
      }
    },
    data() {
      const equalToPassword = (rule, value, callback) => {
        console.log(this.resetVo.newPassword)
        if (this.resetVo.newPassword !== value) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      };
      const validateCode = (rule, value, callback) => {
        if (value.length < 5 || value.length > 5) {
          callback(new Error('请输入5位的校验码'))
        } else {
          callback()
        }
      }

      const validateEmail = (rule, value, callback) => {
        if (value.length < 5) {
          callback(new Error('请输入正确的email'))
        } else {
          callback()
        }
      }
      const validateEmailCode = (rule, value, callback) => {
        if (value.length < 5) {
          callback(new Error('请输入正确的邮箱验证码'))
        } else {
          callback()
        }
      }
      return {
        resetVo: {
          oldPassword: 12345678,
          newPassword: 123456789,
          confirmPassword: 123456789,
          emailCode: undefined,
          code: undefined
        },
         dialogSendEmailVisiable: false,
        sendCheckCodeUrl: '',
        sendEmailCode: '',
        checkCodeUrl: "http://localhost:9000/api/user/checkCode",
        sendEmailInfo: {
          email: '',
          code: '',
          type: 1
        },
        sendEmailRule: {

          code: [{
            required: true,
            trigger: 'blur',
            validator: validateCode
          }]
        },
        // 表单校验
        rules: {
          oldPassword: [{
            required: true,
            message: "旧密码不能为空",
            trigger: "blur"
          }],
          newPassword: [{
              required: true,
              message: "新密码不能为空",
              trigger: "blur"
            },
            {
              min: 6,
              max: 20,
              message: "长度在 6 到 20 个字符",
              trigger: "blur"
            }
          ],
          emailCode: [{
              required: true,
              message: "邮箱校验码不能为空",
              trigger: "blur"
            },
            {
              min: 4,
              max: 5,
              message: "长度在5 个字符",
              trigger: "blur"
            }
          ],
          code: [{
              required: true,
              message: "图片验证码",
              trigger: "blur"
            },
            {
              min: 4,
              max: 5,
              message: "长度在 5 个字符",
              trigger: "blur"
            }
          ],
          confirmPassword: [{
              required: true,
              message: "确认密码不能为空",
              trigger: "blur"
            },
            {
              required: true,
              validator: equalToPassword,
              trigger: "blur"
            }
          ]
        }
      };
    },
    methods: {

      //显示发送验证码框
      handleSendEmail(email) {

        this.sendEmailInfo.email = email;
        //type=1表示邮箱校验码
        this.changeCheckCode(1)
        this.dialogSendEmailVisiable = true;
      },
      closeSendEmail() {
        this.dialogSendEmailVisiable = false;
        //type回到0
        this.changeCheckCode(0)
        this.sendEmailInfo.code = '';
      },
      doSendEmail() {
        this.$refs.sendEmailInfo.validate(valid => {
          if (valid) {
            sendEmail(this.sendEmailInfo).then(res => {
              if (res.code == 20000) {
                this.$message.success("校验码已发送到"+this.sendEmailInfo.email);
                    this.closeSendEmail()
              } else {
                this.changeCheckCode(1)
                this.sendEmailInfo.code = ''
                this.$message.error(res.msg);
              }
            }).catch(error => {
              this.$message.error("发送邮件失败!");
              this.changeCheckCode(1)
              this.sendEmailInfo.code = ''
            })

          }

        })
      },
      changeCheckCode(type) {
        this.sendCheckCodeUrl = this.checkCodeUrl + "?type=" + type + "&time=" + new Date().getTime();
      },
      submit() {
        this.$refs["resetForm"].validate(valid => {
          if (valid) {
            console.log("进入修改")
            resetPassword(this.resetVo).then(res => {
              if (res.code == 20000) {
                this.$modal.msgSuccess("修改成功");
              } else {
                this.$modal.msgError(res.msg);
              }

            });
          }
        });
      },
      closeSendEmail() {
      	this.dialogSendEmailVisiable = false;
      	//type回到0
      	this.changeCheckCode(0)
      	this.sendEmailInfo.code = '';
      },	//显示发送验证码框
      close() {
        this.$router.go(-1);
      }
    }
  };
</script>
<style>
  .check-code-panel {
  	width: 100%;
  	display: flex;

  	.check-code {
  		margin-left: 5px;
  		cursor: pointer;
  	}
  }
</style>
