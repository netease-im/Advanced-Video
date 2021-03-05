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
        <a href="javascript:;" class="set">配置推流</a>
      </li>
      <li class="over" @click="handleOver"></li>
    </ul>
    <el-drawer
      title="配置推流"
      :visible.sync="drawer"
      direction="ltr"
    >
        <div class="pl20 mb20">
            <span class="mr10">推流地址</span>
            <el-input style="width: 400px;" v-model="rtmpTasks[0].streamUrl" placeholder="输入推流地址" />
        </div>
        <div class="t-center">
            <el-button type="primary" @click="togglePushStats">{{isPushing ? '停止推流' : '开始推流'}}</el-button>
        </div>
    </el-drawer>
  </div>
</template>
<script>
    import { message } from '../../components/message';
    import * as WebRTC2 from '../../sdk/NIM_Web_WebRTC2_v4.0.1.js';
    import config from '../../../config';
    import { getToken } from '../../common';

    const pushUser = {
        uid: null, //用户id
        x: null, // user1 的视频布局x偏移，相对整体布局的左上角（前提是推流发布user1的视频）
        y: null, // user1 的视频布局y偏移，相对整体布局的左上角（前提是推流发布user1的视频）
        width: 500, // user1 的视频布局宽度（前提是推流发布user1的视频）
        height: 360, //user1 的视频布局高度（前提是推流发布user1的视频）
        adaption: 1, //自适应，值默认为1
        pushAudio: true, // 推流是否发布user1 的音频
        pushVideo: true // 推流是否发布user1的视频
    }

    export default {
        name: 'push',
        data() {
            return {
                isSilence: false,
                isStop: false,
                isPushing: false,
                drawer: false,
                client: null,
                localUid: Math.ceil(Math.random() * 1e5),
                localStream: null,
                remoteStreams: [],
                max: 4,
                //互动直播的推流任务，可以设置多个推流任务
                rtmpTasks: [{
                    taskId: Math.random().toString(36).slice(-8), //推流任务ID,string格式。taskId为推流任务的唯一标识，用于过程中增删任务操作
                    streamUrl: '',
                    record: false, //录制开关
                    layout: {
                        canvas: { //整体布局大小
                            width: 1280, //整体布局宽度
                            height: 720, //整体布局高度
                            color: 16777215 //整体布局背景色（转为10进制的数，如：#FFFFFF 16进制转为10进制为 16777215）
                        },
                        users: [],
                        images: [{
                            url: 'https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fother%2FLark2.jpeg', //设置背景图片
                            x: 250, // 背景图片x偏移，相对整体布局的左上角
                            y: 390, // 背景图片y偏移，相对整体布局的左上角
                            width: 480, // 背景图片宽度
                            height: 300, //背景图片高度
                            adaption: 1 //自适应，值默认为1
                        }]
                    }
                }]
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
                const uid = evt.uid;
                console.warn(`${uid} 加入房间`);
                this.addRtmpTask(uid);
                this.updateRtmpTask();
            });

            this.client.on('peer-leave', (evt) => {
                console.warn(`${evt.uid} 离开房间`);
                this.remoteStreams = this.remoteStreams.filter(
                    (item) => !!item.getId() && item.getId() !== evt.uid
                );
                this.deleteRtmpTask(evt.uid)
                this.updateRtmpTask()
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
                    (item) => Number(item.dataset.uid) === Number(remoteStream.getId())
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

            // 监听推流任务的状态
            this.client.on('rtmp-state', data => {
                console.warn('=====互动直播状况：', data)
                console.warn(`互动直播推流任务：${data.task_id}，的状态：${data.state}`)
                if (data.state === 505) {
                    console.warn('该推流任务正在推流中，状态正常')
                } else if (data.state === 506) {
                    console.warn('该推流任务推流失败了')
                } else if (data.state === 511) {
                    console.warn('该推流任务推流结束了')
                }
            })

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
                            path: 'push',
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
                        joinChannelLiveConfig: {
                            liveEnable: true, // 开启直播，只有开启直播才能开启推流功能
                        },
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
                        this.addRtmpTask(this.localUid)
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
            addRtmpTask(uid) {
                // 最多只显示两人
                const length = this.rtmpTasks[0].layout.users.length
                if ( length >= 2 ) return
                this.rtmpTasks[0].layout.users.push({
                    ...pushUser,
                    uid: Number(uid),
                    x: length ? 550 : 0,
                    y: 0
                })
            },
            deleteRtmpTask(uid) {
                const leftUsers = this.rtmpTasks[0].layout.users.filter(item => item.uid !== Number(uid))
                leftUsers[0].x = 0;
                this.rtmpTasks[0].layout.users = leftUsers
            },
            updateRtmpTask() {
                if (!this.isPushing) return
                if (!this.client) {
                    throw Error('内部错误，请重新加入房间')
                }
                console.log(this.rtmpTasks)
                this.client.updateTasks({
                    rtmpTasks: this.rtmpTasks
                }).then(() => {
                    console.warn('更新推流任务接口成功')
                }).catch(err => {
                    message('更新推流任务接口失败')
                    console.warn('更新推流任务接口失败: ' + err)
                    if (err === 'INVALID_PARAMETER') {
                        console.warn('参数错误')
                    }
                })
            },
            togglePushStats() {
                if (!this.client) {
                    throw Error('内部错误，请重新加入房间')
                }
                console.log(this.rtmpTasks)
                if (this.isPushing) {
                    this.client.deleteTasks({
                        taskIds: this.rtmpTasks.map(item => item.taskId) //可以同时删除多个推流任务
                    }).then(() => {
                        console.warn('删除推流任务接口调用成功')
                        this.isPushing = false;
                    }).catch(error => {
                        message('删除推流任务接口调用失败')
                        console.warn('删除推流任务接口调用失败: ', error)
                        if (error === 'INVALID_PARAMETER') {
                            console.warn('参数错误')
                        }
                    })
                } else {
                    if (!this.rtmpTasks[0].streamUrl) {
                        message('请填写推流地址，再开始推流！');
                        return
                    }
                    this.client.addTasks({
                        rtmpTasks: this.rtmpTasks
                    }).then(() => {
                        this.isPushing = true;
                        console.warn('添加推流任务接口成功')
                    }).catch(err => {
                        message('添加推流任务接口失败')
                        console.warn('添加推流任务接口失败: ' + err)
                        if (err === 'INVALID_PARAMETER') {
                            console.warn('参数错误')
                        }
                    })
                }
            },
            handleOver() {
                console.warn('离开房间');
                this.client.leave();
                this.returnJoin(1);
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

      //静音
      &.silence {
        background: url("../../assets/img/icon/silence.png") no-repeat center;
        background-size: 60px 54px;

        &:hover {
          background: url("../../assets/img/icon/silence-hover.png") no-repeat
            center;
          background-size: 60px 54px;
        }

        &:active {
          background: url("../../assets/img/icon/silence-click.png") no-repeat
            center;
          background-size: 60px 54px;
        }

        &.isSilence {
          //已经开启静音
          background: url("../../assets/img/icon/relieve-silence.png") no-repeat
            center;
          background-size: 60px 54px;

          &:hover {
            background: url("../../assets/img/icon/relieve-silence-hover.png")
              no-repeat center;
            background-size: 60px 54px;
          }

          &:active {
            background: url("../../assets/img/icon/relieve-silence-click.png")
              no-repeat center;
            background-size: 60px 54px;
          }
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

      // 停止按钮
      &.stop {
        background: url("../../assets/img/icon/stop.png") no-repeat center;
        background-size: 60px 54px;

        &:hover {
          background: url("../../assets/img/icon/stop-hover.png") no-repeat
            center;
          background-size: 60px 54px;
        }

        &:active {
          background: url("../../assets/img/icon/stop-click.png") no-repeat
            center;
          background-size: 60px 54px;
        }

        //已经是停止状态
        &.isStop {
          background: url("../../assets/img/icon/open.png") no-repeat center;
          background-size: 60px 54px;

          &:hover {
            background: url("../../assets/img/icon/open-hover.png") no-repeat
              center;
            background-size: 60px 54px;
          }

          &:active {
            background: url("../../assets/img/icon/open-click.png") no-repeat
              center;
            background-size: 60px 54px;
          }
        }
      }
    }
  }
}
</style>
