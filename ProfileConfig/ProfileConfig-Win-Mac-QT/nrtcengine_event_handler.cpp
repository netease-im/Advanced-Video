#include "nrtcengine_event_handler.h"
#include "nrtc_engine.h"

NRTCEngine_event_handler::NRTCEngine_event_handler( NRTCEngine * engine)
{
    m_engine = engine;
}

void NRTCEngine_event_handler::onUserVideoStop(uid_t uid){
    
}

void NRTCEngine_event_handler::onJoinChannel(channel_id_t cid, uid_t uid, NERtcErrorCode result, uint64_t elapsed){
   
}
void NRTCEngine_event_handler::onUserJoined(uid_t uid, const char * user_name){
   
}

void NRTCEngine_event_handler::onUserLeft(uid_t uid, NERtcSessionLeaveReason reason){
  
}

