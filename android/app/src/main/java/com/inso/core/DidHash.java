package com.inso.core;

import com.inso.plugin.tools.L;

import org.hashids.Hashids;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/20
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class DidHash {
    private static final String SALT = "inso_";
    private static final  int MIN_N = 6;
    public static String hashDid(long timeStamp,String mac){
        Hashids hashids = new Hashids(SALT,MIN_N);
        String hashTime = hashids.encode(timeStamp);
        String hashMac = hashids.encodeHex(mac);
        String hashSn = hashids.encode(1234);
        String ret = hashTime+hashMac+hashSn;
        L.d(String.format("%s,%s,%s,%s",hashTime,hashMac,hashSn,ret));
        return ret;
    }
}
