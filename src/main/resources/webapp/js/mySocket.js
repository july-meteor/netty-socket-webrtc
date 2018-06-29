// web socket的封装
var Service = (function() {
  /**
   * socket 全局变量定义
   * */
  var Socket = undefined,
    host = "192.168.7.221",
    port = 9527;
  var Service = {
    biz: {
      data: {
        userId: undefined
      },

      /**
       *
       * @param {*} event
       */
      msgHub(event) {
        //判断消息类型
        if (typeof event.data === "string") {
          let data = JSON.parse(event.data);
          if (!data.flag) {
            Service.throwError(data);
            return;
          }
        
        } else if (event.data instanceof Blob) {
          let blob = event.data;
          /**
           * 服务器推送过来的是一个blob并非2进制需要进行读取
           */
          let fileRead = new FileReader();
          fileRead.readAsArrayBuffer(blob);
          fileRead.onloadend=(e)=>{
            let buffer = new Uint8Array(e.target.result);
            iRecoreder.addAudio(buffer);
          };

          // iRecoreder.addAudio(buffer);
    
        }else if( event.data instanceof ArrayBuffer ){
      
        }
      },

      getUUID() {
        function S4() {
          return (((1 + Math.random()) * 0x10000) | 0)
            .toString(16)
            .substring(1);
        }
        return (
          S4() +
          S4() +
          "-" +
          S4() +
          "-" +
          S4() +
          "-" +
          S4() +
          "-" +
          S4() +
          S4() +
          S4()
        );
      },
      sendHub: {
        //登录事件
        login() {
          Service.biz.data.userId = Service.biz.getUUID();
          let request = {
            requestId: Service.biz.data.userId,
            name: Service.biz.data.userId,
            protocolId: Service.biz.protocol.login.code
          };

          Service.Server().send(JSON.stringify(request));
        },
        loginOut() {
          let request = {
            requestId: Service.biz.data.userId,
            name: Service.biz.data.userId,
            protocolId: Service.biz.protocol.login_out.code
          };
          Service.Server().send(JSON.stringify(request));
        },
        //发送一个二进制流
        send_AMR(ArrayBuffer){
          console.log('发送给服务器的语音',ArrayBuffer);
          Service.Server().send(ArrayBuffer);
        }
      },

      /**
       * 协议
       */
      protocol: {
        login: {
          code: 1000,
          value: "用户登录协议"
        },
        cur_online: {
          code: 1001,
          value: "当前在线用户"
        },
        send_message: {
          code: 1002,
          value: "发送文本消息!"
        },
        send_AMR: {
          code: 1003,
          value: "发送AMR二进制流"
        },
        login_out: {
          code: 1004,
          value: "客户端下线请求"
        }
      }
    },
    Server: callblack => {
      //定义websocket
      window.WebSocket = window.WebSocket || window.MozWebSocket;
      /**
       * socket对象，对socket封装定义不参与业务
       */

      var Server = {
        //socket初始化
        init() {
          //获取uuId
          if (window.WebSocket) {
            let url = "ws://" + host + ":" + port + "/";

            Socket = new WebSocket(url);
            //方法初始化
            Socket.onmessage = this.message;
            Socket.onopen = this.open;
            Socket.onclose = this.close;
            Socket.onerror = this.error;
          } else {
            Service.throwError("抱歉，您的浏览器不支持WebSocket协议!");
          }
        },
        status() {
          if (Socket.readyState == WebSocket.OPEN) {
            return true;
          }
          Service.throwError("您还未连接上服务器，请刷新页面重试");
        },

        /**
         *  数据和协议号
         * @param {*} data
         * @param {*} protocol
         */
        send(data) {
          if (Server.status()) {
            Socket.send(data);
          }
        },
        /**
         *  服务器推送的消息
         * @param {*} event
         */
        message(event) {
          Service.biz.msgHub(event);
        },
        /**
         *  websocket建立链接
         * @param {*} event
         */
        open(event) {
          console.info("websocket建立链接!");
          //开始登录
          Service.biz.sendHub.login();
        },
        /**
         *  websocket关闭事件
         * @param {} event
         */
        close(event) {
          console.log("WebSocket已经关闭!");
          Service.biz.sendHub.loginOut();
        },
        /**
         *  出现异常
         * @param {*} event
         */
        error(event) {
          iRecoreder.end();
          Service.throwError("webscoket异常!", event);
        }
      };
      return Server;
    },
    //异常事件
    throwError(message, event) {
      console.error(message, event);
      // alert(message);
      throw () => {
        this.toString = function() {
          return message;
        };
      };
    }
  };

  return Service;
})();

Service.Server().init();

// Service.Server();
// let temp = socket.Server();
// temp.init();
