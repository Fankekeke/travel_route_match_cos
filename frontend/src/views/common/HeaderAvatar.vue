<template>
  <div>
    <div @click="openAiAssistant" style="display: inline-block; height: 100%; vertical-align: initial">
      <a-icon type="robot"/>
      <span>AI助手</span>
    </div>
    <a-dropdown style="display: inline-block; height: 100%; vertical-align: initial">
      <span style="cursor: pointer">
        <span class="curr-user">{{user.username}}</span>
      </span>
      <a-menu style="width: 150px" slot="overlay">
        <a-menu-item @click="updatePassword">
          <a-icon type="key"/>
          <span>密码修改</span>
        </a-menu-item>
        <a-menu-divider></a-menu-divider>
        <a-menu-item @click="handleSettingClick">
          <a-icon type="setting"/>
          <span>系统定制</span>
        </a-menu-item>
        <a-menu-divider></a-menu-divider>
        <a-menu-item @click="logout">
          <a-icon type="logout"/>
          <span>退出登录</span>
        </a-menu-item>
      </a-menu>
    </a-dropdown>
    <update-password
      @success="handleUpdate"
      @cancel="handleCancelUpdate"
      :user="user"
      :updatePasswordModelVisible="updatePasswordModelVisible">
    </update-password>
    <a-drawer
      title="AI助手"
      placement="right"
      :closable="true"
      :visible="aiAssistantVisible"
      @close="closeAiAssistant"
      width="400"
    >
      <div class="ai-container">
        <div class="chat-box">
          <div v-for="(message, index) in aiMessages" :key="index" :class="['message', message.type]">
            <div class="avatar">
              <img :src="message.avatar" alt="Avatar" />
            </div>
            <div class="content">
              <div v-html="message.text" style="white-space: pre-wrap;"></div>
              <span class="timestamp">{{ message.timestamp }}</span>
            </div>
          </div>
        </div>
        <div class="input-area">
          <input v-model="aiUserInput" @keyup.enter="sendAiMessage" placeholder="请输入您的问题..." />
          <button @click="sendAiMessage">发送</button>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script>
import { mapMutations, mapState } from 'vuex'
import UpdatePassword from '../personal/UpdatePassword'

export default {
  name: 'HeaderAvatar',
  components: {UpdatePassword},
  data () {
    return {
      updatePasswordModelVisible: false,
      aiAssistantVisible: false, // 控制 AI 助手 Drawer 的显示
      aiUserInput: '', // 用户输入的问题
      aiMessages: [
        {
          type: 'bot',
          avatar: 'http://127.0.0.1:9527/imagesWeb/SA1675604990128.jpg',
          text: '您好！有什么我可以帮您的吗？',
          timestamp: this.getFormattedTime()
        }
      ] // 存储聊天记录
    }
  },
  computed: {
    ...mapState({
      settingBar: state => state.setting.settingBar.opened,
      user: state => state.account.user
    }),
    avatar () {
      return `static/avatar/${this.user.avatar}`
    }
  },
  methods: {
    // 打开 AI 助手 Drawer
    openAiAssistant() {
      this.aiAssistantVisible = true;
    },
    // 关闭 AI 助手 Drawer
    closeAiAssistant() {
      this.aiAssistantVisible = false;
    },
    // 发送消息
    sendAiMessage() {
      if (this.aiUserInput.trim() === '') return;

      // 添加用户消息
      this.aiMessages.push({
        type: 'user',
        avatar: 'http://127.0.0.1:9527/imagesWeb/SA1675604212612.jpg',
        text: this.aiUserInput,
        timestamp: this.getFormattedTime()
      });

      // 模拟 AI 回复
      this.generateAiResponse(this.aiUserInput);

      // 清空输入框
      this.aiUserInput = '';
    },
    // 获取格式化时间
    getFormattedTime() {
      const now = new Date();
      return now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    handleSettingClick () {
      this.setSettingBar(!this.settingBar)
    },
    generateAiResponse(content) {
      this.aiMessages.push({
        type: 'bot',
        avatar: 'http://127.0.0.1:9527/imagesWeb/SA1675604990128.jpg',
        text: '请稍后 正在加载中😋',
        timestamp: this.getFormattedTime()
      });

      this.$post(`/cos/ai/aliTyqw`, {
        content: content
      }).then((r) => {
        this.aiMessages.push({
          type: 'bot',
          avatar: 'http://127.0.0.1:9527/imagesWeb/SA1675604990128.jpg',
          text: r.data.msg,
          timestamp: this.getFormattedTime()
        });
      });
    },
    openProfile () {
      this.$router.push('/profile')
    },
    updatePassword () {
      this.updatePasswordModelVisible = true
    },
    handleCancelUpdate () {
      this.updatePasswordModelVisible = false
    },
    handleUpdate () {
      this.updatePasswordModelVisible = false
      this.$message.success('更新密码成功，请重新登录系统')
      setTimeout(() => {
        this.logout()
      }, 1500)
    },
    logout () {
      this.$get(`logout/${this.user.id}`).then(() => {
        return new Promise((resolve, reject) => {
          this.$db.clear()
          location.reload()
        })
      }).catch(() => {
        this.$message.error('退出系统失败')
      })
    },
    ...mapMutations({setSettingBar: 'setting/setSettingBar'})
  }
}
</script>

<style lang="less" scoped>
  .ant-avatar-sm {
    width: 30px;
    height: 30px;
  }
  .avatar {
    margin: 20px 4px 20px 0;
    color: #1890ff;
    background: hsla(0, 0%, 100%, .85);
    vertical-align: middle;
  }
  .curr-user {
    font-weight: 600;
    margin-left: 6px
  }
</style>
<style lang="less" scoped>
// 整体容器
.ai-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

// 聊天记录区域
.chat-box {
  flex: 1;
  padding: 16px;
  background-color: #f5f5f5;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background-color: #dcdcdc;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background-color: #bfbfbf;
  }
}

// 消息项通用样式
.message {
  display: flex;
  align-items: flex-end;
  margin-bottom: 16px;

  .avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 12px;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .content {
    position: relative;
    max-width: 70%;
    padding: 10px 14px;
    border-radius: 18px;
    line-height: 1.4;
    word-wrap: break-word;

    .timestamp {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
      text-align: right;
    }
  }

  // 用户消息样式（右对齐）
  &.user {
    justify-content: flex-end;

    .content {
      background-color: #00bfff;
      color: #fff;
      border-bottom-right-radius: 4px;

      &::after {
        content: "";
        position: absolute;
        bottom: 0;
        right: -8px;
        width: 0;
        height: 0;
        border-left: 8px solid #00bfff;
        border-top: 8px solid transparent;
      }
    }

    .avatar {
      order: 2;
      margin-right: 0;
      margin-left: 12px;
    }
  }

  // AI 消息样式（左对齐）
  &.bot {
    .content {
      background-color: #fff;
      color: #333;
      border-bottom-left-radius: 4px;

      &::after {
        content: "";
        position: absolute;
        bottom: 0;
        left: -8px;
        width: 0;
        height: 0;
        border-right: 8px solid #fff;
        border-top: 8px solid transparent;
      }
    }
  }
}

// 输入区域
.input-area {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  border-top: 1px solid #eee;

  input {
    flex: 1;
    padding: 10px 14px;
    border: 1px solid #ddd;
    border-radius: 20px;
    outline: none;
    font-size: 14px;
    transition: all 0.2s ease-in-out;

    &:focus {
      border-color: #00bfff;
      box-shadow: 0 0 0 2px rgba(0, 191, 255, 0.2);
    }
  }

  button {
    margin-left: 10px;
    padding: 10px 20px;
    background-color: #00bfff;
    color: #fff;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.2s ease-in-out;

    &:hover {
      background-color: #009fd9;
    }
  }
}
</style>
