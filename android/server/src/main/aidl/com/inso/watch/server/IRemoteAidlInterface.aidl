// IAidlInterface.aidl
package com.inso.watch.server;
import com.inso.watch.server.Person;
// Declare any non-default types here with import statements

interface IRemoteAidlInterface {
    void show(in Person p);
    String getPersonUserName();
    String getPersonUserAge();
    int add(int a,int b);
}
