var webRecorder = (function() {
  //兼容
  window.URL = window.URL || window.webkitURL;
  navigator.getUserMedia =
    navigator.getUserMedia ||
    navigator.webkitGetUserMedia ||
    navigator.mozGetUserMedia ||
    navigator.msGetUserMedia;

  let audioContr = function(stream, config = {}, callback) {
    config.sampleBits = config.sampleBits || 8; //采样数位 8, 16
    config.sampleRate = config.sampleRate || 8000 / 6; //采样率(1/6 44100) amr 的采集率是8000
    //采集池配置
    config.bufferSize = config.bufferSize || 16384; //必须是以下值之一：0 ,256,512,1024,2048,4096,8192,16384
    config.numberOfInputChannels = config.numberOfInputChannels || 1;
    config.numberOfOutputChannels = config.numberOfOutputChannels || 1;

    //音源上下文
    let audioCtx = new (window.AudioContext || webkitAudioContext)();
    //多媒体输入
    let audioInputStream = audioCtx.createMediaStreamSource(stream);
    /**
     * 指定缓冲区，声道 API上说越小音质越好
     *
     */
    let audioBufferChannel = audioCtx.createScriptProcessor(
      config.bufferSize,
      config.numberOfInputChannels,
      config.numberOfOutputChannels
    );

    let audioData = {
      size: 0, //录音文件长度
      buffer: [], //录音缓存
      inputSampleRate: audioCtx.sampleRate, //输入采样率
      inputSampleBits: 16, //输入采样数位 8, 16
      outputSampleRate: config.sampleRate, //输出采样率
      oututSampleBits: config.sampleBits, //输出采样数位 8, 16
      readData: function(data) {
        let tempAry = [];
        tempAry.push(new Float32Array(data));
        let tempLength = data.length;
        // this.buffer.push(new Float32Array(data));
        // this.size += tempLength;
        if (data.length) {
      //  this.encodeWAV(tempAry, tempLength);
          return this.encodeAMR(tempAry, tempLength);
        } else {
          return;
        }
      },
      /**
       *  将语音数组屡平
       * @param ary   语音集合
       * @param size  语音长度
       * @returns {Float32Array}
       */
      merge: (ary, size) => {
        let data = new Float32Array(size);
        let offset = 0;
        for (let i = 0; i < ary.length; i++) {
          data.set(ary[i], offset);
          offset += ary[i].length;
        }
        return data;
      },
      /**
       * 将音频流转成wav 该方法是提供给本身录制的
       * @param stream
       * @param size
       * @returns {Blob}
       */
      encodeWAV: function(stream, size) {
        /**
         *  压缩音频
         * @param {*} data
         */
        function compress(data) {
          //合并压缩
          // let compression = parseInt(this.inputSampleRate / this.outputSampleRate);
          let compression = 9;
          let length = data.length / compression;
          let result = new Float32Array(length);
          let index = 0,
            j = 0;
          while (index < length) {
            result[index] = data[j];
            j += compression;
            index++;
          }
          return result;
        }

        let temp = this.merge(stream, size); //数据整理
        this.handleWAVHead(compress(temp));
      },
      /**
       * 给音频文件拼接头文件
       * @param {Float32Array} bytes   
       */
      handleWAVHead(bytes){
        let sampleRate = Math.min(this.inputSampleRate, this.outputSampleRate);
        let sampleBits = Math.min(this.inputSampleBits, this.oututSampleBits);
        let dataLength = bytes.length * (sampleBits / 8);
        let buffer = new ArrayBuffer(44 + dataLength);
        let data = new DataView(buffer);
        let channelCount = 1; //单声道
        let offset = 0;
        let writeString = function(str) {
          for (let i = 0; i < str.length; i++) {
            data.setUint8(offset + i, str.charCodeAt(i));
          }
        };
        // 资源交换文件标识符
        writeString("RIFF");
        offset += 4;
        // 下个地址开始到文件尾总字节数,即文件大小-8
        data.setUint32(offset, 36 + dataLength, true);
        offset += 4;
        // WAV文件标志
        writeString("WAVE");
        offset += 4;
        // 波形格式标志
        writeString("fmt ");
        offset += 4;
        // 过滤字节,一般为 0x10 = 16
        data.setUint32(offset, 16, true);
        offset += 4;
        // 格式类别 (PCM形式采样数据)
        data.setUint16(offset, 1, true);
        offset += 2;
        // 通道数
        data.setUint16(offset, channelCount, true);
        offset += 2;
        // 采样率,每秒样本数,表示每个通道的播放速度
        data.setUint32(offset, sampleRate, true);
        offset += 4;
        // 波形数据传输率 (每秒平均字节数) 单声道×每秒数据位数×每样本数据位/8
        data.setUint32(
          offset,
          channelCount * sampleRate * (sampleBits / 8),
          true
        );
        offset += 4;
        // 快数据调整数 采样一次占用字节数 单声道×每样本的数据位数/8
        data.setUint16(offset, channelCount * (sampleBits / 8), true);
        offset += 2;
        // 每样本数据位数
        data.setUint16(offset, sampleBits, true);
        offset += 2;
        // 数据标识符
        writeString("data");
        offset += 4;
        // 采样数据总数,即数据总大小-44
        data.setUint32(offset, dataLength, true);
        offset += 4;
        // 写入采样数据
        if (sampleBits === 8) {
          for (let i = 0; i < bytes.length; i++, offset++) {
            let s = Math.max(-1, Math.min(1, bytes[i]));
            let val = s < 0 ? s * 0x8000 : s * 0x7fff;
            val = parseInt(255 / (65535 / (val + 32768)));
            data.setInt8(offset, val, true);
          }
        } else {
          for (let i = 0; i < bytes.length; i++, offset += 2) {
            let s = Math.max(-1, Math.min(1, bytes[i]));
            data.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7fff, true);
          }
        }

        let url = window.URL.createObjectURL( new Blob([data], { type: "audio/wav" }));
        console.error("wav",url)
        return new Blob([data], { type: "audio/wav" });
      },
      /**
       *  将float32array 转成 amr编码的流
       * @param {Float32Array} buffer
       * @param size
       */
      encodeAMR(buffer, size) {
        let data = this.merge(buffer, size);
        // console.info("data:",data);
        let amr = AMR.encode(data, this.inputSampleRate, 7);
        // let temp = AMR.toWAV(amr)
        // console.log(temp)
        // let wav = AMR.decode(amr);
        // console.log("float32",wav);
        // let blob = this.handleWAVHead(wav);
        // let url = window.URL.createObjectURL(blob);
        // console.error(url)
        // // console.info("amr:",amr);
        // // console.info("samples:",amr);
        // let amrBlob = new Blob([amr],{'type':'audio/amr'});
        // // // saveAs(amrBlob, 'test.amr');
        // let url = window.URL.createObjectURL(amrBlob);
        // console.error(amrBlob)
        // console.error(url)
        return amr;
      }
    };

    //开始录音
    this.start = callback => {
      //将设备的获取的流写进 缓冲去
      audioInputStream.connect(audioBufferChannel);
      audioBufferChannel.connect(audioCtx.destination);
      if (callback) {
        callback(audioCtx, audioInputStream);
      }
    };

    this.stop = () => {
      //关闭轨道链接
      audioBufferChannel.disconnect();
      console.info("录音停止!");
      // audioData.encodeAMR();
      // let url = window.URL.createObjectURL(blob);
      // console.log(url)
      // callback(url);
    };

    // //音频捕获
    audioBufferChannel.onaudioprocess = e => {
      let buffer = audioData.readData(e.inputBuffer.getChannelData(0));
      if (buffer) {
        //如果有返回就添加

        callback(buffer);
      }
    };
  };

  //抛出异常
  audioContr.throwError = message => {
    alert(message);
    throw () => {
      this.toString = function() {
        return message;
      };
    };
  };
  //获取录音机
  audioContr.get = (callback, blolCallback, config) => {
    if (navigator.getUserMedia) {
      navigator.getUserMedia(
        { audio: true }, //只启用音频
        stream => {
          //返回对象
          callback(new audioContr(stream, config, blolCallback));
        },
        error => {
          switch (error.code || error.name) {
            case "PERMISSION_DENIED":
            case "PermissionDeniedError":
              audioContr.throwError("用户拒绝提供信息。");
              break;
            case "NOT_SUPPORTED_ERROR":
            case "NotSupportedError":
              audioContr.throwError("浏览器不支持硬件设备。");
              break;
            case "MANDATORY_UNSATISFIED_ERROR":
            case "MandatoryUnsatisfiedError":
              audioContr.throwError("无法发现指定的硬件设备。");
              break;
            default:
              audioContr.throwError(
                "无法打开麦克风。异常信息:" + (error.code || error.name)
              );
              break;
          }
        }
      );
    }
  };

  return audioContr;
})();
