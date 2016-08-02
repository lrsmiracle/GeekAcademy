// IControlOtherAppService.aidl
package com.jikexueyuan.aidlcontroller;

// Declare any non-default types here with import statements
import com.jikexueyuan.aidlcontroller.ICallback;

interface IControlOtherAppService {

    void registCallBack(ICallback callBack);
    void unregistCallBack(ICallback callBack);
}
