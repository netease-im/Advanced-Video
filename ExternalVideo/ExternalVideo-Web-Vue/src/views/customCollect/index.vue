<template>
  <div class="wrapper">
    <div class="content">
      <!--画面div-->
      <div class="main-window" ref="large"></div>
      <div class="sub-window-wrapper">
          <!--小画面div-->
        <template v-if="remoteStreams.length">
            <div
            v-for="item in remoteStreams"
            :key="item.getId()"
            class="sub-window"
            ref="small"
            :data-uid="item.getId()"
            ></div>
        </template>
        <div v-else class="sub-window" ref="small">
            <span class="loading-text">等待对方加入…</span>
        </div>
      </div>
    </div>
    <!--底层栏-->
    <ul class="tab-bar">
      <li class="set-wrapper" @click="drawer=true">
        <a href="javascript:;" class="set">选择视频源</a>
      </li>
      <li class="over" @click="handleOver"></li>
    </ul>
    <el-drawer
      title="选择视频源"
      :visible.sync="drawer"
      direction="ltr"
    >
        <div class="pl20 mb20">
            <el-radio v-model="radio" label="1">本地实时音视频数据</el-radio>
        </div>
        <div class="pl20 mb20">
            <el-radio v-model="radio" label="2">本地上传</el-radio>
            <input :disabled="radio!=='2'" type="file" accept="video/*" v-on:input="changeStream" />
        </div>
        <div class="t-center">
            <el-button type="primary" @click="play">{{localStream ? '停止' : '播放'}}</el-button>
        </div>
    </el-drawer>
  </div>
</template>
<script>
    import { message } from '../../components/message';
    import WebRTC2 from '../../sdk/NIM_Web_WebRTC2_v3.9.1.js';
    import config from '../../../config';
    import { getToken } from '../../common';

    export default {
        name: 'customCollect',
        data() {
            return {
                radio: '1', // 视频源类型 1实时 2上传
                drawer: false,
                client: null,
                localUid: Math.ceil(Math.random() * 1e5),
                localStream: null,
                remoteStreams: [],
                max: 4,
                uploadStream: null,
                customStream: null,
            };
        },
        mounted() {
            // 初始化音视频实例
            console.warn('初始化音视频sdk');
            window.self = this;
            this.client = WebRTC2.createClient({
                appkey: config.appkey,
                debug: true,
            });
            //监听事件
            this.client.on('peer-online', (evt) => {
                console.warn(`${evt.uid} 加入房间`);
            });

            this.client.on('peer-leave', (evt) => {
                console.warn(`${evt.uid} 离开房间`);
                this.remoteStreams = this.remoteStreams.filter(
                    (item) => !!item.getId() && item.getId() !== evt.uid
                );
            });

            this.client.on('stream-added', async (evt) => {
                const stream = evt.stream;
                const userId = stream.getId();
                if (this.remoteStreams.some(item => item.getId() === userId)) {
                    console.warn('收到已订阅的远端发布，需要更新', stream);
                    this.remoteStreams = this.remoteStreams.map(item => item.getId() === userId ? stream : item);
                    await this.subscribe(stream);
                } else if (this.remoteStreams.length < this.max - 1) {
                    console.warn('收到新的远端发布消息', stream)
                    this.remoteStreams = this.remoteStreams.concat(stream)
                    await this.subscribe(stream);
                } else {
                    console.warn('房间人数已满')
                }
            });

            this.client.on('stream-removed', (evt) => {
                const stream = evt.stream
                const userId = stream.getId()
                stream.stop();
                this.remoteStreams = this.remoteStreams.map(item => item.getId() === userId ? stream : item)
                console.warn('远端流停止订阅，需要更新', userId, stream)
            });

            this.client.on('stream-subscribed', (evt) => {
                console.warn('收到了对端的流，准备播放');
                const remoteStream = evt.stream;
                //用于播放对方视频画面的div节点
                const div = [...this.$refs.small].find(
                    (item) => {
                        return Number(item.dataset.uid) === Number(remoteStream.getId())
                    }
                );
                remoteStream
                    .play(div)
                    .then(() => {
                        console.warn('播放视频');
                        remoteStream.setRemoteRenderMode({
                            // 设置视频窗口大小
                            width: 160,
                            height: 90,
                            cut: false, // 是否裁剪
                        });
                    })
                    .catch((err) => {
                        console.warn('播放对方视频失败了: ', err);
                    });
            });

            this.getToken().then(token => {
                this.joinChannel(token)
            }).catch(e => {
                message(e)
                console.error(e)
            })
        },
        destroyed() {
            try {
                this.localStream.destroy()
                WebRTC2.destroy()
            } catch (e) {
                // 为了兼容低版本，用try catch包裹一下
            }
        },
        methods: {
            getToken() {
                return getToken({
                    uid: this.localUid,
                    appkey: config.appkey,
                    appSecret: config.appSecret,
                    channelName: this.$route.query.channelName
                }).then(token => {
                    return token
                }, (e) => {
                    throw e;
                });
            },
            returnJoin(time = 2000) {
                setTimeout(() => {
                    this.$router.push({
                        path: '/',
                        query: {
                            path: 'customCollect',
                        },
                    });
                }, time);
            },
            joinChannel(token) {
                if (!this.client) {
                    message('内部错误，请重新加入房间');
                    return;
                }
                console.info('开始加入房间: ', this.$route.query.channelName);
                return this.client.join({
                    channelName: this.$route.query.channelName,
                    uid: this.localUid,
                    token,
                }).then((data) => {
                    console.info('加入房间成功，开始初始化本地音视频流');
                }, (error) => {
                    console.error('加入房间失败：', error);
                    message(`${error}: 请检查appkey或者token是否正确`);
                    this.returnJoin();
                })
            },
            initLocalStream() {
                if (!this.customStream) {
                    message('请先选择视频源');
                    return
                }
                const videoSource = this.customStream.getVideoTracks()[0];
                const audioSource = this.customStream.getAudioTracks()[0];
                //初始化本地的Stream实例，用于管理本端的音视频流
                this.localStream = WebRTC2.createStream({
                    uid: this.localUid,
                    audio: true, //是否启动mic
                    video: true, //是否启动camera
                    screen: false, //是否启动屏幕共享
                    videoSource,
                    audioSource,
                });

                //设置本地视频质量
                this.localStream.setVideoProfile({
                    resolution: WebRTC2.VIDEO_QUALITY_720p, //设置视频分辨率
                    frameRate: WebRTC2.CHAT_VIDEO_FRAME_RATE_15, //设置视频帧率
                });
                //设置本地音频质量
                this.localStream.setAudioProfile('speech_low_quality');
                //启动媒体，打开实例对象中设置的媒体设备
                this.localStream
                    .init()
                    .then(() => {
                        console.warn('音视频开启完成，可以播放了');
                        const div = self.$refs.large;
                        this.localStream.play(div);
                        this.localStream.setLocalRenderMode({
                            // 设置视频窗口大小
                            width: div.clientWidth,
                            height: div.clientHeight,
                            cut: true, // 是否裁剪
                        });
                        // 发布
                        this.publish();
                    })
                    .catch((err) => {
                        console.warn('音视频初始化失败: ', err);
                        message('音视频初始化失败');
                        this.localStream = null;
                    });
            },
            publish() {
                console.warn('开始发布视频流');
                //发布本地媒体给房间对端
                this.client
                    .publish(this.localStream)
                    .then(() => {
                        console.warn('本地 publish 成功');
                    })
                    .catch((err) => {
                        console.error('本地 publish 失败: ', err);
                        message('本地 publish 失败');
                    });
            },
            unpublish() {
                console.warn('停止发布视频流');
                //发布本地媒体给房间对端
                return this.client.unpublish(this.localStream)
            },
            subscribe(remoteStream) {
                remoteStream.setSubscribeConfig({
                    audio: true,
                    video: true,
                });
                this.client
                    .subscribe(remoteStream)
                    .then(() => {
                        console.warn('本地 subscribe 成功');
                    })
                    .catch((err) => {
                        console.warn('本地 subscribe 失败: ', err);
                        message('订阅对方的流失败');
                    });
            },
            handleOver() {
                console.warn('离开房间');
                this.client.leave();
                this.returnJoin(1);
            },
            changeStream(e) {
                const file = e.target.files[0];
                const reader = new FileReader();
                reader.addEventListener('error', error => {
                    console.error('fileReader Error: ' + error);
                })
                reader.addEventListener('abort', e => {
                    console.warn('fileReader Abort: ', e);
                })
                reader.addEventListener('load', e => {
                    console.warn('fileReader Load: ', e);
                    const videoDom = document.createElement('video');
                    videoDom.autoplay = 'autoplay';
                    videoDom.loop = true;
                    videoDom.muted = true;
                    videoDom.src = e.target.result;
                    videoDom.oncanplay = e => {
                        let stream = null
                        if (videoDom.captureStream) {
                            stream = videoDom.captureStream();
                            console.log('Captured stream from videoDom with captureStream', stream);
                        } else if (videoDom.mozCaptureStream) {
                            stream = videoDom.mozCaptureStream();
                            console.log('Captured stream from videoDom with mozCaptureStream()', stream);
                        } else {
                            console.log('captureStream() not supported');
                        }
                        if (stream) {
                            this.uploadStream = stream
                        }
                    }
                    videoDom.play();
                })
                reader.readAsDataURL(file);
            },
            play() {
                if (this.localStream) {
                    Promise.all([
                        this.localStream.close({ type: 'video' }),
                        this.localStream.close({ type: 'audio' }),
                    ]).then(() => {
                        this.localStream.stop();
                        this.localStream.destroy();
                        this.localStream = null;
                        console.info('停止推送自定义流成功！');
                    }).catch(err => {
                        console.error('停止推送自定义流失败: ', err);
                        message('停止推送自定义流失败');
                    });
                    return;
                }
                if (this.radio === '1') {
                    navigator.mediaDevices.getUserMedia({
                        video: true,
                        audio: true
                    }).then(mediaStream => {
                        this.customStream = mediaStream;
                        this.initLocalStream();
                    }).catch(err => {
                        console.error('获取实时音视频数据失败: ', err)
                        message('获取实时音视频数据失败');
                    })
                } else if (this.radio === '2') {
                    this.customStream = this.uploadStream;
                    this.initLocalStream();
                } else {
                    message('未选择视频来源！');
                }
            }
        },
    };
</script>

<style scoped lang="less">
.wrapper {
  height: 100vh;
  background-image: linear-gradient(179deg, #141417 0%, #181824 100%);
  display: flex;
  flex-direction: column;

  .content {
    flex: 1;
    display: flex;
    position: relative;

    .main-window {
      height: 100%;
      width: 67vh;
      //width: 37vw;
      //width: 427px;
      margin: 0 auto;
      background: #25252d;
    }

    .sub-window-wrapper {
        position: absolute;
        top: 16px;
        right: 16px;
        z-index: 9;
        width: 165px;
    }

    .sub-window {
      background: #25252d;
      border: 1px solid #ffffff;
      margin-bottom: 20px;

      .loading-text {
        display: block;
        width: 100%;
        text-align: center;
        line-height: 90px;
        font-size: 12px;
        color: #fff;
        font-weight: 400;
      }
    }
  }

  .tab-bar {
    height: 54px;
    background-image: linear-gradient(180deg, #292933 7%, #212129 100%);
    box-shadow: 0 0 0 0 rgba(255, 255, 255, 0.3);
    list-style: none;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;

    li {
      height: 54px;
      width: 125px;
      cursor: pointer;

      &.set-wrapper {
          display: flex;
          justify-content: center;
          align-items: center;

          &:hover {
              background-color: #18181d;
          }

          .set {
            background-color: #2A6AF2;
            color: #fff;
            display: inline-block;
            width: 68px;
            height: 36px;
            text-align: center;
            line-height: 36px;
            font-size: 12px;
            text-decoration: none;
            font-weight: 500;
            border-radius: 100px;
        }
      }

      //结束按钮
      &.over {
        background: url("../../assets/img/icon/over.png") no-repeat center;
        background-size: 68px 36px;

        &:hover {
          background: url("../../assets/img/icon/over-hover.png") no-repeat
            center;
          background-size: 68px 36px;
        }

        &:active {
          background: url("../../assets/img/icon/over-click.png") no-repeat
            center;
          background-size: 68px 36px;
        }
      }
    }
  }
}
</style>
