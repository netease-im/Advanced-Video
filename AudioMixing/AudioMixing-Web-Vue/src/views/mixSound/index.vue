<template>
  <div class="wrapper">
    <div class="content">
      <!--画面div-->
      <div class="main-window" ref="large"></div>
      <div>
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
        <a href="javascript:;" class="set">设置混音</a>
      </li>
      <li class="over" @click="handleOver"></li>
    </ul>
    <el-drawer
      title="设置混音"
      :visible.sync="drawer"
      direction="ltr"
    >
        <div class="pl20 pr20 mb20 flex jcb">
            <span class="mr10">背景音乐</span>
            <el-radio-group @change="changeMusic" v-model="primaryRadio">
                <el-radio-button label="1">音乐1</el-radio-button>
                <el-radio-button label="2">音乐2</el-radio-button>
            </el-radio-group>
        </div>
        <div class="pl20 pr20 mb20 flex jcb">
            <span class="mr10">背景音量</span>
            <div class="f1">
                <el-slider @input="changeVolume" :max="255" v-model="primaryVolume"></el-slider>
            </div>
        </div>
        <!-- <div class="pl20 pr20 mb20 flex jcb">
            <span class="mr10">音效</span>
            <el-radio-group v-model="secondaryRadio">
                <el-radio-button label="1">音效1</el-radio-button>
                <el-radio-button label="2">音效2</el-radio-button>
            </el-radio-group>
        </div>
        <div class="pl20 pr20 mb20 flex jcb">
            <span class="mr10">音效音量</span>
            <div class="f1">
                <el-slider max="255" v-model="secondaryVolume"></el-slider>
            </div>
        </div> -->
        <div class="t-center">
            <el-button type="success" round @click="resumeAudio">播放</el-button>
            <el-button type="warning" round @click="pauseAudio">暂停</el-button>
            <el-button type="danger" round @click="stopAudio">停止</el-button>
        </div>
    </el-drawer>
  </div>
</template>
<script>
    import { message } from '../../components/message';
    import WebRTC2 from '../../sdk/NIM_Web_WebRTC2_v3.7.0.js';
    import config from '../../../config';
    import { getToken } from '../../common';
    import music1 from '../../assets/music/music1.mp3';
    import music2 from '../../assets/music/music2.mp3';

    export default {
        name: 'mixSound',
        data() {
            return {
                drawer: false,
                primaryRadio: null,
                // secondaryRadio: '1',
                primaryVolume: 50,
                // secondaryVolume: 50,
                client: null,
                localUid: Math.ceil(Math.random() * 1e5),
                localStream: null,
                remoteStreams: [],
                max: 4,
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
                    (item) => item.getId() !== evt.uid
                );
            });

            this.client.on('stream-added', (evt) => {
                var remoteStream = evt.stream;
                const uid = remoteStream.getId();
                console.warn('收到对方发布的订阅消息: ', uid);

                if (
                    this.remoteStreams.every((item) => item.getId() !== uid) &&
                    this.remoteStreams.length < this.max - 1
                ) {
                    console.warn('房间新加入一人:  ', uid);
                    this.remoteStreams.push(remoteStream);
                    this.subscribe(remoteStream);
                } else { console.warn('房间人数已满') }
            });

            this.client.on('stream-removed', (evt) => {
                var remoteStream = evt.stream;
                console.warn('对方停止订阅: ', remoteStream.getId());
                remoteStream.stop();
                this.remoteStreams = this.remoteStreams.filter(
                    (item) => item.getId() !== remoteStream.getId()
                );
            });

            this.client.on('stream-subscribed', (evt) => {
                console.warn('收到了对端的流，准备播放');
                const remoteStream = evt.stream;
                //用于播放对方视频画面的div节点
                const div = [...this.$refs.small].find(
                    (item) => item.dataset.uid === remoteStream.getId()
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
                            path: 'mixSound',
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
                this.client
                    .join({
                        channelName: this.$route.query.channelName,
                        uid: this.localUid,
                        token,
                    })
                    .then((data) => {
                        console.info('加入房间成功，开始初始化本地音视频流');
                        this.initLocalStream();
                    })
                    .catch((error) => {
                        console.error('加入房间失败：', error);
                        message(`${error}: 请检查appkey或者token是否正确`);
                        this.returnJoin();
                    });
            },
            initLocalStream() {
                //初始化本地的Stream实例，用于管理本端的音视频流
                this.localStream = WebRTC2.createStream({
                    uid: this.localUid,
                    audio: true, //是否启动mic
                    video: true, //是否启动camera
                    screen: false, //是否启动屏幕共享
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
            changeMusic() {
                if (!this.localStream) {
                    throw Error('内部错误，请重新加入房间')
                }
                const map = {
                    1: music1,
                    2: music2,
                }
                this.localStream.startAudioMixing({
                    audioFilePath: map[this.primaryRadio],
                    loopback: false,
                    replace: false,
                    cycle: 0,
                    playStartTime: 0,
                    volume: this.primaryVolume,
                    auidoMixingEnd: () => { console.warn('伴音结束') }
                }).then(() => {
                    console.warn('开始伴音成功');
                }).catch(err => {
                    message('开始伴音失败');
                    console.error('开始伴音失败: ', err);
                })
            },
            changeVolume() {
                if (!this.localStream) {
                    throw Error('内部错误，请重新加入房间')
                }
                this.localStream.adjustAudioMixingVolume(this.primaryVolume).then(() => {
                    console.warn('设置伴音的音量成功');
                }).catch(err => {
                    console.error('设置伴音的音量失败: ', err);
                })
            },
            resumeAudio() {
                if (!this.localStream) {
                    throw Error('内部错误，请重新加入房间')
                }
                this.localStream.resumeAudioMixing().then(res => {
                    console.warn('恢复伴音成功')
                }).catch(err => {
                    message('恢复伴音失败')
                    console.error('恢复伴音失败: ', err)
                })
            },
            pauseAudio() {
                if (!this.localStream) {
                    throw Error('内部错误，请重新加入房间')
                }
                this.localStream.pauseAudioMixing().then(res => {
                    console.warn('暂停伴音成功')
                }).catch(err => {
                    message('暂停伴音失败')
                    console.error('暂停伴音失败: ', err)
                })
            },
            stopAudio() {
                if (!this.localStream) {
                    throw Error('内部错误，请重新加入房间')
                }
                this.localStream.stopAudioMixing().then(res => {
                    console.warn('停止伴音成功')
                }).catch(err => {
                    message('停止伴音失败')
                    console.error('停止伴音失败: ', err)
                })
            },
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

    .main-window {
      height: 100%;
      width: 67vh;
      //width: 37vw;
      //width: 427px;
      margin: 0 auto;
      background: #25252d;
    }

    .sub-window {
      width: 160px;
      height: 90px;
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
