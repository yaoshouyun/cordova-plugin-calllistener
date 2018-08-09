/* 监听电话状态（1空闲、2响铃、3通话） */
this.CallListener.addListener((state) => {
  Toast.text({
    duration: 1000,
    message: '电话状态：' + state
  })
})

/* 获取通话时长（单位秒） */
this.CallListener.getCallTime((time) => {
  Toast.text({
    duration: 1000,
    message: '通话时长：' + time
  })
}, '18916797777')
