<template>
  <div class="container">
    <div class="content">
      <div class="top" style="margin-right: 25px">
        <div class="header">
<!--          <img alt="logo" src="static/img/logo.png" height="175"/>-->
          <div class="title">{{ systemName }}</div>
        </div>
        <div class="desc">出行路线匹配与在线结算系统</div>
      </div>
      <component :is="componentName" @regist="handleRegist" class="main-content"></component>
    </div>
    <global-footer :copyright="copyright" />
  </div>
</template>

<script>import GlobalFooter from '../common/GlobalFooter'
import Login from './Login'
import Regist from './Regist'

export default {
  name: 'Common',
  components: {GlobalFooter, Login, Regist},
  data () {
    return {
      componentName: 'Login'
    }
  },
  computed: {
    systemName () {
      return this.$store.state.setting.systemName
    },
    copyright () {
      return this.$store.state.setting.copyright
    }
  },
  methods: {
    handleRegist (val) {
      this.componentName = val
    }
  },
  mounted () {
    // 添加淡入动画
    this.$el.style.opacity = 0
    this.$el.style.transform = 'translateY(20px)'

    setTimeout(() => {
      this.$el.style.transition = 'opacity 0.5s ease, transform 0.5s ease'
      this.$el.style.opacity = 1
      this.$el.style.transform = 'translateY(0)'
    }, 100)
  }
}
</script>

<style lang="less" scoped>.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: auto;
  background: linear-gradient(135deg, #c3c5dc 0%, #70657c 100%);
  .content {
    padding: 32px 0;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    @media (min-width: 768px){
      padding: 0;
    }

    .top {
      text-align: center;
      margin-bottom: 40px;

      .header {
        height: auto;
        line-height: normal;

        img {
          height: 80px;
          width: auto;
          filter: drop-shadow(0 4px 8px rgba(0,0,0,0.1));
        }

        .title {
          font-size: 32px;
          color: #fff;
          font-weight: 700;
          margin-top: 20px;
          text-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
      }

      .desc {
        font-size: 16px;
        color: rgba(255,255,255,0.85);
        margin-top: 16px;
        margin-bottom: 0;
      }
    }

    .main-content {
      width: 420px;
      padding: 40px 30px 20px 30px;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 12px;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
      backdrop-filter: blur(10px);

      @media screen and (max-width: 576px) {
        width: 90%;
        margin: 0 5%;
        padding: 30px 20px;
      }
    }
  }
}
</style>
