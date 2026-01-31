<template>

</template>

<script>
export default {
  name: 'Staff',
  data () {
    return {
    }
  },
  mounted () {
    this.queryCurrentRouteByStaff()
  },
  methods: {
    queryCurrentRouteByStaff () {
      this.$get('/business/order-info/queryCurrentRouteByStaff').then((r) => {
        this.routeInfo = r.data.data
      })
    }
  }
}
</script>

<style scoped>

</style>
