  ```javascript

  created () {
    setTimeout(function () {
      /* 监听电话状态（1空闲、2响铃、3通话） */
      CallListener.addListener((state) => {
        if (state == 1) {
          /* 获取通话时长（单位秒） */
          CallListener.getCallTime((time) => {
            Toast.text({
              duration: 3000,
              message: '电话状态：' + state + '，通话时长：' + time
            })
          }, '13207180317')
        } else {
          Toast.text({
            duration: 3000,
            message: '电话状态：' + state
          })
        }
      })
    },3000)
  },

  ```