package com.inso.core.bind;

import com.inso.entity.http.post.Bind;
import com.inso.entity.http.post.BindStatus;
import com.inso.entity.http.post.Unbind;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/14
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public interface IBindServer {
    void checkDeviceStatus(BindStatus o);
    void bindDevice(Bind o);
    void unBindDevice(Unbind o);
}
