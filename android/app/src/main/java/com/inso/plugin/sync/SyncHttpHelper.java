package com.inso.plugin.sync;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/11/22
 * @ 描述:
 */
public class SyncHttpHelper {
//    public static SyncHttpHelper instance;
//    public  synchronized  static SyncHttpHelper getInstance(){
//        if (null == instance) {
//            instance = new SyncHttpHelper();
//        }
//        return instance;
//    }
//
//    public  void  pushAlarm(){
//        List<HttpAlarm> list = new ArrayList<>();
//        List<AlarmDao> mDbSource = mDBHelper.getAllAlarm();
//        for(AlarmDao item : mDbSource)
//        {
//            list.add(new HttpAlarm(item.id,item.status ?ON:OFF,item.seconds,item.extend,getListType(item.repeatType),item.desc));
//        }
//        HttpSyncHelper.pushData(
//                new RequestParams(
//                        MODEL,
//                        UID,
//                        DID,
//                        TYPE_USER_INFO,
//                        NORMAL_ALARM_KEY,
//                        AppController.getGson().toJson(list),
//                        TimeUtil.getNowTimeSeconds()), new Callback<JSONArray>() {
//                    @Override
//                    public void onSuccess(JSONArray jsonArray) {
//                        L.e(TAG + "=>pushAlarmInfoToMijia Success::" + jsonArray.toString());
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//                        L.e(TAG + "=>pushAlarmInfoToMijia Error::" + s);
//                    }
//                });
//    }
//
//    private List<Integer> getListType(String type)
//    {
//        List<Integer> ret = new ArrayList<>() ;
//        String[] src = type.split(",");
//        if(src.length==1){
//            if(Integer.parseInt(src[0])<4){
//                ret.add(Integer.parseInt(src[0]));
//                return ret;
//            }else{//等于4
//                for (int i =4;i<9;i++){
//                    ret.add(i);
//                }
//                return ret;
//            }
//        }
//        for(int i = 1;i<src.length;i++)
//        {
//            ret.add(Integer.parseInt(src[i])+4);
//        }
//        return ret;
//    }
}
